package TimeExpanded;


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

    public void createNode(String ID, String station, double time, int type, String trip)
    {
        nodes.put(ID,new TENode(ID, station, time, type, trip));
    }
    public void addEdge(String u, String v, double cost, String vStation)
    {
        if(u.equals(v))
            return;

        TENode unode = getNode(u);
        TENode vnode = getNode(v);

        if(vnode.time - unode.time < 0){
            System.out.println("add edge in TEGraph ERROR!" + vnode.time  + " - " + unode.time );
        }else{
            TEArc uarc = TEArc.createArc(v, cost, vStation);
            addArc(u, uarc);
        }
        /*if(unode == null || vnode == null) {
            return;
        }*/

    }
    public int getNumNodes()
    {return getNodes().size();
    }

}
