package TimeExpanded;


import basic.Arc;
import basic.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bjozz on 4/6/2017.
 */
public class TEGraph  {

    private Map<String, TENode> nodes;
    private Map<String, List<TEArc>> adjacentArcs;

    public TEGraph() {
        this.nodes = new HashMap<String, TENode>();
        this.adjacentArcs = new HashMap<String, List<TEArc>>();
    }
    public void addArc(String sourceID, TEArc arc) {
        if (!adjacentArcs.containsKey(sourceID))
            adjacentArcs.put(sourceID, new ArrayList<TEArc>());
        adjacentArcs.get(sourceID).add(arc);
    }
    public TENode getNode(String nodeID)
    {
        return nodes.get(nodeID);
    }
    public Map<String, TENode> getNodes() {
        return nodes;
    }
    public List<TEArc> getadjacentArc(String nodeID)
    {
        return adjacentArcs.get(nodeID);
    }

    public Map<String, List<TEArc>> getadjacentArcs() {

        return adjacentArcs;
    }

    public void createNode(String ID, String station, int time, int type)
    {
        nodes.put(ID,new TENode(ID, station, time, type));
    }
    public void addEdge(String u, String v, double cost)
    {
        if(u.equals(v))
            return;

        TENode unode = getNode(u);
        TENode vnode = getNode(v);

        if(unode == null || vnode == null) {
            return;
        }

        TEArc uarc = TEArc.createArc(v, cost);
        addArc(u, uarc);
    }
    public int getNumNodes()
    {return getNodes().size();
    }

}
