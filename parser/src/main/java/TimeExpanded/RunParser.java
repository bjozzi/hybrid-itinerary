package TimeExpanded;

import basic.Node;
import basic.Transfer;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by bjozz on 4/8/2017.
 */

/**
 * Created by bjozz on 4/8/2017.
 */
public class RunParser {
    public  Map<String, ArrayList<NodeOrder>> nodeOrders = new HashMap<>();
    //public  Map<String, ArrayList<StopTime>> transfers = new HashMap<>();
    public TEGraph g = new TEGraph();
    public  Map<String, String> stopNames = new ConcurrentHashMap<>();
    public List<String> stops = Collections.synchronizedList(new ArrayList<String>());
    public HashMap<String, List<TransferPattern>> transferPatterns = new HashMap<>();


    public static Comparator NOcomparator = new Comparator<NodeOrder>() {
        public int compare(NodeOrder o1, NodeOrder o2) {
            if(o1.minute - o2.minute  == 0){
                if(o1.type == o2.type){
                    return 0;
                }
                return  (o1.type - o2.type > 0) ? 1 : -1;
            }
            else if(o1.minute - o2.minute < 0){
                return  -1;
            }else {
                return 1;
            }
        }
    };

    public static void main(String[] args) {
        /*run();
        TEDijkstra d = new TEDijkstra(g);
            //thisted: 000785000100G -> 000787009900G
            String stopId = "000008600858";
            ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(NOcomparator).collect(toList());
            String nodeId = se.get(0).nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, "000008600020G");
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            System.out.println(d.shortestPathToString(nodeId, endNodeId, reader.stopNames));*/
    }

