package basic;

/**
 * Created by Karlis on 2017.03.23..
 */
public class ActiveNode  implements Comparable{
    private String id;
    private Double dist;
    private String parent;
    private String trip_id;
    private double arrivalTime;

    public ActiveNode(String id, Double dist, String parent, String trip_id, double arrivalTime) {
        this.id = id;
        this.dist = dist;
        this.parent = parent;
        this.trip_id = trip_id;
        this.arrivalTime = arrivalTime;
    }

    public ActiveNode() {
    }

    public String getTrip_id() {
        return trip_id;
    }

    /**
     * Creates a new node used to indicate that is has been visited
     * and what the currently known distance to it is.
     * @param id - the ID of the node in the original graph
     * @param dist - the initial discovered distance to this node
     * @param parent - the ID of the node from which we came to get the distance
     */
    public ActiveNode(String id, Double dist, String parent) {
        this.id = id;
        this.dist = dist;
        this.parent = parent;
        this.trip_id = "";
    }
    public ActiveNode(String id, Double dist, String parent, String trip_id) {
        this.id = id;
        this.dist = dist;
        this.parent = parent;
        this.trip_id = trip_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getDist() {
        return dist;
    }

    public void setDist(Double dist) {
        this.dist = dist;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getArrivalTime() {

        return arrivalTime;
    }
}
