package TimeDependent;

import basic.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TDDGraph {

    private Map<String, Node> nodes;
    private Map<String, List<TDArc>> adjacentArcs;
    private int TransferTime = 3;

    public void setTransferTime(int transferTime) {
        TransferTime = transferTime;
    }

    public TDDGraph() {
        this.nodes = new HashMap<String, Node>();
        this.adjacentArcs = new HashMap<String, List<TDArc>>();
    }

    public void addArc(String sourceID, TDArc arc) {
        if (!adjacentArcs.containsKey(sourceID))
            adjacentArcs.put(sourceID, new ArrayList<TDArc>());
        adjacentArcs.get(sourceID).add(arc);
    }

    public Node getNode(String nodeID) {
        return nodes.get(nodeID);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public List<TDArc> getadjacentArc(String nodeID, double timeNow) {
        List<TDArc> adjacentLaterArcs = adjacentArcs.get(nodeID).stream().filter(x -> x.departureTime.values().stream().anyMatch(y -> y >= timeNow)).collect(Collectors.toList());
        return adjacentLaterArcs;
    }
    public List<TDArc> getadjacentArcBack(String nodeID, double timeNow) {
        List<TDArc> adjacentLaterArcs = adjacentArcs.get(nodeID).stream().filter(x -> x.departureTime.values().stream().anyMatch(y -> y <= timeNow)).collect(Collectors.toList());
        return adjacentLaterArcs;
    }

    public List<TDArc> getadjacentArc(String nodeID) {
        return adjacentArcs.get(nodeID);
    }

    public void setAdjacentArcs(Map<String, List<TDArc>> adjacentArcs) {
        this.adjacentArcs = adjacentArcs;
    }

    public Map<String, List<TDArc>> getadjacentArcs() {

        return adjacentArcs;
    }

    public void createNode(String ID, float lat, float lng) {
        nodes.put(ID, new Node(ID, lat, lng));
        //adjacentArcs.put(ID, new ArrayList<Arc>());
    }

    public void addEdge(String u, String v, double cost, double DepartureTime, String tripID) {
        if (u.equals(v))
            return;

        Node unode = getNode(u);
        Node vnode = getNode(v);

        if (unode == null || vnode == null) {
            return;
        }

        TDArc uarc = TDArc.createArc(v, cost, DepartureTime, tripID);
        if (this.getadjacentArc(u) != null && this.getadjacentArc(u).stream().anyMatch(x -> x.getHeadNodeID().equals(v))) {
            this.getadjacentArc(u).stream().filter(x -> x.getHeadNodeID().equals(v)).forEach(y -> y.departureTime.put(tripID, DepartureTime));
        } else
            addArc(u, uarc);
    }

    public int getNumNodes() {
        return getNodes().size();
    }
}
