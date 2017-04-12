package basic;

import TimeDependent.TDDGraph;
import TimeDependent.TDDijkstra;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {



    public static void main(String[] args) {
        CSVParser csv = null;
        List<stop> Stops = new ArrayList<>();
        List<Transfer> Transfers = new ArrayList<>();
        Map<String, List<Stop_times>> StopTimes = new HashMap<>();
        int number = 0;
        boolean first = true;
        try {
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\stops.txt");

            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                stop s = new stop(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3), csv.getItem(4), csv.getItem(5), csv.getItem(6), csv.getItem(7));
                Stops.add(s);
            }
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\transfers.txt");
            first = true;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                Transfer t = new Transfer(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3));
                Transfers.add(t);
            }
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\gtfs\\stop_times.txt");
            first = true;
            String trip_id;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                trip_id = csv.getItem(0);
                Stop_times st = new Stop_times(csv.getItem(1), csv.getItem(2), csv.getItem(3), Integer.parseInt(csv.getItem(4)), Integer.parseInt(csv.getItem(5)), Integer.parseInt(csv.getItem(6)), csv.getItem(7));
                if (!StopTimes.containsKey(trip_id))
                    StopTimes.put(trip_id, new ArrayList<Stop_times>());
                if (!StopTimes.get(trip_id).contains(st.stop_id))
                    StopTimes.get(trip_id).add(st);
            }
         //  BasicDijkstra(Stops, Transfers, StopTimes);
            TDDDijkstra(Stops, Transfers, StopTimes);
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
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date dateTimeFrom = formatter.parse(st_from.departure_time);
                Date dateTimeTo = formatter.parse(st_to.arrival_time);
                long timeBetween = dateTimeTo.getTime() - dateTimeFrom.getTime();
                long seconds = timeBetween / 1000;
                g.addEdge(st_from.stop_id, st_to.stop_id, seconds);
            }
        }
        for (Transfer t : Transfers) {
            g.addEdge(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time));
            g.addEdge(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time));
        }

        Dijkstra d = new Dijkstra(g);
        Double dk = d.computeShortestPath("000000004030", "000000002613");
        System.out.println(dk + " " + d.shortestPathToString("000000004030", "000000002613"));
    }

    public static void TDDDijkstra(List<stop> Stops, List<Transfer> Transfers, Map<String, List<Stop_times>> StopTimes) throws ParseException {
        TDDGraph g = new TDDGraph();

        for (stop s : Stops) {
            float lat = Float.parseFloat(s.stop_lat);
            float lon = Float.parseFloat(s.stop_lon);
            g.createNode(s.stop_id, lat, lon);
        }

        for (Transfer t : Transfers) {
            g.addEdge(t.from_stop_id, t.to_stop_id, Double.parseDouble(t.min_transfer_time) /60 , -1);
            g.addEdge(t.to_stop_id, t.from_stop_id, Double.parseDouble(t.min_transfer_time) /60, -1);
        }
        for (List<Stop_times> ss : StopTimes.values()) {
            for (int j = 0; j < ss.size(); j++) {
                if (j + 1 == ss.size())
                    break;

                Stop_times st_from = ss.get(j);
                Stop_times st_to = ss.get(j + 1);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date dateTimeFrom = formatter.parse(st_from.departure_time);
                Date dateTimeTo = formatter.parse(st_to.arrival_time);
                long timeBetween = dateTimeTo.getTime() - dateTimeFrom.getTime();
                long seconds = timeBetween / 1000 /60 ;
                g.addEdge(st_from.stop_id, st_to.stop_id, seconds, TimeInMinutes(dateTimeFrom));
            }
        }


        TDDijkstra d = new TDDijkstra(g);
        Date TimeNow = new java.util.Date();
        System.out.println(TimeNow.getHours()+":"+TimeNow.getMinutes()+":"+TimeNow.getSeconds());
        Double dk = d.computeShortestPath("000000004030", "000000002613", TimeInMinutes(TimeNow));
        String when = MinutesToTime(dk);
        System.out.println(when + " " + d.shortestPathToString("000000004030", "000000002613"));

    }
    public static double TimeInMinutes(Date depTime)
    {
       double minutes =  depTime.getHours() * 60 + depTime.getMinutes() + depTime.getSeconds() / 60;
       return minutes;
    }
    public static String MinutesToTime(double minutes)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Double minutesAsInt = Math.floor(minutes);
        Double seconds = (minutes - minutesAsInt) * 60;
        Double fullhours =minutesAsInt / 60;
        Double hours = Math.floor(fullhours);
        Double minute = (fullhours - hours) * 60;

      /*  Date dateTime = new java.util.Date();
        try {
            dateTime = formatter.parse(hours.intValue()+":"+minute.intValue()+":"+seconds.intValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return hours.intValue()+":"+minute.intValue()+":"+seconds.intValue();
    }
}
