import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karlis on 2017.03.23..
 */
public class Graph {

    private Map<String, Node> nodes;
    private Map<String, List<Arc>> adjacentArcs;

    public Graph() {
        this.nodes = new HashMap<String, Node>();
        this.adjacentArcs = new HashMap<String, List<Arc>>();
    }
    public void addArc(String sourceID, Arc arc) {
        if (!adjacentArcs.containsKey(sourceID))
            adjacentArcs.put(sourceID, new ArrayList<Arc>());
        adjacentArcs.get(sourceID).add(arc);
    }
    public Node getNode(String nodeID)
    {
        return nodes.get(nodeID);
    }
    public Map<String, Node> getNodes() {
        return nodes;
    }
    public List<Arc> getadjacentArc(String nodeID)
    {
        return adjacentArcs.get(nodeID);
    }

    public Map<String, List<Arc>> getadjacentArcs() {

        return adjacentArcs;
    }

    public void createNode(String ID, float lat, float lng)
    {
        nodes.put(ID,new Node(ID, lat, lng));
        //adjacentArcs.put(ID, new ArrayList<Arc>());
    }
    public void addEdge(String u, String v, double cost)
    {
        if(u.equals(v))
            return;

        Node unode = getNode(u);
        Node vnode = getNode(v);

        if(unode == null || vnode == null) {
            return;
        }

        Arc uarc = Arc.createArc(v, cost);
        addArc(u, uarc);
    }
    public int getNumNodes()
    {return getNodes().size();
    }
}
