package TimeExpanded;

import basic.Node;

import java.util.Objects;

/**
 * Created by bjozz on 4/8/2017.
 */
public class NodeOrder implements Comparable{
    public String nodeId;
    public double minute;
    public int type;
    public String stopId;
    public NodeOrder(String nodeId, double minute, int type, String stopId) {
        this.nodeId = nodeId;
        this.minute = minute;
        this.type = type;
        this.stopId = stopId;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeOrder that = (NodeOrder) o;
        return Objects.equals(this.minute, that.minute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minute);
    }
}