    public void run(String gtfsFeed) {
        int js = 0;
        try {
            long startTime = System.nanoTime();
            GTFSReader reader = new GTFSReader();
            reader.sequential(gtfsFeed);
            long estimatedTime = System.nanoTime() - startTime;
            System.out.println(estimatedTime);
            this.stopNames = reader.stopNames;
            this.stops = reader.stops;
            this.transferPatterns = reader.transferPatterns;

            for (int j = 0; j < reader.stopTimes.size(); j++) {
                if (j + 1 == reader.stopTimes.size())
                    break;

                StopTimes st_from = reader.stopTimes.get(j);
                StopTimes st_to = reader.stopTimes.get(j + 1);

                if (!st_from.tripId.equals(st_to.tripId)) {
                    continue;
                }

                //Date arrivalTime = formatter.parse(st_from.arrival_time);
                //Date departureTime = formatter.parse(st_to.departure_time);

                double dateTimeDeparture = st_from.departure_time;
                double dateTimeArrival = st_to.arrival_time;
                double timeBetween = dateTimeArrival - dateTimeDeparture;

                //TODO: add transfer nodes at same place as each departure node
                //TODO: add connection between transfer node to departure node, weight 0
                //TODO: add transfer node in node order

                String arrivalNodeId = st_to.tripId + j +"_"+ st_to.stopId;
                String departureNodeId = st_from.tripId + j +"_"+ st_from.stopId;
                String transferNodeId = "transDep" + j +"_"+ st_from.stopId;

                g.createNode(arrivalNodeId, st_to.stopId, dateTimeArrival, 1, st_to.tripId);
                g.createNode(transferNodeId, st_from.stopId, dateTimeDeparture, 2, st_from.tripId);
                g.createNode(departureNodeId, st_from.stopId, dateTimeDeparture, 3, st_from.tripId);
                g.addEdge(departureNodeId, arrivalNodeId, timeBetween, st_to.stopId);
                g.addEdge(transferNodeId, departureNodeId, 0, st_from.stopId);

                nodeOrder(st_to.stopId, dateTimeArrival, arrivalNodeId, 1);
                nodeOrder(st_from.stopId, dateTimeDeparture, transferNodeId, 2);
                nodeOrder(st_from.stopId, dateTimeDeparture, departureNodeId, 3);


                //add transfer between station
                if (reader.transfers.containsKey(st_to.stopId) ) {
                    int k = 0;
                    for (Transfer t : reader.transfers.get(st_to.stopId)){
                        String stationTransferNode = "transfer" + k + j + "_" + st_to.stopId;
                        g.createNode(stationTransferNode, st_to.stopId, dateTimeArrival, 2, st_to.tripId);
                        nodeOrder(st_to.stopId, dateTimeArrival, stationTransferNode, 2);
                        //g.addEdge(arrivalNodeId, stationTransferNode, 0, st_to.stopId);

                        String arrivalNodeTransfer = "arrivalTrans"+ k + j + "_" + t.to_stop_id;
                        g.createNode(arrivalNodeTransfer, t.to_stop_id, dateTimeArrival + t.transfer_time, 1, t.to_stop_id);
                        nodeOrder(t.to_stop_id, dateTimeArrival + t.transfer_time, arrivalNodeTransfer, 1);
                        g.addEdge(stationTransferNode, arrivalNodeTransfer, t.transfer_time, t.to_stop_id);
                        k++;
                    }
                }


            }


            /*List<TENode> arrivalNodes = g.getNodes().values().stream().parallel().filter(x -> x.type == 1).collect(toList());
            ExecutorService executor = Executors.newFixedThreadPool(9);
            List<Future<?>> futures = new ArrayList<Future<?>>();
            Object lock = new Object();
            int sizeofArrivalNodes = arrivalNodes.size();
            for (int i = 0; i < 9; i++) {

                final int from = sizeofArrivalNodes/9  * i;
                final int to = (i+1 >= sizeofArrivalNodes) ? sizeofArrivalNodes : sizeofArrivalNodes/9 * (i+1);

                futures.add(executor.submit( () -> {
                    for (int j = from; j < to; j++) {
                        TENode teNode = arrivalNodes.get(j);
                        if (reader.transfers.containsKey(teNode.stopId) ) {
                            ArrayList<StopName> stopTimes1 = reader.transfers.get(teNode.stopId);
                            for (StopName m : stopTimes1) {
                                String stopId = m.stopId;
                                int time = m.time;
                                String nodeId = "transfer"+j+"_"+stopId;
                                synchronized (lock){
                                    g.createNode(nodeId, stopId, teNode.time+time, 3, "0");
                                    g.addEdge(teNode.ID,nodeId, time, stopId);
                                    nodeOrder(stopId, teNode.time+time,nodeId, 3);
                                }
                            }
                        }
                    }
                }));
            }

            for(Future<?> future: futures){
                future.get();
            }
*/


            //creates connections within station for waiting arcs
            int k = 0;
            for (List<NodeOrder> no : nodeOrders.values()) {
                try{
                    ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) no.stream().parallel().sorted(NOcomparator).collect(toList());
                    for (int i = 0; i < se.size(); i++) {
                        if (i + 1 == se.size())
                            break;


                        /*NodeOrder earlier = se.get(i);
                        NodeOrder later = se.get(i + 1);
                        if(earlier.minute > later.minute){
                            System.out.println("LARGERS");
                        }
                        g.addEdge(earlier.nodeId, later.nodeId, later.minute - earlier.minute, later.stopId);*/


                        //TODO: connect nodes in correct order with arrival nodes connecting to departure nodes
                        NodeOrder earlier = se.get(i);
                        //if node == 1, make edge to next transfer node (type 2)
                        if(earlier.type == 1){
                            for (int j = i+1; j < se.size(); j++) {
                                NodeOrder nextNode = se.get(j);
                                if(nextNode.type == 2){
                                    g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute+3, nextNode.stopId);
                                }
                                if (nextNode.type == 3){
                                    g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute, nextNode.stopId);
                                    break;
                                }
                            }
                        }
                        //if node == 2, make edge to each transfer node (type 2) until next departure node(type 3) then make edge to that node and break
                        if(earlier.type == 2){
                            for (int j = i+1; j < se.size(); j++) {
                                NodeOrder nextNode = se.get(j);
                                if(nextNode.type == 2){
                                    g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute+3, nextNode.stopId);
                                }
                                if (nextNode.type == 3){
                                    g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute, nextNode.stopId);
                                    break;
                                }
                            }
                        }
                    }



                }catch (Exception e){
                    System.out.println(k);
                    e.printStackTrace();
                }
                k++;

            }

        } catch (Exception e) {
            System.out.println(js);
            e.printStackTrace();
        }



    }

    private void nodeOrder(String stopId, double minutes, String nodeId, int type) {
        if (nodeOrders.containsKey(stopId)) {
            nodeOrders.get(stopId).add(new NodeOrder(nodeId, minutes, type, stopId));
        } else {
            ArrayList<NodeOrder> no = new ArrayList<>();
            no.add(new NodeOrder(nodeId, minutes, type, stopId));
            nodeOrders.put(stopId, no);
        }
    }

    public static int TimeInMinutes(Date depTime) {
        int minutes = depTime.getHours() * 60 + depTime.getMinutes();
        return minutes;
    }


}



