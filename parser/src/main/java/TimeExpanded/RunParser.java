package TimeExpanded;

import basic.Node;
import basic.Transfer;
import basic.stop;

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
    public List<stop> stops = Collections.synchronizedList(new ArrayList<stop>());
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
            int edges = 0;
            if(gtfsFeed.contains("Iceland")){

            }

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

                    String arrivalNodeId = st_to.tripId + j +"_"+ st_to.stopId;
                    String departureNodeId = st_from.tripId + j +"_"+ st_from.stopId;
                    String transferNodeId = "transDep" + j +"_"+ st_from.stopId;

                    g.createNode(arrivalNodeId, st_to.stopId, dateTimeArrival, 1, st_to.tripId);
                    g.createNode(transferNodeId, st_from.stopId, dateTimeDeparture, 2, st_from.tripId);
                    g.createNode(departureNodeId, st_from.stopId, dateTimeDeparture, 3, st_from.tripId);
                    g.addEdge(departureNodeId, arrivalNodeId, timeBetween, st_to.stopId);
                    g.addEdge(transferNodeId, departureNodeId, 0, st_from.stopId);
                    edges += 2;

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
                            edges++;
                            k++;
                        }
                    }


                }



                //creates connections within station for waiting arcs
                for (List<NodeOrder> no : nodeOrders.values()) {
                    try{
                        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) no.stream().parallel().sorted(NOcomparator).collect(toList());
                        for (int i = 0; i < se.size(); i++) {
                            if (i + 1 == se.size())
                                break;

                            NodeOrder earlier = se.get(i);
                            //if node == 1, make edge to next transfer node (type 2)
                            if(earlier.type == 1){
                                for (int j = i+1; j < se.size(); j++) {
                                    NodeOrder nextNode = se.get(j);
                                    if(nextNode.type == 2){
                                        g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute+3, nextNode.stopId);
                                        edges++;
                                    }
                                    if (nextNode.type == 3){
                                        g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute, nextNode.stopId);
                                        edges++;
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
                                        edges++;
                                    }
                                    if (nextNode.type == 3){
                                        g.addEdge(earlier.nodeId, nextNode.nodeId, nextNode.minute - earlier.minute+3, nextNode.stopId);
                                        edges++;
                                        break;
                                    }
                                }
                            }
                        }



                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            System.out.println("edges: " + edges);
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