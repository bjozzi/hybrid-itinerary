package TimeExpanded;

import basic.Main;
import basic.Node;
import basic.Transfer;
import javafx.util.Pair;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static TimeExpanded.RunParser.NOcomparator;
import static TimeExpanded.RunParser.TimeInMinutes;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Created by bjozz on 4/13/2017.
 */
public class RunParserTest {
    @Test
    public void run() throws Exception {

        String stopId = "000008600858";
        String gtfsFeed = "gtfs";
        String targetNodeId = "000008600620G";

        TEGraph g = new TEGraph();
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);



        //thisted: 000785000100G -> 000787009900G
        //Date yesterday = new Date(System.currentTimeMillis() - 1000L * 60L * 30L * 23L);
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(NOcomparator).collect(toList());
        String nodeId = se.get(0).nodeId;//"419956041041760_000008600858";//
        System.out.println(nodeId);
        String dk = d.computeShortestPath(nodeId, stopId, "000008600620G");//"000008600702G");//"000008600856G");//42027006425959_000008600702G//42026896419811_000008600620G
        System.out.println(dk);
        String endNodeId = dk.split(";")[1];
        System.out.println(g.getNode(nodeId).time);
        System.out.println(g.getNode(endNodeId).time);
        System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
        System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
        System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));

    }


    @Test
    public void DenmarkTestShortestPath() throws Exception {
        String stopId = "000000007002";
        String gtfsFeed = "gtfs";
        String targetNodeId = "000000001357";
        shortestPathVanilla(stopId, gtfsFeed, targetNodeId);
    }
    @Test
    public void IrelandTestShortestPath() throws Exception {
        String stopId = "8220DB001535";
        String gtfsFeed = "gtfsIreland";
        gtfsFeed = "gtfsDublin";
        String targetNodeId = "8220DB000415";
        shortestPathVanilla(stopId, gtfsFeed, targetNodeId);
        //transferPattern(stopId, gtfsFeed, targetNodeId);
    }
    @Test
    public void IcelandTestShortestPath() throws Exception {
        String stopId = "90000018";
        String gtfsFeed = "gtfsIceland";
        String targetNodeId = "90000295";//"90000295";
        shortestPathVanilla(stopId, gtfsFeed, targetNodeId);
        //transferPatterns(stopId, gtfsFeed, targetNodeId);
        //shortestPathTp(stopId, gtfsFeed,targetNodeId);
    }

    private void shortestPathTp(String stopId, String gtfsFeed, String targetStopId) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);

        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.type == 2 ).sorted(NOcomparator).collect(toList());
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            System.out.println(nodeId);
            String dk = d.DijkstraWithHubStations(nodeId, stopId, targetStopId);
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            String dist = dk.split(";")[0];
            System.out.println(g.getNode(nodeId).time);
            System.out.println(g.getNode(endNodeId).time);
            System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
            System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
            System.out.println(g.getNode(endNodeId).time - g.getNode(nodeId).time);

            if(dist.equals("0.0")){
                System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));
                r.transferPatterns.get(endNodeId.split("_")[1]).stream().filter(z->z.endStation.equals(targetStopId)).forEach(tp ->{
                    tp.transferPattern.stream().forEach(z-> System.out.println(r.stopNames.get(z)));
                    tp.startTimes.stream().forEach(s -> System.out.println(s));
                });
            }else{
                System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));
            }
        }
    }


    private void transferPatterns(String stopIdd, String gtfsFeed, String targetNodeIdd) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);
        List<List<TransferPattern>> patternsPerStation = new ArrayList<>();


        //list of all stations
        ArrayList<String> targetStationsIds = new ArrayList<>();
        r.nodeOrders.forEach((x,y) -> targetStationsIds.add(x));

        for(String stopId : getHubStations()){
            System.out.println(stopId);
            ArrayList<NodeOrder> fromNodes = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.type == 3 ).collect(toList());
            for(String targetStationId : targetStationsIds){
                if(!targetStationId.equals(stopId)){
                    List<TransferPattern> path = new ArrayList<>();
                    ArrayList<String> stationsFound = d.setDijkstra(fromNodes, stopId, targetStationId);
                    for(String targetNode : stationsFound){
                        TransferPattern p = d.transferPattern(stopId, targetNode);
                        path.add(p);
                        /*boolean contains = false;
                        for(TransferPattern tp : path){
                            if(tp.transferPattern.equals(p.transferPattern)){
                                contains = true;
                            }
                        }
                        if(!contains){
                            path.add(p);
                        }*/
                        //System.out.println(p.transferPattern.toString());
                    }
                    patternsPerStation.add(path);
                }
            }
        }

        Path file = Paths.get("C:\\Users\\bjozz\\Documents\\TransferPatterns25-5.txt");

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(file))
        {
            for (List<TransferPattern> patterns : patternsPerStation){
                double previousTime = 0;
                patterns = patterns.stream().sorted((n1, n2) -> {
                    if(n1.endTime > n2.endTime) return 1;
                    else if( n2.endTime > n1.endTime) return -1;
                    else return 0;
                }).collect(toList());
                Set<List<String>> establishedPatterns = new HashSet<>();
                HashMap<ArrayList<String>, TransferPattern> toPrint = new HashMap<>();
                for(TransferPattern p : patterns){
                    if(previousTime >= p.startTime){
                        //patterns.remove(p);
                        p.transferPattern.stream().forEach( x -> System.out.print(r.stopNames.get(x) + "-->" ));
                    }else{
                        if(!establishedPatterns.contains(p.transferPattern)){
                            p.timeOrders.add(p.timeOrder);
                            toPrint.put(p.transferPattern, p);
                            establishedPatterns.add(p.transferPattern);
                        }else {
                            toPrint.get(p.transferPattern).timeOrders.add(p.timeOrder);
                        }
                        previousTime = p.startTime;
                    }
                }
                toPrint.forEach((key, p) -> {
                    try {
                        //writer.write(p.startTime + "," +p.endTime + "," + p.transferTime+",");
                        p.transferPattern.stream().forEach( x -> {
                            try {
                                writer.write(x+";");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        writer.write(",");
                        p.timeOrders.stream().forEach(x->{
                            try {
                                writer.write(x.toString()+";");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        writer.write("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //patternsPerStationToFile(patternsPerStation);

        /*path.stream().sorted((n1, n2) -> {
            if(n1.startTime > n2.startTime) return 1;
            else if( n2.startTime > n1.startTime) return -1;
            else return 0;
        }).forEach(p -> {
            System.out.print(Main.MinutesToTime(p.startTime) + " " +Main.MinutesToTime(p.endTime) + " : ");
            p.transferPattern.stream().forEach( x -> System.out.print(r.stopNames.get(x) + "-->" ));
            System.out.println("\n");
        });*/

        /*double prevTime = 0;
        for(TransferPattern p : path.stream().sorted((p1, p2) -> p1.endTime - p2.endTime >= 1 ? 1 : -1 ).collect(toList())){
            if(prevTime > p.startTime){
                path.remove(p);
            }else{
                System.out.print(Main.MinutesToTime(p.startTime) + " " +Main.MinutesToTime(p.endTime) + " : ");
                p.transferPattern.stream().forEach(x -> {
                    System.out.println(r.stopNames.get(x) + "<-->");
                });

                prevTime = p.startTime;
            }
        }*/
    }


    private void shortestPathVanilla(String stopId, String gtfsFeed, String targetNodeId) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);

        int i = 0;
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.type == 3 ).filter(x->x.minute >= TimeInMinutes(new Date())).sorted(NOcomparator).collect(toList());
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, targetNodeId);
            System.out.println(dk);
            String endNodeId = dk;
            try{
                System.out.println(g.getNode(nodeId).time);
                System.out.println(g.getNode(endNodeId).time);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
            System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
            System.out.println(g.getNode(endNodeId).time - g.getNode(nodeId).time);
            System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));


            List<ArrayList<String>> path = new ArrayList<>();
            path.add(d.transferPatternToString(nodeId, endNodeId, r.stopNames));

            for(ArrayList<String> p : path){
                System.out.println(path);
            }

            i++;
            if(i>25) break;
        }
    }

    @Test
    public void hub() throws Exception {
            getHubStations();
    }

    private ArrayList<String> getHubStations(){
        ArrayList<String> hubStations = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\bjozz\\Desktop\\gtfsIceland\\10Hubs.txt"))) {
            lines.forEach(x-> hubStations.add(x));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hubStations;
    }


    private void transferPattern(String stopId, String gtfsFeed, String targetNodeId) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);
        List<List<TransferPattern>> patternsPerStation = new ArrayList<>();
        Map<String, ArrayList<NodeOrder>> gaur = r.nodeOrders;

        ArrayList<String> targetStationsIds = new ArrayList<>();
        r.nodeOrders.forEach((x,y) -> targetStationsIds.add(x));

        ArrayList<String> hubs = getHubStations();
        hubs.stream().forEach(hubStopId -> {
            final ArrayList<NodeOrder> nodeOrders = (ArrayList<NodeOrder>) r.nodeOrders.get(hubStopId).stream().filter(x -> x.type == 3).sorted(NOcomparator).collect(toList());
            targetStationsIds.stream().forEach(targetStation -> {
                System.out.println(hubStopId + " -> " + targetStation);
                List<TransferPattern> path = new ArrayList<>();
                double prevTime = 0;
                for (NodeOrder n : nodeOrders) {
                    if (!hubStopId.equals(targetStation)) {
                        String endNodeId = d.computeShortestPath(n.nodeId, hubStopId, targetStation);
                        if(!endNodeId.equals("")){
                            TransferPattern p = d.transferPattern(n.nodeId, endNodeId);
                            boolean contains = false;
                            if(prevTime <= p.startTime){
                                for(TransferPattern tp : path){
                                    if(tp.transferPattern.equals(p.transferPattern)){
                                        contains = true;
                                    }
                                }
                                if(!contains){
                                    path.add(p);
                                    /*p.transferPattern.stream().forEach(s -> System.out.print(r.stopNames.get(s) + " - "));
                                    System.out.println("\n");*/
                                }
                            }
                            prevTime = p.startTime;

                        }
                    }
                }
                patternsPerStation.add(path);
            });
        });


        for (List<TransferPattern> patterns : patternsPerStation){
            for(TransferPattern pattern : patterns){
                pattern.transferPattern.stream().forEach(x -> System.out.print(x + " - "));
                System.out.println("\n");
            }
        }
        patternsPerStationToFile(patternsPerStation);





        /*for (List<TransferPattern> path : patternsPerStation){
            double prevTime = 0;
            for(TransferPattern p : path.stream().sorted((p1, p2) -> p1.endTime - p2.endTime >= 1 ? 1 : -1 ).collect(toList())){
                if(prevTime > p.startTime){
                    path.remove(p);
                }else{
                    System.out.print(Main.MinutesToTime(p.startTime) + " " +Main.MinutesToTime(p.endTime) + " : ");
                        p.transferPattern.stream().forEach(x -> System.out.print(r.stopNames.get(x) +"--" ));
                        System.out.println("\n");
                    prevTime = p.startTime;
                }
            }
        }*/

    }

    private void patternsPerStationToFile(List<List<TransferPattern>> patternsPerStation) {
        Path file = Paths.get("C:\\Users\\bjozz\\Documents\\output.txt");
        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(file))
        {
            try {
                for (List<TransferPattern> path : patternsPerStation){
                    for(TransferPattern p : path){
                        System.out.print(Main.MinutesToTime(p.startTime) + " " +Main.MinutesToTime(p.endTime) + " : ");
                        writer.write(p.startTime + ","+p.endTime+","+p.transferTime+",");
                        p.transferPattern.stream().forEach(x -> {
                            try {
                                writer.write(x + ";");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        writer.write("\n");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    } catch (IOException e) {
            e.printStackTrace();
        }
    }
}