package TimeExpanded;

import basic.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by bjozz on 4/8/2017.
 */
public class RunParser {
    public static Map<String, ArrayList<NodeOrder>> nodeOrders = new HashMap<>();
    public static Map<String, ArrayList<StopTime>> transfers = new HashMap<>();


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
        int js = 0;
        try {
            TEGraph g = new TEGraph();
            long startTime = System.nanoTime();
            GTFSReader reader = new GTFSReader();
            reader.sequential();
            long estimatedTime = System.nanoTime() - startTime;
            System.out.println(estimatedTime);

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

                Date dateTimeFrom = st_from.departure_time;
                Date dateTimeTo = st_to.arrival_time;
                long timeBetween = dateTimeTo.getTime() - dateTimeFrom.getTime();
                long minutes = timeBetween / 1000 / 60;

                String arrivalNodeId = st_to.tripId + j +"_"+ st_to.stopId;
                String departureNodeId = st_from.tripId + j +"_"+ st_from.stopId;

                g.createNode(arrivalNodeId, st_to.stopId, TimeInMinutes(dateTimeFrom), 2);
                g.createNode(departureNodeId, st_from.stopId, TimeInMinutes(dateTimeTo), 1);
                g.addEdge(departureNodeId, arrivalNodeId, minutes, st_to.stopId);

                nodeOrder(st_to.stopId, TimeInMinutes(dateTimeFrom), arrivalNodeId, 1);
                nodeOrder(st_from.stopId, TimeInMinutes(dateTimeTo), departureNodeId, 2);

            }


            List<TENode> arrivalNodes = g.getNodes().values().stream().parallel().filter(x -> x.type == 1).collect(toList());
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
                        if (transfers.containsKey(teNode.stopId) ) {
                            ArrayList<StopTime> stopTimes1 = transfers.get(teNode.stopId);
                            for (StopTime m : stopTimes1) {
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
            }


            int k = 0;
            for (List<NodeOrder> no : nodeOrders.values()) {

                try{
                    ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) no.stream().parallel().sorted(NOcomparator).collect(toList());
                    for (int i = 0; i < se.size(); i++) {
                        if (i + 1 == se.size())
                            break;


                        NodeOrder earlier = se.get(i);
                        NodeOrder later = se.get(i + 1);
                        g.addEdge(earlier.nodeId, later.nodeId, later.minute - earlier.minute, later.stopId);
                    }
                }catch (Exception e){
                    System.out.println(k);
                    e.printStackTrace();
                }
                k++;

            }

            TEDijkstra d = new TEDijkstra(g);

            String stopId = "000000004030";
            ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(NOcomparator).collect(toList());
            String nodeId = se.get(0).nodeId;
            System.out.println(nodeId);
            String dk = d.computeShortestPath(nodeId, stopId, "000000002613");
            System.out.println(dk);
            String endNodeId = dk.split(";")[1];
            System.out.println(d.shortestPathToString(nodeId, endNodeId, reader.stopNames));
            //System.out.println(dk + " " + d.shortestPathToString("000000004030", "000000002613"));

        } catch (Exception e) {
            System.out.println(js);
            e.printStackTrace();
        }



    }

    private static void nodeOrder(String stopId, int minutes, String nodeId, int type) {
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
