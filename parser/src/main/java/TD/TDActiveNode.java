package TD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karlis on 2017.05.18..
 */
public class TDActiveNode implements Comparable<TDActiveNode> {
    public String nodeID;
    public double distance;
    public String parentID;
    public List<String> trips = new ArrayList<>();
    public double time;

    public TDActiveNode(String nodeID, double distance, String parentID, List<String> trips, double time) {
        this.nodeID = nodeID;
        this.distance = distance;
        this.parentID = parentID;
        this.trips = trips;
        this.time = time;
    }

    public TDActiveNode(String nodeID, double distance, String parentID, String trip, double time) {
        this.nodeID = nodeID;
        this.distance = distance;
        this.parentID = parentID;
        if (!trip.equals(""))
            trips.add(trip);
        this.time = time;
    }

    public TDActiveNode(String nodeID, double distance, String parentID, List<String> trips) {
        this.nodeID = nodeID;
        this.distance = distance;
        this.parentID = parentID;
        this.trips = trips;
    }

    public TDActiveNode(String nodeID, double distance, String parentID, String trip) {
        this.nodeID = nodeID;
        this.distance = distance;
        this.parentID = parentID;
        if (!trip.equals(""))
            trips.add(trip);
    }

    @Override
    public int compareTo(TDActiveNode o) {
        if (this.distance - o.distance < 0)
            return -1;
        else if (this.distance - o.distance > 0)
            return 1;
        else
            return 0;
    }
}
