import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karlis on 2017.03.23..
 */
public class Graph {

    private Map<String, Node> nodes;
    private Map<String, List<Arc>> adjacentNodes;

    public Graph() {
        this.nodes = new HashMap<String, Node>();
        this.adjacentNodes = new HashMap<String, List<Arc>>();
    }
    public void addArc(String sourceID, Arc arc) {
        if (!adjacentNodes.containsKey(sourceID))
            adjacentNodes.put(sourceID, new ArrayList<Arc>());
        adjacentNodes.get(sourceID).add(arc);
    }
    public Node getNode(String nodeID)
    {
        return nodes.get(nodeID);
    }
    public Map<String, Node> getNodes() {
        return nodes;
    }
    public List<Arc> getAdjacentNodes(String nodeID)
    {
        return adjacentNodes.get(nodeID);
    }

    public Map<String, List<Arc>> getAdjacentNodes() {

        return adjacentNodes;
    }

    public void createNode(String ID, float lat, float lng)
    {
        nodes.put(ID,new Node(ID, lat, lng));
        //adjacentNodes.put(ID, new ArrayList<Arc>());
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
