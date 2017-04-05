import java.util.Date;

/**
 * Created by Karlis on 2017.03.26..
 */
public class Stop_times {
    public String stop_id,arrival_time, departure_time, stop_headsign;
    public int stop_sequence, pickup_type, drop_off_type;

    public Stop_times(String arrival_time, String departure_time, String stop_id, int stop_sequence, int pickup_type, int drop_off_type, String stop_headsign) {

        this.stop_id = stop_id;
        this.stop_headsign = stop_headsign;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.stop_sequence = stop_sequence;
        this.pickup_type = pickup_type;
        this.drop_off_type = drop_off_type;
    }
}

