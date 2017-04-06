package basic;

/**
 * Created by Karlis on 2017.03.23..
 */
public class ActiveNode {
    private String id;
    private Double dist;
    private String parent;

    public ActiveNode() {
    }

    /**
     * Creates a new node used to indicate that is has been visited
     * and what the currently known distance to it is.
     * @param id - the ID of the node in the original graph
     * @param dist - the initial discovered distance to this node
     * @param parent - the ID of the node from which we came to get the distance
     */
    public ActiveNode(String id, Double dist, String parent) {
        this.id = id;
        this.dist = dist;
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getDist() {
        return dist;
    }

    public void setDist(Double dist) {
        this.dist = dist;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
