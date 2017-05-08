package TimeExpanded;

import basic.CSVParser;
import basic.Transfer;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by bjozz on 4/11/2017.
 */
public class GTFSReader {

    //public  List<String> trips = Collections.synchronizedList(new ArrayList<String>());
    private  Set trips = Collections.synchronizedSet(new HashSet<String>());
    private  Set weekday = Collections.synchronizedSet(new HashSet<String>());
    private  Set saturday = Collections.synchronizedSet(new HashSet<String>());
    private  Set sunday = Collections.synchronizedSet(new HashSet<String>());
    public  List<StopTimes> stopTimes = Collections.synchronizedList(new ArrayList<StopTimes>());
    public  Map<String, ArrayList<Transfer>> transfers = new ConcurrentHashMap<>();
    public  Map<String, String> stopNames = new ConcurrentHashMap<>();
    public List<String> stops = Collections.synchronizedList(new ArrayList<String>());
    public HashMap<String, List<TransferPattern>> transferPatterns = new HashMap<>();


public void sequential(String gtfsFeed) {
    CSVParser csv = null;
    try {

        boolean first = true;
        int js = 0;
        if(!gtfsFeed.contains("Iceland")){
            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\"+gtfsFeed+"\\calendar.txt");
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                if(csv.getItem(1).equals("1") || csv.getItem(2).equals("1") || csv.getItem(3).equals("1") || csv.getItem(4).equals("1") || csv.getItem(5).equals("1"))
                    weekday.add(csv.getItem(0));
                if(csv.getItem(6).equals("1"))
                    saturday.add(csv.getItem(0));
                if(csv.getItem(7).equals("1"))
                    sunday.add(csv.getItem(0));
            }
        }


        //read stop times and fill in map of stopTimes
        csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\"+gtfsFeed+"\\trips.txt");
        first = true;
        while (csv.readNextLine()) {
            js++;
            if (first) {
                first = false;
                continue;
            }
            if(weekday.contains(csv.getItem(1)) || gtfsFeed.contains("Iceland")){
                trips.add(csv.getItem(2));
            }
        }

        //read stop times and fill in map of stopTimes
        csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\" + gtfsFeed + "\\stop_times.txt");
        first = true;
        String trip_id;
        while (csv.readNextLine()) {
            if (first) {
                first = false;
                continue;
            }
            trip_id = csv.getItem(0);
            if(trips.contains(trip_id)){
                StopTimes st = new StopTimes(trip_id, csv.getItem(1), csv.getItem(2), csv.getItem(3), Integer.parseInt(csv.getItem(4)));
                st.tripId = trip_id;
                stopTimes.add(st);
            }
        }


        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\bjozz\\Desktop\\" + gtfsFeed + "\\stops.txt"))) {
            lines.parallel().map(line -> Arrays.asList(line.split(","))).skip(1).forEach(x->{
                if(gtfsFeed.contains("Ireland") ||gtfsFeed.contains("Dublin") ){
                    stopNames.put(x.get(0).replace("\"", ""), x.get(1).replace("\"", ""));
                    stops.add(x.get(0).replace("\"", ""));
                }else{
                    stopNames.put(x.get(0), x.get(2));
                    stops.add(x.get(0));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


            csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\" + gtfsFeed + "\\transfers.txt");
            first = true;
            while (csv.readNextLine()) {
                if (first) {
                    first = false;
                    continue;
                }
                Transfer t = new Transfer(csv.getItem(0), csv.getItem(1), csv.getItem(2), csv.getItem(3));
                t.transfer_time = Double.parseDouble(t.min_transfer_time)/60;
                fillTransfer(t, transfers);
            }


        try(Stream<String> lines = Files.lines(Paths.get("C:\\Users\\bjozz\\Documents\\TransferPatterns2.txt"))){
                lines.parallel().map(line -> Arrays.asList(line.split("-"))).forEach( x ->{
                    TransferPattern p = new TransferPattern(x.get(0), x.get(1));
                    if(transferPatterns.containsKey(p.startStation)){
                        transferPatterns.get(p.startStation).add(p);
                    }else {
                        List<TransferPattern> pattern = new ArrayList<TransferPattern>();
                        pattern.add(p);
                        transferPatterns.put(p.startStation, pattern);
                    }
                });
        }



    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (EOFException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public  void paralell() {
        long startTime = System.nanoTime();

        //ArrayList<StopTimes> stopTimes = new ArrayList<>();
        //ArrayList<String> weekday = new ArrayList<>(), saturday = new ArrayList<>(), sunday = new ArrayList<>(), trips = new ArrayList<>();

        parseFile("calendar.txt", calendarFunction);
        parseFile("trips.txt", tripsFunction);
        //parseFile("stop_times.txt", stopTimesFunction);
        System.out.println(System.nanoTime() - startTime);
        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\bjozz\\Desktop\\gtfs\\stop_times.txt"))) {
            lines.parallel().map(line -> Arrays.asList(line.split(","))).skip(1).forEach(mapToStopTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.nanoTime() - startTime);
        parseFile("transfers.txt", transfersFunction);

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println(estimatedTime);
        int i = 0;
        i++;

    }

    private  Consumer< List<String> > calendarFunction = (csv -> {
        if(csv.get(1).equals("1") || csv.get(2).equals("1") || csv.get(3).equals("1") || csv.get(4).equals("1") || csv.get(5).equals("1"))
            weekday.add(csv.get(0));
        if(csv.get(6).equals("1"))
            saturday.add(csv.get(0));
        if(csv.get(7).equals("1"))
            sunday.add(csv.get(0));
    });

    private  Consumer<List<String>> tripsFunction = (csv -> {
        if(weekday.contains(csv.get(1))){
            trips.add(csv.get(2));
            //trips.add(csv.get(2));
        }
    });


    private  Consumer<List<String>> mapToStopTimes = (csv) -> {

        if(trips.contains(csv.get(0))){
            stopTimes.add(new StopTimes(csv.get(0), csv.get(1), csv.get(2), csv.get(3), Integer.parseInt(csv.get(4))));
        }
    };

    private  Consumer<List<String>> stopTimesFunction = (csv -> {
        String trip_id = csv.get(0);
        if(trips.contains(trip_id)) {
            try {
                StopTimes st = new StopTimes(csv.get(0), csv.get(1), csv.get(2), csv.get(3), Integer.parseInt(csv.get(4)));
                st.tripId = trip_id;
                stopTimes.add(st);
            } catch (Exception e) {
                System.out.println(csv);
                e.printStackTrace();
            }
        }
    });

    private  Consumer<List<String>> transfersFunction= (csv -> {
        try {
            if(csv.get(2).equals("1")){ //filter(x->!x.get(2).equals("1"))
                Transfer t = new Transfer(csv.get(0), csv.get(1), csv.get(2), "0");
                fillTransfer(t, transfers);
            }else{
                Transfer t = new Transfer(csv.get(0), csv.get(1), csv.get(2), csv.get(3));
                fillTransfer(t, transfers);
            }
        }catch (Exception e){
            System.out.println(csv);
            e.printStackTrace();
        }
    });

    private static void  parseFile(String fileName, Consumer<List<String>> function){
        String file = "C:\\Users\\bjozz\\Desktop\\gtfs\\"+fileName;
        try (Stream<String> lines = Files.lines(Paths.get(file))) {
            lines.map(line -> Arrays.asList(line.split(","))).skip(1).parallel().forEach(function);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fillTransfer(Transfer t, Map<String, ArrayList<Transfer>> transfers) {
        if (transfers.containsKey(t.from_stop_id)) {
            transfers.get(t.from_stop_id).add(t);
        } else {
            ArrayList<Transfer> trans = new ArrayList<>();
            trans.add(t);
            transfers.put(t.from_stop_id, trans);
        }
        /*if (transfers.containsKey(t.to_stop_id)) {
            transfers.get(t.to_stop_id).add(new StopName(t.from_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
        } else {
            ArrayList<StopName> hm = new ArrayList<>();
            hm.add(new StopName(t.from_stop_id, Integer.parseInt(t.min_transfer_time) / 60));
            transfers.put(t.to_stop_id, hm);
        }*/
    }


    public  List<StopTimes> getStopTimes() {
        return stopTimes;
    }

    public  Map<String, ArrayList<Transfer>> getTransfers() {
        return transfers;
    }
}
