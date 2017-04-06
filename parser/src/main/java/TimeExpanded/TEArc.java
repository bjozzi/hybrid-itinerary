package TimeExpanded;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TEArc {
    public String headNodeID;
    public double cost;
    public String GTFSName;

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TEArc(String headNodeID) {
        this.headNodeID = headNodeID;
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public static TEArc createArc(String headNodeID, Double cost)
    {
        TEArc ar = new TEArc(headNodeID);
        ar.setCost(cost);
        return ar;
    }

    public double getCost() {
        return cost;
    }

}
