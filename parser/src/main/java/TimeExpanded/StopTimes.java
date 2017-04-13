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
    public double arrival_time, departure_time;

    public StopTimes(String tripId, String arrival_time, String departure_time, String stop_id, int stop_sequence, int pickup_type, int drop_off_type, String stop_headsign) {
        String[] arrivalTime = arrival_time.split(":");
        String[] departureTime = departure_time.split(":");
        this.arrival_time = Integer.parseInt(arrivalTime[0])*60 + Integer.parseInt(arrivalTime[1]) + Integer.parseInt(arrivalTime[2])/60;
        this.departure_time = Integer.parseInt(departureTime[0])*60 + Integer.parseInt(departureTime[1]) + Integer.parseInt(departureTime[2])/60;
        this.tripId = tripId;
        this.stopId = stop_id;
        this.stop_headsign = stop_headsign;
        this.stop_sequence = stop_sequence;
        this.pickup_type = pickup_type;
        this.drop_off_type = drop_off_type;
    }
}



/*
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");


        try {
            Date dateTimeTo = formatter.parse(arrival_time);
            Date dateTimeFrom = formatter.parse(departure_time);
            this.arrival_time = dateTimeTo;
            this.departure_time = dateTimeFrom;
        } catch (ParseException e) {
            e.printStackTrace();
        }*/