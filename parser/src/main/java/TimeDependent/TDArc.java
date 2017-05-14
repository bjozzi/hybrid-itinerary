package TimeDependent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Karlis on 2017.04.05..
 */
public class TDArc {

    public String headNodeID;
    public double cost;
    public Map<String, Double> departureTime; //if departure time is -1 then it is always accessible
    public double fullCost;

    public void setFullCost(double fullCost) {
        this.fullCost = fullCost;
    }

    public void setDepartureTime(double departureTime, String tripID) {
        this.departureTime.put(tripID, departureTime);
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TDArc(String headNodeID) {
        this.headNodeID = headNodeID;
        departureTime = new HashMap<>();
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public static TDArc createArc(String headNodeID, Double cost, double depTime, String tripID) {
        TDArc ar = new TDArc(headNodeID);
        ar.setCost(cost);
        ar.setDepartureTime(depTime, tripID);
        return ar;
    }

    public static TDArc createArc(String headNodeID, Double cost, Map<String, Double> depTime) {
        TDArc ar = new TDArc(headNodeID);
        ar.setCost(cost);
        ar.departureTime = depTime;
        return ar;
    }

    public double getCost() {
        return cost;
    }

    public Map.Entry<String, Double> GetClosestConnection(double TimeNow, String tripID) {
        try {
            Map.Entry<String, Double> something;
            if (departureTime.values().stream().allMatch(x -> x.equals(-1)))
                something = departureTime.entrySet().iterator().next();
            else
                something = departureTime.entrySet().stream().filter(x -> {
                    double addTime = 0;
                    if (!tripID.equals("") && !tripID.equals(x.getKey()))
                        addTime = 3;
                    if (x.getValue() >= (TimeNow + addTime))
                        return true;
                    else
                        return false;
                }).sorted(Map.Entry.<String, Double>comparingByValue()).iterator().next();
            return something;
        } catch (Exception e) {
            return null;
        }
    }

    public Map.Entry<String, Double> GetClosestConnectionBack(double TimeNow, String tripID, double endTime) {
        try {
            Map.Entry<String, Double> something;
            if (departureTime.values().stream().allMatch(x -> x.equals(-1)))
                something = departureTime.entrySet().iterator().next();
            else
                something = departureTime.entrySet().stream().filter(x -> {
                    double addTime = 0;
                    if (!tripID.equals("") && !tripID.equals(x.getKey()) && !tripID.equals("Transfer"))
                        addTime = 3;
                    if (x.getValue() <= (TimeNow - addTime) && x.getValue() >= endTime)
                        return true;
                    else
                        return false;
                }).sorted(Map.Entry.<String, Double>comparingByValue().reversed()).iterator().next();
            return something;
        } catch (Exception e) {
            return null;
        }
    }
}
