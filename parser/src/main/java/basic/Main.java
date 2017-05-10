package basic;

import TimeDependent.TDArc;
import TimeDependent.TDDGraph;
import TimeDependent.TDDijkstra;
import TimeExpanded.TEActiveNode;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        String path = "D:\\ITU\\4. semestris\\Final Thesis\\gtfs\\";
        CSVParser csv = null;
        List<stop> Stops = new ArrayList<>();
        List<Transfer> Transfers = new ArrayList<>();
        Map<String, List<Stop_times>> StopTimes = new HashMap<>();
        int number = 0;
        boolean first = true;
        try {
            csv = new CSVParser(path + "stops.txt");

            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                stop s = new stop(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3), csv.getItem(4), csv.getItem(5), csv.getItem(6), csv.getItem(7));
                Stops.add(s);
            }
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
                Stop_times st = new Stop_times(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3), Integer.parseInt(csv.getItem(4)), Integer.parseInt(csv.getItem(5)), Integer.parseInt(csv.getItem(6)), csv.getItem(7));
                if (!StopTimes.containsKey(trip_id))
                    StopTimes.put(trip_id, new ArrayList<Stop_times>());
                if (!StopTimes.get(trip_id).contains(st.stop_id))
                    StopTimes.get(trip_id).add(st);
            }

            Map<String, String> stopNames = new ConcurrentHashMap<>();
            try (Stream<String> lines = Files.lines(Paths.get("D:\\ITU\\4. semestris\\Final Thesis\\gtfs\\stops.txt"))) {
                lines.parallel().map(line -> Arrays.asList(line.split(","))).skip(1).forEach(x -> stopNames.put(x.get(0), x.get(2)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  BasicDijkstra(Stops, Transfers, StopTimes);
            TDDDijkstra(Stops, Transfers, StopTimes, stopNames);
            System.out.println("Done with everything");
            System.exit(0);
        } catch (EOFException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ParseException e) {
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

    public static void TDDDijkstra(List<stop> Stops, List<Transfer> Transfers, Map<String, List<Stop_times>> StopTimes, Map<String, String> stopNames) throws ParseException {
        TDDGraph g = new TDDGraph();

        for (stop s : Stops) {
            float lat = Float.parseFloat(s.stop_lat);
            float lon = Float.parseFloat(s.stop_lon);
            g.createNode(s.stop_id, lat, lon);
        }

        for (Transfer t : Transfers) {
            g.addEdge(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
            g.addEdge(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time) / 60, -1, "Transfer");
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
                g.addEdge(st_from.stop_id, st_to.stop_id, timeBetween, dateTimeFrom, st_from.trip_id);
            }
        }

        TDDijkstra d = new TDDijkstra(g);
        Date TimeNow = new java.util.Date();
        double timeInMinutes = TimeInMinutes(TimeNow);
        String startNode = "000008600858";
        String targetNode = "000008600512";
        Double dk = d.computeShortestPath(startNode, targetNode, timeInMinutes);
        System.out.println(dk);
        System.out.println(d.shortestPathName(startNode, targetNode, stopNames));
        int TaskCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(TaskCount);
        List<Future<?>> futures = new ArrayList<Future<?>>();
        Map<String, List<TDArc>> adjacentArcs = g.getadjacentArcs();
        Map<String, List<TDArc>> switchedArcs = new HashMap<>();
        List<Map<String, ActiveNode>> trees = new ArrayList<>();
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
                        List<TDArc> ListOfAdjacentArcs = adjacentArcs.get(fromNode);
                        for (TDArc arc : ListOfAdjacentArcs) {
                            synchronized (_lock) {
                                TDArc tdAr = TDArc.createArc(fromNode, arc.getCost(), arc.departureTime + arc.getCost(), arc.tripID);
                                if (switchedArcs.containsKey(arc.getHeadNodeID())) {
                                    switchedArcs.get(arc.getHeadNodeID()).add(tdAr);
                                } else {
                                    List<TDArc> adjArcs = new ArrayList<>();
                                    adjArcs.add(tdAr);
                                    switchedArcs.put(arc.getHeadNodeID(), adjArcs);
                                }
                            }
                        }
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
            g.setAdjacentArcs(switchedArcs);
            futures = new ArrayList<Future<?>>();
            int minutes = 30;
            double time = dk;
            for (int i = 0; i < TaskCount; i++) {

                final int from = minutes / TaskCount * i;
                final int to = (i + 1 >= minutes) ? minutes : minutes / TaskCount * (i + 1);

                futures.add(executor.submit(() -> {

                    final String nodeId = targetNode;
                    for (double k = time + from; k < time + to; k++)
                        synchronized (_lock) {
                            Map<String, ActiveNode> path = d.computeShortestPathTree(nodeId, k, timeInMinutes);
                            trees.add(path);
                        }

                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }

            List<Map<String, ActiveNode>> reduced = TreesContainingStartStation(trees, startNode);


            System.out.println(shortestPathName(targetNode, startNode, reduced.get(0), stopNames));

            PrintWriter writer = new PrintWriter("trees.txt", "UTF-8");


            for (Map<String, ActiveNode> tre : reduced) {
                tre.values().stream().forEach(x -> writer.println(stopNames.get(x.getId()) + ";" + stopNames.get(x.getParent()) + ";" + x.getArrivalTime()));
                writer.println("------------------------");
            }
            writer.close();
            System.out.println(TimeInMinutes(TimeNow));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // do something
        }
    }

    public static String shortestPathName(String startNodeId, String targetNodeId, Map<String, ActiveNode> parents, Map<String, String> stopNames) {

        String pathName = "";
        String currentNodeId;

        currentNodeId = targetNodeId;

        pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).getDist());
        while (currentNodeId != startNodeId) {
            currentNodeId = parents.get(currentNodeId).getParent();
            pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).getDist()) + "->" + pathName;
            if (currentNodeId == null)
                break;
        }

        return pathName;
    }

    public static List<Map<String, ActiveNode>> TreesContainingStartStation(List<Map<String, ActiveNode>> trees, String nodeID) {
        List<Integer> hash = new ArrayList<>();
        double minutes = TimeInMinutes(new java.util.Date());
        List<Map<String, ActiveNode>> containment = new ArrayList<>();
        for (Map<String, ActiveNode> tree : trees) {
            if (!isInTheList(containment, tree))
                containment.add(tree);
        }

        return containment;
    }

    public static boolean isInTheList(List<Map<String, ActiveNode>> containment, Map<String, ActiveNode> tree) {
        boolean toReturn = false;
        if (containment.size() == 0)
            return false;
        else {
            for (Map<String, ActiveNode> cont : containment) {
                if (tree.size() != cont.size())
                    continue;
                int count = 0;
                for (ActiveNode node : tree.values()) {

                    if (cont.values().stream().parallel().anyMatch(x -> x.getId().equals(node.getId())
                            && x.getParent().equals(node.getParent())
                            && x.getTrip_id().equals(node.getTrip_id())
                            && x.getDist().equals(node.getDist()))) {
                        count++;
                    } else
                        break;
                }
                if (count == tree.size()) {
                    toReturn = true;
                    break;
                }
            }
        }
        return toReturn;
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