/*
public class RunParser {
    public  Map<String, ArrayList<NodeOrder>> nodeOrders = new HashMap<>();
    public  Map<String, ArrayList<StopName>> transfers = new HashMap<>();
    public TEGraph g = new TEGraph();
    public  Map<String, String> stopNames = new ConcurrentHashMap<>();
    public GTFSReader reader = new GTFSReader();


    public static Comparator NOcomparator = new Comparator<NodeOrder>() {
        public int compare(NodeOrder o1, NodeOrder o2) {
            if(o1.minute - o2.minute  == 0){
                if(o1.type == o2.type){
                    return 0;
                }
                return  (o1.type - o2.type > 0) ? 1 : -1;
            }
            else if(o1.minute - o2.minute < 0){
                return  -1;
            }else {
                return 1;
            }
        }
    };

    public static void main(String[] args) {
        */
/*run();
        TEDijkstra d = new TEDijkstra(g);

            //thisted: 000785000100G -> 000787009900G

            String stopId = "000008600858";
            ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(NOcomparator).collect(toList());
            String nodeId = se.get(0).nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, "000008600020G");
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            System.out.println(d.shortestPathToString(nodeId, endNodeId, reader.stopNames));*//*

    }

    public void run(String gtfsFeed) {
        int js = 0;
        try {
            long startTime = System.nanoTime();

            reader.sequential(gtfsFeed);
            long estimatedTime = System.nanoTime() - startTime;
            System.out.println(estimatedTime);
            stopNames = reader.stopNames;

            for (int j = 0; j < reader.stopTimes.size(); j++) {
                if (j + 1 == reader.stopTimes.size())
                    break;

                StopTimes st_from = reader.stopTimes.get(j);
                StopTimes st_to = reader.stopTimes.get(j + 1);

                if (!st_from.tripId.equals(st_to.tripId)) {
                    continue;
                }

                double dateTimeDeparture = st_from.departure_time;
                double dateTimeArrival = st_to.arrival_time;
                double timeBetween = dateTimeArrival - dateTimeDeparture;

                String departureNodeId = st_from.tripId + j +"_"+ st_from.stopId;
                String arrivalNodeId = st_to.tripId + j +"_"+ st_to.stopId;
                String transferNodeId = "transfer"+ j + "_" + st_from.stopId;

                g.createNode(departureNodeId, st_from.stopId, dateTimeDeparture, 1);
                g.createNode(arrivalNodeId, st_to.stopId, dateTimeArrival, 2);
                //g.createNode(transferNodeId, st_from.stopId, dateTimeDeparture, 3);
                g.addEdge(departureNodeId, arrivalNodeId, timeBetween, st_to.stopId);

                //g.addEdge(transferNodeId, departureNodeId, 0, st_from.stopId);

                nodeOrder(st_from.stopId, dateTimeDeparture, departureNodeId, 2);
                nodeOrder(st_to.stopId, dateTimeArrival, arrivalNodeId, 1);
                //nodeOrder(st_from.stopId, dateTimeDeparture, transferNodeId, 3);

            }


            */
