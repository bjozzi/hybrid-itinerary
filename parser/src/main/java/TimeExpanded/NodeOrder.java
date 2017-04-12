package TimeExpanded;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bjozz on 4/8/2017.
 */
public class NodeOrder implements Comparable{
    public String nodeId;
    public int minute;
    public int type;
    public String stopId;
    public NodeOrder(String nodeId, int minute, int type, String stopId) {
        this.nodeId = nodeId;
        this.minute = minute;
        this.type = type;
        this.stopId = stopId;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
