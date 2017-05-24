package TD;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Karlis on 2017.05.18..
 */
public class TDArc {
    public String toID;
    public double cost;
    public Map<Double, List<String>> accessTime;
    List<Double> allTimes = new ArrayList<>();

    public void setAccessTime(double accesTime, String tripID) {
        if (this.accessTime.containsKey(accesTime)) {
            this.accessTime.get(accesTime).add(tripID);
        } else {
            List<String> trips = new ArrayList<>();
            trips.add(tripID);
            this.accessTime.put(accesTime, trips);
        }
        if (!allTimes.contains(accesTime))
            allTimes.add(accesTime);
    }

    public TDArc(String toID, double cost, double accessTime, String tripID) {
        this.toID = toID;
        this.cost = cost;
        this.accessTime = new HashMap<>();
        List<String> trips = new ArrayList<>();
        trips.add(tripID);
        this.accessTime.put(accessTime, trips);
        if (!allTimes.contains(accessTime))
            allTimes.add(accessTime);
    }

    public Map.Entry<Double, List<String>> getAccessTime(double TimeNow) {
        try {
            if (allTimes.stream().allMatch(x -> x == -1)) {
                return accessTime.entrySet().iterator().next();
            } else {
                double time = allTimes.stream().filter(x -> x >= TimeNow).min(Double::compareTo).get();
                return accessTime.entrySet().stream().filter(x -> x.getKey() == time).iterator().next();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Map.Entry<Double, List<String>> getAccessTimeBack(double TimeNow) {
        try {
            if (allTimes.stream().allMatch(x -> x == -1)) {
                return accessTime.entrySet().iterator().next();
            } else {
                double time = allTimes.stream().filter(x -> x <= TimeNow).max(Double::compareTo).get();
                return accessTime.entrySet().stream().filter(x -> x.getKey() == time).iterator().next();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Map.Entry<Double, List<String>> getAccessTime(double TimeNow, List<String> trips) {
        try {
            if (allTimes.stream().allMatch(x -> x == -1)) {
                return accessTime.entrySet().iterator().next();
            } else {
                double time = allTimes.stream().filter(x -> {
                    double timeToAdd = 0;
                    if (trips.size() > 0 && !trips.stream().allMatch(y -> y.equals("Transfer"))) {
                        if (!Collections.disjoint(trips, accessTime.get(x))) {
                        } else
                            timeToAdd = 3;
                    }

                    if (x >= (TimeNow + timeToAdd))
                        return true;
                    else
                        return false;
                }).min(Double::compareTo).get();
                Map.Entry<Double, List<String>> val = accessTime.entrySet().stream().filter(x -> x.getKey() == time).iterator().next();
                if (trips.size() > 0 && !trips.stream().allMatch(x -> x.equals("Transfer"))) {
                    List<String> tr = val.getValue().stream().filter(x->trips.contains(x)).collect(Collectors.toList());
                    if (tr.size() > 0)
                        val.setValue(tr);
                }
                return val;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Map.Entry<Double, List<String>> getAccessTimeBack(double TimeNow, List<String> trips) {
        try {
            if (allTimes.stream().allMatch(x -> x == -1)) {
                return accessTime.entrySet().iterator().next();
            } else {
                double time = allTimes.stream().filter(x -> {
                    double timeToAdd = 0;
                    if (trips.size() > 0 && !trips.stream().allMatch(y -> y.equals("Transfer"))) {
                        if (!Collections.disjoint(trips, accessTime.get(x))) {
                        } else
                            timeToAdd = -3;
                    }

                    if (x <= (TimeNow + timeToAdd))
                        return true;
                    else
                        return false;
                }).max(Double::compareTo).get();
                Map.Entry<Double, List<String>> val =  accessTime.entrySet().stream().filter(x -> x.getKey() == time).iterator().next();
                if (trips.size() > 0 && !trips.stream().allMatch(x -> x.equals("Transfer"))) {
                    List<String> tr = val.getValue().stream().filter(x->trips.contains(x)).collect(Collectors.toList());
                    if (tr.size() > 0)
                        val.setValue(tr);
                }
                return val;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
