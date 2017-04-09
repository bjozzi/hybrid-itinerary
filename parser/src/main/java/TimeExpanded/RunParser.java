package TimeExpanded;

import basic.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bjozz on 4/8/2017.
 */
public class RunParser {
    public static Map<String, ArrayList<NodeOrder>> nodeOrders = new HashMap<>();
    public static Map<String, ArrayList<StopTime>> transfers = new HashMap<>();

    public static Comparator NOcomparator = new Comparator<NodeOrder>() {
        public int compare(NodeOrder o1, NodeOrder o2) {
            if(o1.minute - o2.minute  == 0){
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

        CSVParser csv = null;
        boolean first = false;
        ArrayList<StopTimes> stopTimes = new ArrayList<>();
        ArrayList<String> weekday = new ArrayList<>(), saturday = new ArrayList<>(), sunday = new ArrayList<>(), trips = new ArrayList<>();
        int js = 0;
        try {
            TEGraph g = new TEGraph();

            //read stop times and fill in map of stopTimes
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\calendar.txt");
            first = true;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                if(csv.getItem(1).equals("1") || csv.getItem(2).equals("1") || csv.getItem(3).equals("1") || csv.getItem(4).equals("1") || csv.getItem(5).equals("1")) weekday.add(csv.getItem(0));
                if(csv.getItem(6).equals("1")) saturday.add(csv.getItem(0));
                if(csv.getItem(7).equals("1")) sunday.add(csv.getItem(0));
            }

            //read stop times and fill in map of stopTimes
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\trips.txt");
            first = true;
            while (csv.readNextLine()) {
                js++;
                if (first) {
                    first = false;
                    continue;
                }
                if(weekday.contains(csv.getItem(1))){
                    trips.add(csv.getItem(2));
                }
            }

            //read stop times and fill in map of stopTimes
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\stop_times.txt");
            first = true;
            String trip_id;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                trip_id = csv.getItem(0);
                if(trips.contains(trip_id)){
                    StopTimes st = new StopTimes(csv.getItem(1), csv.getItem(2), csv.getItem(3), Integer.parseInt(csv.getItem(4)), Integer.parseInt(csv.getItem(5)), Integer.parseInt(csv.getItem(6)), csv.getItem(7));
                    st.tripId = trip_id;
                    stopTimes.add(st);
                }
            }

            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\transfers.txt");
            first = true;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                Transfer t = new Transfer(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3));
                fillTransfer(t);
            }


            for (int j = 0; j < stopTimes.size(); j++) {
                if (j + 1 == stopTimes.size())
                    break;

                StopTimes st_from = stopTimes.get(j);
                StopTimes st_to = stopTimes.get(j + 1);

                if (!st_from.tripId.equals(st_to.tripId)) {
                    continue;
                }

                //Date arrivalTime = formatter.parse(st_from.arrival_time);
                //Date departureTime = formatter.parse(st_to.departure_time);

                Date dateTimeFrom = st_from.departure_time;
                Date dateTimeTo = st_to.arrival_time;
                long timeBetween = dateTimeTo.getTime() - dateTimeFrom.getTime();
                long minutes = timeBetween / 1000 / 60;

                String arrivalNodeId = st_to.tripId + j + st_to.stopId;
                String departureNodeId = st_from.tripId + j + st_from.stopId;

                g.createNode(st_from.tripId, st_to.stopId, TimeInMinutes(dateTimeFrom), 2);
                g.createNode(st_to.tripId, st_from.stopId, TimeInMinutes(dateTimeTo), 1);
                g.addEdge(departureNodeId, arrivalNodeId, minutes);

                nodeOrder(st_to.stopId, TimeInMinutes(dateTimeFrom), arrivalNodeId, 1);
                nodeOrder(st_from.stopId, TimeInMinutes(dateTimeTo), departureNodeId, 2);


                if (transfers.containsKey(st_from.stopId)) {
                    ArrayList<StopTime> stopTimes1 = transfers.get(st_from.stopId);
                    for (StopTime m : stopTimes1) {
                        String stopId = m.stopId;
                        int time = m.time;
                        g.createNode("transfer", stopId, TimeInMinutes(dateTimeFrom)+time, 3);
                        g.addEdge(arrivalNodeId,"transfer"+j+ stopId, time);
                        nodeOrder(stopId, TimeInMinutes(dateTimeFrom)+time,"transfer"+j+ stopId, 3);
                    }
                }

            }

            int k = 0;
            for (List<NodeOrder> no : nodeOrders.values()) {
                if(k == 97){
                    int sdf = 0;
                    System.out.println(sdf);
                }

                try{
                    ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) no.stream().parallel().sorted(NOcomparator).collect(Collectors.toList());
                    for (int i = 0; i < se.size(); i++) {
                        if (i + 1 == se.size())
                            break;


                        NodeOrder earlier = se.get(i);
                        NodeOrder later = se.get(i + 1);
                        g.addEdge(earlier.stopId, later.stopId, later.minute - earlier.minute);


                    }
                }catch (Exception e){
                    System.out.println(k);
                    e.printStackTrace();
                }
                k++;

            }

            TEDijkstra d = new TEDijkstra(g);

            Double dk = d.computeShortestPath("000000004030", "000000002613", TimeInMinutes(new java.util.Date()));
            System.out.println(dk + " " + d.shortestPathToString("000000004030", "000000002613"));

        } catch (Exception e) {
            System.out.println(js);
            e.printStackTrace();
        }



    }

    private static void fillTransfer(Transfer t) {
        if (transfers.containsKey(t.from_stop_id)) {
            transfers.get(t.from_stop_id).add(new StopTime(t.to_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
        } else {
            ArrayList<StopTime> hm = new ArrayList<>();
            hm.add(new StopTime(t.to_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
            transfers.put(t.from_stop_id, hm);
        }
        if (transfers.containsKey(t.to_stop_id)) {
            transfers.get(t.to_stop_id).add(new StopTime(t.from_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
        } else {
            ArrayList<StopTime> hm = new ArrayList<>();
            hm.add(new StopTime(t.from_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
            transfers.put(t.to_stop_id, hm);
        }
    }

    private static void nodeOrder(String stopId, int minutes, String nodeId, int type) {
        if (nodeOrders.containsKey(stopId)) {
            nodeOrders.get(stopId).add(new NodeOrder(nodeId, minutes, type));
        } else {
            ArrayList<NodeOrder> no = new ArrayList<>();
            no.add(new NodeOrder(nodeId, minutes, type));
            nodeOrders.put(stopId, no);
        }
    }

    public static int TimeInMinutes(Date depTime) {
        int minutes = depTime.getHours() * 60 + depTime.getMinutes();
        return minutes;
    }


}
