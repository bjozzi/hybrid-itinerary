
package basic;

import TD.TDActiveNode;
import TD.TDGraph;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {

    public List<stop> Stops = new ArrayList<>();
    private List<Transfer> Transfers = new ArrayList<>();
    private Map<String, List<Stop_times>> StopTimes = new HashMap<>();
    public Map<String, String> stopNames = new ConcurrentHashMap<>();
    public TDGraph g = new TDGraph();
    public TDGraph gReversed = new TDGraph();


    public void main(String path) {
        //path = "D:\\ITU\\4. semestris\\Final Thesis\\gtfs\\";
        CSVParser csv = null;
        int number = 0;
        boolean first = true;
        try {
            /*csv = new CSVParser(path + "stops.txt");

            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                stop s = new stop(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3), csv.getItem(4), csv.getItem(5), csv.getItem(6), csv.getItem(7));
                Stops.add(s);
            }*/

            csv = new CSVParser(path + "transfers.txt");
            first = true;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                Transfer t = new Transfer(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3));
                Transfers.add(t);
            }
            csv = new CSVParser(path + "stop_times.txt");
            first = true;
            String trip_id;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                trip_id = csv.getItem(0);
                Stop_times st = new Stop_times(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3));
                if (!StopTimes.containsKey(trip_id))
                    StopTimes.put(trip_id, new ArrayList<Stop_times>());
                if (!StopTimes.get(trip_id).contains(st.stop_id))
                    StopTimes.get(trip_id).add(st);
            }

           /* try (Stream<String> lines = Files.lines(Paths.get("D:\\ITU\\4. semestris\\Final Thesis\\gtfs\\stops.txt"))) {
                lines.parallel().map(line -> Arrays.asList(line.split(","))).skip(1).forEach(x -> stopNames.put(x.get(0), x.get(2)));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            //  BasicDijkstra(Stops, Transfers, StopTimes);

            //TDijkstra(Stops, Transfers, StopTimes);
            //  TDDDijkstra(Stops, Transfers, StopTimes, stopNames);
            System.out.println("Done with everything");
        } catch (EOFException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void TDijkstra(List<stop> Stops, List<Transfer> Transfers, Map<String, List<Stop_times>> StopTimes) throws ParseException {

        TDGraph g = new TDGraph();

        for (stop s : Stops) {
            g.createNode(s.stop_id, s.stop_name, s.stop_lat, s.stop_lon);
        }


        for (List<Stop_times> ss : StopTimes.values()) {
            for (int j = 0; j < ss.size(); j++) {
                if (j + 1 == ss.size())
                    break;

                Stop_times st_from = ss.get(j);
                Stop_times st_to = ss.get(j + 1);

                double dateTimeFrom = st_from.departure_time;
                double dateTimeTo = st_to.arrival_time;
                double timeBetween = dateTimeTo - dateTimeFrom;
                g.addArc(st_from.stop_id, st_to.stop_id, timeBetween, dateTimeFrom, st_from.trip_id);
            }
        }
        for (Transfer t : Transfers) {
            g.addArc(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
            g.addArc(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
        }

        int TaskCount = 10;
        TDGraph reversedGraph = new TDGraph();
        reversedGraph.setNodes(g.getNodes());
        ExecutorService executor = Executors.newFixedThreadPool(TaskCount);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        Map<String, List<TD.TDArc>> adjacentArcs = g.getAdjacentArcs();
        int sizeOfDirChange = adjacentArcs.size();
        Object _lock = new Object();
        try {
            List<String> keys = new ArrayList(adjacentArcs.keySet());

            for (int i = 0; i < TaskCount; i++) {

                final int from = sizeOfDirChange / TaskCount * i;
                final int to = (i + 1 >= sizeOfDirChange) ? sizeOfDirChange : sizeOfDirChange / TaskCount * (i + 1);

                futures.add(executor.submit(() -> {
                    for (int j = from; j < to; j++) {
                        String fromNode = keys.get(j);
                        List<TD.TDArc> ListOfAdjacentArcs = adjacentArcs.get(fromNode);
                        for (TD.TDArc arc : ListOfAdjacentArcs) {
                            synchronized (_lock) {
                                for (Map.Entry<Double, List<String>> depTimes : arc.accessTime.entrySet()) {
                                    for (String trip_id : depTimes.getValue()) {
                                        reversedGraph.addArc(arc.toID, fromNode, arc.cost, (depTimes.getKey() + arc.cost), trip_id);
                                    }
                                }
                            }
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }

            Date TimeNow = new java.util.Date();
            double timeInMinutes = TimeInMinutes(TimeNow);
        /*String startNode = "000008600858";
        String targetNode = "000008600512";*/
            String startNode = "000000007002";
            String targetNode = "000000001357";
            HashMap<String, TDActiveNode> parents = g.TimeDependentDijkstra(startNode, targetNode, timeInMinutes);
            double endTime = parents.get(targetNode).distance;
            List<Map<String, TDActiveNode>> trees = new ArrayList<>();
            int minutes = 30;
            double time = endTime;
            double toTime = endTime+minutes;


            for (double k = time ; k < toTime; k++){
                HashMap<String, TDActiveNode> par = reversedGraph.ComputeISPT(targetNode, k, timeInMinutes);
                trees.add(par);
            }



            List<Map<String, TDActiveNode>> reduced = TreesContainingStartStation(trees, startNode);
            if (reduced.size() > 0) {
                //send the reduced tree
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BasicDijkstra(List<stop> Stops, List<Transfer> Transfers, Map<String, List<Stop_times>> StopTimes) throws ParseException {

        Graph g = new Graph();

        for (stop s : Stops) {
            float lat = Float.parseFloat(s.stop_lat);
            float lon = Float.parseFloat(s.stop_lon);
            g.createNode(s.stop_id, lat, lon);
        }


        for (List<Stop_times> ss : StopTimes.values()) {
            for (int j = 0; j < ss.size(); j++) {
                if (j + 1 == ss.size())
                    break;

                Stop_times st_from = ss.get(j);
                Stop_times st_to = ss.get(j + 1);

                double dateTimeFrom = st_from.departure_time;
                double dateTimeTo = st_to.arrival_time;
                double timeBetween = dateTimeTo - dateTimeFrom;
                g.addEdge(st_from.stop_id, st_to.stop_id, timeBetween);
            }
        }
        for (Transfer t : Transfers) {
            g.addEdge(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time) / 60);
            g.addEdge(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time) / 60);
        }

        Dijkstra d = new Dijkstra(g);
        Double dk = d.computeShortestPath("000000004030", "000000002613");
        System.out.println(dk + " " + d.shortestPathToString("000000004030", "000000002613"));
    }

    public void createGraph(){

        //TDGraph g = new TDGraph();

        for (stop s : Stops) {
            g.createNode(s.stop_id, s.stop_name, s.stop_lat, s.stop_lon);
        }


        for (List<Stop_times> ss : StopTimes.values()) {
            for (int j = 0; j < ss.size(); j++) {
                if (j + 1 == ss.size())
                    break;

                Stop_times st_from = ss.get(j);
                Stop_times st_to = ss.get(j + 1);

                double dateTimeFrom = st_from.departure_time;
                double dateTimeTo = st_to.arrival_time;
                double timeBetween = dateTimeTo - dateTimeFrom;
                g.addArc(st_from.stop_id, st_to.stop_id, timeBetween, dateTimeFrom, st_from.trip_id);
            }
        }
        for (Transfer t : Transfers) {
            g.addArc(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
            g.addArc(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
        }
    }

    public void reverseGraph(){

        int TaskCount = 10;
        //TDGraph reversedGraph = new TDGraph();
        this.gReversed.setNodes(g.getNodes());
        ExecutorService executor = Executors.newFixedThreadPool(TaskCount);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        Map<String, List<TD.TDArc>> adjacentArcs = g.getAdjacentArcs();
        int sizeOfDirChange = adjacentArcs.size();
        Object _lock = new Object();
        try {
            List<String> keys = new ArrayList(adjacentArcs.keySet());

            for (int i = 0; i < TaskCount; i++) {

                final int from = sizeOfDirChange / TaskCount * i;
                final int to = (i + 1 >= sizeOfDirChange) ? sizeOfDirChange : sizeOfDirChange / TaskCount * (i + 1);

                futures.add(executor.submit(() -> {
                    for (int j = from; j < to; j++) {
                        String fromNode = keys.get(j);
                        List<TD.TDArc> ListOfAdjacentArcs = adjacentArcs.get(fromNode);
                        for (TD.TDArc arc : ListOfAdjacentArcs) {
                            synchronized (_lock) {
                                for (Map.Entry<Double, List<String>> depTimes : arc.accessTime.entrySet()) {
                                    for (String trip_id : depTimes.getValue()) {
                                        this.gReversed.addArc(arc.toID, fromNode, arc.cost, (depTimes.getKey() + arc.cost), trip_id);
                                    }
                                }
                            }
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        }catch (Exception e ){
            e.printStackTrace();
        }


    }



    public static String shortestPathName(String startNodeId, String targetNodeId, Map<String, TDActiveNode> parents, Map<String, String> stopNames) {

        String pathName = "";
        String currentNodeId;

        currentNodeId = targetNodeId;

        pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).distance);
        while (currentNodeId != startNodeId) {
            currentNodeId = parents.get(currentNodeId).parentID;
            pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).distance) + "->" + pathName;
            if (currentNodeId == null)
                break;
        }

        return pathName;
    }

    public static List<Map<String, TDActiveNode>> TreesContainingStartStation(List<Map<String, TDActiveNode>> trees, String nodeID) {
        List<Map<String, TDActiveNode>> containment = new ArrayList<>();
        for (Map<String, TDActiveNode> tree : trees) {
            if (!isInTheList(containment, tree, nodeID))
                containment.add(tree);
        }
        return containment;
    }

    public static boolean isInTheList(List<Map<String, TDActiveNode>> containment, Map<String, TDActiveNode> tree, String nodeID) {
        boolean isIn = false;
        //For checking if in the tree there is the start node - if there is none it returns true and isn't added to the list
        if (!tree.values().stream().anyMatch(x -> x.nodeID.equals(nodeID)))
            return true;
        for (Map<String, TDActiveNode> cont : containment) {
            if (tree.size() != cont.size())
                continue;
            int count = 0;
            for (TDActiveNode node : tree.values()) {

                if (cont.values().stream().parallel().anyMatch(x -> x.nodeID.equals(node.nodeID)
                        && x.parentID.equals(node.parentID)
                        && x.distance == node.distance)) {
                    count++;
                } else
                    break;
            }
            if (count == tree.size()) {
                isIn = true;
                break;
            }
        }

        return isIn;
    }

    public static double TimeInMinutes(Date depTime) {
        double minutes = depTime.getHours() * 60 + depTime.getMinutes() + depTime.getSeconds() / 60;
        return minutes;
    }

    public static String MinutesToTime(double minutes) {
        Double minutesAsInt = Math.floor(minutes);
        Double seconds = (minutes - minutesAsInt) * 60;
        Double fullHours = minutesAsInt / 60;
        Double hours = Math.floor(fullHours);
        Double minute = (fullHours - hours) * 60;

        return hours.intValue() + ":" + minute.intValue() + ":" + seconds.intValue();
    }
}