/*List<TENode> arrivalNodes = g.getNodes().values().stream().parallel().filter(x -> x.type == 1).collect(toList());
            ExecutorService executor = Executors.newFixedThreadPool(9);
            List<Future<?>> futures = new ArrayList<Future<?>>();
            Object lock = new Object();
            int sizeofArrivalNodes = arrivalNodes.size();
            for (int i = 0; i < 9; i++) {

                final int from = sizeofArrivalNodes/9  * i;
                final int to = (i+1 >= sizeofArrivalNodes) ? sizeofArrivalNodes : sizeofArrivalNodes/9 * (i+1);

                futures.add(executor.submit( () -> {
                    for (int j = from; j < to; j++) {
                        TENode teNode = arrivalNodes.get(j);
                        ArrayList<StopName> stopTimes1 = reader.transfers.get(teNode.stopId);
                        if (stopTimes1 != null) {
                            for (StopName m : stopTimes1) {
                                String stopId = m.stopId;
                                int time = m.time;
                                String nodeId = "transfer"+j+"_"+stopId;
                                synchronized (lock){
                                    g.createNode(nodeId, stopId, teNode.time+time, 3);
                                    g.addEdge(teNode.ID,nodeId, time, stopId);
                                    nodeOrder(stopId, teNode.time+time,nodeId, 3);
                                }
                            }
                        }
                    }
                }));
            }

            for(Future<?> future: futures){
                future.get();
            }*//*



            //creates connections within station for waiting arcs
            int k = 0;
            for (List<NodeOrder> no : nodeOrders.values()) {
                try{
                    ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) no.stream().parallel().sorted(NOcomparator).collect(toList());
                    for (int i = 0; i < se.size(); i++) {
                        if (i + 1 == se.size())
                            break;

                        NodeOrder earlier = se.get(i);
                        NodeOrder later = se.get(i + 1);
                        if(earlier.minute > later.minute){
                            System.out.println("LARGERS");
                        }
                        g.addEdge(earlier.nodeId, later.nodeId, later.minute - earlier.minute, later.stopId);

                        */
/*NodeOrder node = se.get(i);

                        //add edge from arrival node to next departure and/or transfer node
                        if(node.type == 1){
                            for (int j = i+1; j < se.size(); j++) {
                                NodeOrder nextNode = se.get(j);
                                if(nextNode.type == 1){
                                    break;
                                }
                                //add edge from arrival node to departure node
                                else if(nextNode.type == 2){
                                    g.addEdge(node.nodeId, nextNode.nodeId, nextNode.minute - node.minute, nextNode.stopId);
                                }
                                //add edge from arrival to transfer
                                else if(nextNode.type == 3){
                                    g.addEdge(node.nodeId, nextNode.nodeId, nextNode.minute - node.minute, nextNode.stopId);
                                }
                            }
                        }

                        //add an arc to next transfer node and each departure node until next transfer node
                        if(node.type == 3){
                            //create the waiting chain of waiting arcs between transfer nodes
                            for (int j = i+1; j < se.size(); j++) {
                                NodeOrder nextNode = se.get(j);
                                if(nextNode.type == 2){
                                    g.addEdge(node.nodeId, nextNode.nodeId, nextNode.minute - node.minute, nextNode.stopId);
                                }
                                if(nextNode.type == 3){
                                    g.addEdge(node.nodeId, nextNode.nodeId, nextNode.minute - node.minute, nextNode.stopId);
                                    break;
                                }
                            }
                        }*//*


                    }



                }catch (Exception e){
                    System.out.println(k);
                    e.printStackTrace();
                }
                k++;

            }






            //System.out.println(dk + " " + d.shortestPathToString("000000004030", "000000002613"));

        } catch (Exception e) {
            System.out.println(js);
            e.printStackTrace();
        }



    }

    private void nodeOrder(String stopId, double minutes, String nodeId, int type) {
        if (nodeOrders.containsKey(stopId)) {
            nodeOrders.get(stopId).add(new NodeOrder(nodeId, minutes, type, stopId));
        } else {
            ArrayList<NodeOrder> no = new ArrayList<>();
            no.add(new NodeOrder(nodeId, minutes, type, stopId));
            nodeOrders.put(stopId, no);
        }
    }

    public static int TimeInMinutes(Date depTime) {
        int minutes = depTime.getHours() * 60 + depTime.getMinutes();
        return minutes;
    }


}
*/
