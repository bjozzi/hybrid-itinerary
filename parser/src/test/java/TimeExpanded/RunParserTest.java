package TimeExpanded;

import basic.Main;
import basic.Node;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
        String stopId = "000008600858";
        String gtfsFeed = "gtfs";
        String targetNodeId = "000008600620G";
        shortestPath(stopId, gtfsFeed, targetNodeId);
    }
    @Test
    public void IrelandTestShortestPath() throws Exception {
        String stopId = "839000051";
        String gtfsFeed = "gtfsIreland";
        String targetNodeId = "839000016";
        //shortestPath(stopId, gtfsFeed, targetNodeId);
        transferPattern(stopId, gtfsFeed, targetNodeId);
    }
    @Test
    public void IcelandTestShortestPath() throws Exception {
        String stopId = "90000317";
        String gtfsFeed = "gtfsIceland";
        String targetNodeId = "90000019";
        //shortestPath(stopId, gtfsFeed, targetNodeId);
        transferPattern(stopId, gtfsFeed, targetNodeId);
    }


    private void shortestPath(String stopId, String gtfsFeed, String targetNodeId) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);

        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.type == 2 ).sorted(NOcomparator).collect(toList());
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, targetNodeId);
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            System.out.println(g.getNode(nodeId).time);
            System.out.println(g.getNode(endNodeId).time);
            System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
            System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
            System.out.println(g.getNode(endNodeId).time - g.getNode(nodeId).time);
            System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));


            List<ArrayList<String>> path = new ArrayList<>();
            path.add(d.transferPatternToString(nodeId, endNodeId, r.stopNames));

            for(ArrayList<String> p : path){
                System.out.println(path);
            }
        }
    }


    private void transferPattern(String stopId, String gtfsFeed, String targetNodeId) {
        TEGraph g;
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);

        List<TransferPattern> path = new ArrayList<>();
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.type == 2 ).sorted(NOcomparator).collect(toList());
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, targetNodeId);
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            /*System.out.println(g.getNode(nodeId).time);
            System.out.println(g.getNode(endNodeId).time);
            System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
            System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
            System.out.println(g.getNode(endNodeId).time - g.getNode(nodeId).time);
            System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));*/

            path.add(d.transferPattern(nodeId, endNodeId));
        }

        double prevTime = 0;
        for(TransferPattern p : path.stream().sorted((p1, p2) -> p1.endTime - p2.endTime > 1 ? 1 : -1 ).collect(toList())){
            if(prevTime > p.startTime){
                path.remove(p);
                prevTime = 0;
                break;
            }
            System.out.print(Main.MinutesToTime(p.startTime) + " " +Main.MinutesToTime(p.endTime) + " : ");
            p.transferPattern.stream().forEach(x -> System.out.print(r.stopNames.get(x) +" " ));
            System.out.println("\n");
            prevTime = p.startTime;
        }
    }



    @Test
    public void IrelandTransferPattern() throws Exception {
        String gtfsFeed = "gtfsIreland";
        transferPattern(gtfsFeed);
    }


    @Test
    public void IcelandTransferPattern() throws Exception {
        String gtfsFeed = "gtfsIceland";
        transferPattern(gtfsFeed);
    }

    @Test
    public void DenmarkTransferPattern() throws Exception {
        String gtfsFeed = "gtfsIceland";
        transferPattern(gtfsFeed);
    }


    private void transferPattern(String gtfsFeed) {
        TEGraph g = new TEGraph();
        RunParser r = new RunParser();
        r.run(gtfsFeed);
        g = r.g;

        String arbaejarskoli = "90000317";
        String artun = "90000019";

        //thisted: 000785000100G -> 000787009900G


        List<ArrayList<String>> path = new ArrayList<>();
        TEDijkstra dijkstra = new TEDijkstra(g);
        System.out.println("transfer patterns started");
        //TODO: Find transfer pattern between árbæjarskóli and Ártún
        //TODO: Go through all transfer nodes in Árbæjarskóli
        //TODO: find paths from all nodes in Árbæjarskóli to Ártún
        //TODO: go through all nodes in Árbæjarskóli and Ártún and find transfer patterns between them
        //for(String stopId : r.stops){
            try{
                //String stopId = "839000051";
                List<NodeOrder> nodesInStop = r.nodeOrders.get(arbaejarskoli).stream().filter(x -> x.type == 2).collect(Collectors.toList());
                List<NodeOrder> nodesInArtun = r.nodeOrders.get(artun).stream().filter(x -> x.type == 3).collect(Collectors.toList());
                if(nodesInStop != null){

                    for (NodeOrder nodeOrder : nodesInStop){
                        try{
                            String shortestNode = dijkstra.computeShortestPath(nodeOrder.nodeId,arbaejarskoli, artun).split(";")[1];
                            System.out.println(dijkstra.shortestPathToString(nodeOrder.nodeId, shortestNode, r.stopNames));
                            //path.add(dijkstra.transferPattern(nodeOrder.nodeId, shortestNode, r.stopNames));
                        }catch (Exception e){
                            continue;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        //}

        Path file = Paths.get("C:\\Users\\bjozz\\Documents\\output.txt");

            //Use try-with-resource to get auto-closeable writer instance
            try (BufferedWriter writer = Files.newBufferedWriter(file))
            {
                for (ArrayList<String> node : path) {
                    node.stream().forEach(x -> {
                        try {
                            writer.write(x);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    writer.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Test
    public void printGraph() throws Exception {
        TEGraph g = new TEGraph();
        RunParser r = new RunParser();
        r.run("gtfs");
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);



        //thisted: 000785000100G -> 000787009900G



        ArrayList<NodeOrder> n = r.nodeOrders.get("000008600858");
        String stopId = "000008600858";
        String nodeId = "419956951042729_000008600858";
        NodeOrder se =  r.nodeOrders.get(stopId).stream().parallel().reduce((a,b)-> a.minute < b.minute ? a:b).get();


        Queue<String> arcStack = new ConcurrentLinkedQueue<>();
        arcStack.add("7.MF-BH.3-975-y11-3.11.O16_8290B1358002");

        while (!arcStack.isEmpty()){
            String nodeString = arcStack.poll();
            List<TEArc> arcsAdj = g.getadjacentArc(nodeString);//(nodeString).stream().filter(x -> x.stopId == stopId).collect(toList());
            if(arcsAdj != null){
                arcsAdj.stream().forEach(x->arcStack.add(x.headNodeID));
            }else{
                System.out.println( " ------------------------------------------------- " );
                //break;
            }
            TENode teNode = g.getNode(nodeString);
            System.out.println("nodeID:" + nodeString + " Time: " + teNode.time + " type:" + teNode.type );
        }

    }


    @Test
    public void printGraphIreland() throws Exception {
        TEGraph g = new TEGraph();
        RunParser r = new RunParser();
        r.run("gtfsIreland");
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);



        //thisted: 000785000100G -> 000787009900G


        Queue<String> arcStack = new ConcurrentLinkedQueue<>();
        arcStack.add("7.MF-BH.3-975-y11-3.11.O16_8290B1358002");

        while (!arcStack.isEmpty()){
            String nodeString = arcStack.poll();
            List<TEArc> arcsAdj = g.getadjacentArc(nodeString);//(nodeString).stream().filter(x -> x.stopId == stopId).collect(toList());
            if(arcsAdj != null){
                arcsAdj.stream().forEach(x->arcStack.add(x.headNodeID));
            }else{
                System.out.println( " ------------------------------------------------- " );
                //break;
            }
            TENode teNode = g.getNode(nodeString);
            System.out.println("nodeID:" + nodeString + " Time: " + teNode.time + " type:" + teNode.type );
        }

    }
    /*List<ArrayList<String>> path = new ArrayList<>();
            TEDijkstra dijkstra = new TEDijkstra(g);
            System.out.println("transfer patterns started");
            for(String stopId : reader.stops){
                try{
                    //String stopId = "839000051";
                    List<NodeOrder> nodesInStop = nodeOrders.get(stopId).stream().filter(x -> x.type == 1).collect(Collectors.toList());
                    if(nodesInStop != null){
                        Map<String, TEActiveNode> paths = dijkstra.setDijkstra(nodesInStop, stopId);

                        nodesInStop.stream().forEach( nodeOrder -> {
                            paths.keySet().stream().filter(x -> !x.contains("transfer"))
                                    .forEach(x -> path.add(dijkstra.transferPattern(nodeOrder.nodeId, x, stopNames)));
                        });
                    }
                }catch (Exception e){
                    //e.printStackTrace();
                }
            }
            for (ArrayList<String> node : path){
                node.stream().forEach(x -> System.out.print(x + " "));
                System.out.println("---------");
            }
*/


}