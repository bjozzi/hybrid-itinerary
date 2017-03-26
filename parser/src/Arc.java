import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by Karlis on 2017.03.23..
 */
public class Arc {
    public String headNodeID;
    public double cost;

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Arc(String headNodeID) {
        this.headNodeID = headNodeID;
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public static Arc createArc(String headNodeID, Double cost)
    {
        Arc ar = new Arc(headNodeID);
        ar.setCost(cost);
        return ar;
    }

    public double getCost() {
        return cost;
    }

}
