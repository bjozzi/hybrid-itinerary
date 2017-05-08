import TimeExpanded.StopTimes;
import basic.CSVParser;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by bjozz on 4/27/2017.
 */
public class createTransferFromStops {
    public static void main(String[] args) {
        //transferFromStops();

        try {
            hubStations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hubStations() throws IOException {
        String gtfsFeed = "gtfsIceland";
        Map<String, Integer> stops = new HashMap<>();
        List<StopTimes> stopTimes = new ArrayList<StopTimes>();
        CSVParser csv = new CSVParser("C:\\Users\\bjozz\\Desktop\\" + gtfsFeed + "\\x.txt");
        boolean first = true;
        String trip_id;
        while (csv.readNextLine()) {
            if (first) {
                first = false;
                continue;
            }
            String stopId = csv.getItem(3);
            if(stops.containsKey(stopId)){
                int count = stops.get(stopId)+1;
                stops.put(stopId,count);
            }else{
                stops.put(stopId, 1);
            }
        }

        Map<String, Integer> sorted = stops.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)-> e1, LinkedHashMap::new));
        int i = 0;
        for(Map.Entry<String, Integer> s : sorted.entrySet()){
            System.out.println(s.getKey() + " - " + s.getValue());
        }

    }

    private static void transferFromStops() {
        HashMap<String, List<String>> stops = new HashMap<>();
        String gtfsFeed = "gtfsIceland";
        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\bjozz\\Desktop\\" + gtfsFeed + "\\stops.txt"))) {
            lines.map(line -> Arrays.asList(line.split(","))).skip(1).forEach(x->{
                //stop_id,stop_code,stop_name,stop_lat,stop_lon,location_type
                // stop(String stop_id, String stop_code, String stop_name, String stop_desc, String stop_lat, String stop_lon, String location_type, String parent_station)
                if(stops.containsKey(x.get(2))){
                    stops.get(x.get(2)).add(x.get(0));
                }else{
                    List<String> stopIds = new ArrayList<>();
                    stopIds.add(x.get(0));
                    stops.put(x.get(2), stopIds);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path file = Paths.get("C:\\Users\\bjozz\\Documents\\transfers.txt");

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(file))
        {
            stops.forEach((key, value) -> {
                if(value.size()>1){
                    for (int i = 0; i < value.size(); i++) {
                        for (int j = 0; j < value.size(); j++) {
                            try {
                                if(!value.get(i).equals(value.get(j))){
                                    writer.write(value.get(i) +"," + value.get(j)+ ",2,180\n" );
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
