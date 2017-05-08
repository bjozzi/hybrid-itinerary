package TimeExpanded;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Created by bjozz on 4/24/2017.
 */
public class TransferPattern {
    public String startStation;
    public String endStation;
    public List<List<Double>> timeOrders = new ArrayList<>();
    public List<Double> timeOrder = new ArrayList<>();
    public List<Double> startTimes = new ArrayList<>();
    public double startTime;
    public double endTime;
    public double transferTime;
    public ArrayList<String> transferPattern = new ArrayList<>();

    public TransferPattern(double startTime, double endTime, double transferTime, ArrayList<String> transferPattern, List<Double> timeOrder) {
        this.timeOrder = timeOrder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transferTime = transferTime;
        this.transferPattern = transferPattern;
    }

    public TransferPattern(String transferPatterns, String timeorders){
        this.transferPattern = new ArrayList<String>(Arrays.asList(transferPatterns.split(";")));

        for (String timeorder : timeorders.split(";")){
            timeorder = timeorder.substring(1, timeorder.length() - 1);
            this.timeOrders.add(Arrays.stream(timeorder.split(",")).map(Double::parseDouble).collect(Collectors.toList()));
        }

        //Double[] doubleArr = convertArray(startTimes.split(";"), Double::parseDouble, Double[]::new);
        //this.timeOrder = new ArrayList<Double>(Arrays.asList(doubleArr));
        this.startStation = this.transferPattern.get(0);
        this.endStation = this.transferPattern.get(this.transferPattern.size()-1);
    }

    public TransferPattern() {
    }

    //for lists
    public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
        return from.stream().map(func).collect(Collectors.toList());
    }

    //for arrays
    public static <T, U> U[] convertArray(T[] from,
                                          Function<T, U> func,
                                          IntFunction<U[]> generator) {
        return Arrays.stream(from).map(func).toArray(generator);
    }
}
