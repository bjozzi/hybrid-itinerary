package TimeExpanded;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TEArc {
    public String headNodeID;
    public double cost;
    public String stopId;

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TEArc(String headNodeID, String stopId) {
        this.headNodeID = headNodeID;
        this.stopId = stopId;
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public static TEArc createArc(String headNodeID, Double cost, String stopId)
    {
        TEArc ar = new TEArc(headNodeID, stopId);
        ar.setCost(cost);
        return ar;
    }

    public double getCost() {
        return cost;
    }

}
