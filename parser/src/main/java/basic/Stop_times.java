package basic;

/**
 * Created by Karlis on 2017.03.26..
 */
public class Stop_times {
    public String trip_id, stop_id, stop_headsign;
    public int stop_sequence, pickup_type, drop_off_type;
    public double arrival_time, departure_time;

    public Stop_times(String trip_id, String arrival_time, String departure_time, String stop_id, int stop_sequence, int pickup_type, int drop_off_type, String stop_headsign) {

        String[] arrivalTime = arrival_time.split(":");
        String[] departureTime = departure_time.split(":");
        this.arrival_time = Integer.parseInt(arrivalTime[0])*60 + Integer.parseInt(arrivalTime[1]) + Integer.parseInt(arrivalTime[2])/60;
        this.departure_time = Integer.parseInt(departureTime[0])*60 + Integer.parseInt(departureTime[1]) + Integer.parseInt(departureTime[2])/60;
        this.trip_id = trip_id;
        this.stop_id = stop_id;
        this.stop_headsign = stop_headsign;
        this.stop_sequence = stop_sequence;
        this.pickup_type = pickup_type;
        this.drop_off_type = drop_off_type;
    }
}

