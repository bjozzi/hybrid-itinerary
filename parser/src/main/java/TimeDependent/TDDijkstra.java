package TimeDependent;

import basic.ActiveNode;
import basic.Graph;
import basic.Main;

import java.util.*;

/**
 * Created by Karlis on 2017.04.05..
 */
public class TDDijkstra {
    private TDDGraph graph;
    private Map<String, Double> visitedNodeMarks;
    private PriorityQueue<ActiveNode> activeNodes;

    public void setParents(Map<String, ActiveNode> parents) {
        this.parents = parents;
    }

    private double TimeAtTheNode = 0;
    private Map<String, ActiveNode> parents;

    private Comparator<ActiveNode> activeNodeComparator;

    public TDDijkstra(TDDGraph graph) {
        this.graph = graph;
    }

    private TDDijkstra() {
        this.activeNodeComparator = new Comparator<ActiveNode>() {
            public int compare(ActiveNode o1, ActiveNode o2) {
                double o1FullDist = TimeAtTheNode - o1.getDist();
                double o2FullDist = TimeAtTheNode - o2.getDist();
                if (o1FullDist - o2FullDist < 0)
                    return -1;
                else if (o1FullDist - o2FullDist > 0)
                    return 1;
                else
                    return 0;

            }
        };
        this.visitedNodeMarks = new HashMap<String, Double>();
        this.activeNodes = new PriorityQueue<ActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, ActiveNode>();
    }

    /**
     * Computes the shortest path between two nodes in a {@link Graph}.
     *
     * @param startNodeId  - the (source) node to start the search from
     * @param targetNodeId - the (target) node to reach
     * @param startTime
     * @return {@link Double#NEGATIVE_INFINITY} if no shortest path was found,
     * otherwise a value indicating the total cost of the shortest path
     */
    public double computeShortestPath(String startNodeId, String targetNodeId, double startTime) {

        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<TDArc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;

        ActiveNode activeNode;
        ActiveNode currentNode;

        this.activeNodes = new PriorityQueue<ActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, ActiveNode>();
        activeNodes.add(new ActiveNode(startNodeId, startTime + 0.0, null));

        while (activeNodes.size() != 0) {
            currentNode = activeNodes.poll();

            if (isVisited(currentNode.getId())) {
                continue;
            }
            // Mark as settled
            visitedNodeMarks.put(currentNode.getId(), currentNode.getDist());

            numSettledNodes++;

            // Found target
            if (currentNode.getId().equals(targetNodeId)) {
                shortestPathCost = currentNode.getDist();
                //break;
            }

            // Graph was apparently not connected
            if (numSettledNodes > graph.getNumNodes()) {
                System.out.println("There is no short path between startNode and targetNode");
                break;
            }

            // Discover all adjacent nodes
            nodeAdjacentArcs = this.graph.getadjacentArc(currentNode.getId(), currentNode.getDist(), currentNode.getTrip_id());
            // int currentLabel = graph.getNode(currentNode.getId()).getLabel();
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                TDArc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = currentNode.getDist() + arc.getFullCost(currentNode.getDist(), currentNode.getTrip_id()) + arc.getCost();
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new ActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), arc.tripID);
                    if (!parents.containsKey(arc.getHeadNodeID()))
                        parents.put(arc.getHeadNodeID(), activeNode);
                    else {
                        parents.get(arc.getHeadNodeID()).setDist(distToAdjNode);
                        parents.get(arc.getHeadNodeID()).setParent(currentNode.getId());
                    }
                    activeNodes.add(activeNode);
                }
            }
        }

        return parents.get(targetNodeId).getDist();
    }

    public Map<String, ActiveNode> computeShortestPathTree(String startNodeId, double startTime, double endTime) {

        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<TDArc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;

        ActiveNode activeNode;
        ActiveNode currentNode;

        this.activeNodes = new PriorityQueue<ActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, ActiveNode>();
        activeNodes.add(new ActiveNode(startNodeId, startTime + 0.0, null, "", startTime));

        while (activeNodes.size() != 0) {
            currentNode = activeNodes.poll();

            if (isVisited(currentNode.getId())) {
                continue;
            }

            // Mark as settled
            visitedNodeMarks.put(currentNode.getId(), currentNode.getDist());

            // Graph was apparently not connected
            if (numSettledNodes > graph.getNumNodes()) {
                System.out.println("There is no short path between startNode and targetNode");
                break;
            }

            // Discover all adjacent nodes
            nodeAdjacentArcs = this.graph.getadjacentArcForBackwardsSearch(currentNode.getId(), currentNode.getArrivalTime(), currentNode.getTrip_id(), endTime);
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                TDArc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = arc.getFullCost(currentNode.getArrivalTime(), currentNode.getTrip_id()) + arc.getCost();
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) {
                    double depTime = arc.departureTime;
                    if(depTime == -1)
                        depTime = currentNode.getArrivalTime();
                    activeNode = new ActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), arc.tripID, depTime - arc.getCost());
                    if((activeNode.getArrivalTime()+"").equals("0.0") )
                        continue;
                    if (!parents.containsKey(arc.getHeadNodeID()))
                        parents.put(arc.getHeadNodeID(), activeNode);
                    else {
                        parents.get(arc.getHeadNodeID()).setDist(distToAdjNode);
                        parents.get(arc.getHeadNodeID()).setArrivalTime(activeNode.getArrivalTime());
                        parents.get(arc.getHeadNodeID()).setParent(currentNode.getId());
                    }
                    activeNodes.add(activeNode);
                }
            }
        }

        return parents;
    }

    private boolean isVisited(String nodeId) {
        return visitedNodeMarks.containsKey(nodeId);
    }

    /**
     * Construct a string containing a description of the shortest path
     * between two nodes, node for node.
     *
     * @param startNodeId  - the source node
     * @param targetNodeId - the target node
     * @return a string with a description of the path
     */
    public String shortestPathToString(String startNodeId, String targetNodeId) {
        String path = "";
        String currentNodeId;

        currentNodeId = targetNodeId;
        path = path + currentNodeId;
        while (currentNodeId != startNodeId) {
            currentNodeId = parents.get(currentNodeId).getParent();
            path = currentNodeId + "->" + path;
            if (currentNodeId == null)
                break;
        }

        return path;
    }

    public String shortestPathName(String startNodeId, String targetNodeId, Map<String, String> stopNames) {

        String pathName = "";
        String currentNodeId;

        currentNodeId = targetNodeId;

        pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).getDist());
        while (currentNodeId != startNodeId) {
            currentNodeId = parents.get(currentNodeId).getParent();
            pathName = stopNames.get(currentNodeId) + "@" + Main.MinutesToTime(parents.get(currentNodeId).getDist()) + "->" + pathName;
            if (currentNodeId == null)
                break;
        }

        return pathName;
    }
}
