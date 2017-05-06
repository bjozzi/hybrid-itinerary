package TimeDependent;

/**
 * Created by Karlis on 2017.04.05..
 */
public class TDArc {

    public String headNodeID;
    public double cost;
    public double departureTime; //if departure time is -1 then it is always accessible
    public double fullCost;
    public String tripID;

    public void setFullCost(double fullCost) {
        this.fullCost = fullCost;
    }

    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TDArc(String headNodeID) {
        this.headNodeID = headNodeID;
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public static TDArc createArc(String headNodeID, Double cost, double depTime, String tripID) {
        TDArc ar = new TDArc(headNodeID);
        ar.setCost(cost);
        ar.setDepartureTime(depTime);
        ar.tripID = tripID;
        return ar;
    }

    public double getCost() {
        return cost;
    }

    public double getFullCost(double TimeNow, String trip_id) {
        double Waiting = 0;
        if (departureTime == -1)
            Waiting = 0;//cost;
        else
            Waiting = departureTime - TimeNow;// + cost;
        if (Waiting < 0)
            Waiting = Waiting * (-1);
        setFullCost(Waiting);
        return Waiting;
    }
}
