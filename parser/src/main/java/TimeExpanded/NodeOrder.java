package TimeExpanded;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bjozz on 4/8/2017.
 */
public class NodeOrder {
    public String stopId;
    public int minute;
    public int type;
    public NodeOrder(String stopId, int minute, int type) {
        this.stopId = stopId;
        this.minute = minute;
        this.type = type;
    }

}
