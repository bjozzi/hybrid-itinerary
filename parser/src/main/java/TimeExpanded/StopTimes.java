package TimeExpanded;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bjozz on 4/8/2017.
 */
public class StopTimes {
    public String tripId, serviceId, stopId, stop_headsign;
    public int stop_sequence, pickup_type, drop_off_type;
    public Date arrival_time, departure_time;

    public StopTimes(String arrival_time, String departure_time, String stop_id, int stop_sequence, int pickup_type, int drop_off_type, String stop_headsign) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateTimeTo = formatter.parse(arrival_time);
            Date dateTimeFrom = formatter.parse(departure_time);
            this.arrival_time = dateTimeTo;
            this.departure_time = dateTimeFrom;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.stopId = stop_id;
        this.stop_headsign = stop_headsign;
        this.stop_sequence = stop_sequence;
        this.pickup_type = pickup_type;
        this.drop_off_type = drop_off_type;
    }
}
