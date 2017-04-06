package basic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Karlis on 2017.04.05..
 */
public class TDArc{

    public String headNodeID;
    public double cost;
    public List<Integer> times = new ArrayList<>();
    public Map<String, List<Integer>> waitingTimes = new HashMap<>();

    public void setCost(double cost) {
        this.cost = cost;
    }

    public TDArc(String headNodeID) {
        this.headNodeID = headNodeID;
    }

    public String getHeadNodeID() {
        return headNodeID;
    }

    public Arc createArc(String headNodeID, Double cost)
    {
        Arc ar = new Arc(headNodeID);
        ar.setCost(cost);
        return ar;
    }

    public double getCost() {
        return cost;
    }

    public int getTime(int TimeNow)
    {
        List<Integer> largerTimes = times.stream().filter(i -> i> TimeNow).collect(Collectors.toList());
        Collections.sort(largerTimes);

        return largerTimes.get(0);
    }
}
