package TimeExpanded;

/**
 * Created by bjozz on 4/9/2017.
 */
public class Service {
    public String serviceId;
    public String tripId;
    public boolean weekday;
    public boolean saturday;
    public boolean sunday;

    public Service(String serviceId, String tripId, boolean weekday, boolean saturday, boolean sunday) {
        this.serviceId = serviceId;
        this.tripId = tripId;
        this.weekday = weekday;
        this.saturday = saturday;
        this.sunday = sunday;
    }
}
