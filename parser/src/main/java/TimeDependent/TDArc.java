package TimeDependent;

/**
 * Created by Karlis on 2017.04.05..
 */
public class TDArc {

    public String headNodeID;
    public double cost;
    public int departureTime; //if departure time is -1 then it is always accessible
    public double fullCost;

    public void setFullCost(double fullCost) {
        this.fullCost = fullCost;
    }

    public void setDepartureTime(int departureTime) {
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

    public static TDArc createArc(String headNodeID, Double cost, int depTime) {
        TDArc ar = new TDArc(headNodeID);
        ar.setCost(cost);
        ar.setDepartureTime(depTime);
        return ar;
    }

    public double getCost() {
        return cost;
    }

    public double getFullCost(double TimeNow) {
        double WaitingNGoing = 0;
        if (departureTime == -1)
            WaitingNGoing = cost;
        else
            WaitingNGoing = departureTime - TimeNow + cost;

        setFullCost(WaitingNGoing);
        return WaitingNGoing;
    }
}
