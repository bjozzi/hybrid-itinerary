package TimeExpanded;

import java.util.ArrayList;

/**
 * Created by bjozz on 4/24/2017.
 */
public class TransferPattern {
    public double startTime;
    public double endTime;
    public double transferTime;
    public ArrayList<String> transferPattern = new ArrayList<>();

    public TransferPattern(double startTime, double endTime, double transferTime, ArrayList<String> transferPattern) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.transferTime = transferTime;
        this.transferPattern = transferPattern;
    }

    public TransferPattern() {
    }
}
