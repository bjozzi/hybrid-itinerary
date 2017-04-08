package TimeDependent;

import basic.Arc;
import basic.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TDDGraph {

    private Map<String, Node> nodes;
    private Map<String, List<TDArc>> adjacentArcs;

    public TDDGraph() {
        this.nodes = new HashMap<String, Node>();
        this.adjacentArcs = new HashMap<String, List<TDArc>>();
    }
    public void addArc(String sourceID, TDArc arc) {
        if (!adjacentArcs.containsKey(sourceID))
            adjacentArcs.put(sourceID, new ArrayList<TDArc>());
        adjacentArcs.get(sourceID).add(arc);
    }
    public Node getNode(String nodeID)
    {
        return nodes.get(nodeID);
    }
    public Map<String, Node> getNodes() {
        return nodes;
    }
    public List<TDArc> getadjacentArc(String nodeID)
    {
        return adjacentArcs.get(nodeID);
    }

    public Map<String, List<TDArc>> getadjacentArcs() {

        return adjacentArcs;
    }

    public void createNode(String ID, float lat, float lng)
    {
        nodes.put(ID,new Node(ID, lat, lng));
        //adjacentArcs.put(ID, new ArrayList<Arc>());
    }
    public void addEdge(String u, String v, double cost, int DepartureTime)
    {
        if(u.equals(v))
            return;

        Node unode = getNode(u);
        Node vnode = getNode(v);

        if(unode == null || vnode == null) {
            return;
        }

        TDArc uarc = TDArc.createArc(v, cost, DepartureTime);
        addArc(u, uarc);
    }
    public int getNumNodes()
    {return getNodes().size();
    }
}
