package TD;

/**
 * Created by Karlis on 2017.05.18..
 */
public class TDNode {
    public String nodeID;
    public String Name;
    public String lat;
    public String lng;

    public TDNode(String nodeID, String name, String lat, String lng) {
        this.nodeID = nodeID;
        Name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public TDNode(String nodeID, String name) {

        this.nodeID = nodeID;
        Name = name;
    }
}
