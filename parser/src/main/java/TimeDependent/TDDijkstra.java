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
        activeNode = new ActiveNode(startNodeId, startTime + 0.0, null);
        activeNodes.add(activeNode);
        parents.put(activeNode.getId(), activeNode);
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
            nodeAdjacentArcs = this.graph.getadjacentArc(currentNode.getId(), currentNode.getDist());
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                TDArc arc = nodeAdjacentArcs.get(i);
                if (arc.getHeadNodeID().equals(startNodeId)) {
                    String x = "";
                }
                Map.Entry<String, Double> depTime = arc.GetClosestConnection(currentNode.getDist(), currentNode.getTrip_id());
                if (depTime == null)
                    continue;
                distToAdjNode = depTime.getValue() + arc.getCost();//currentNode.getDist()
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new ActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), depTime.getKey());
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
        activeNode = new ActiveNode(startNodeId,  0.0, null, "", startTime);
        activeNodes.add(activeNode);
        parents.put(activeNode.getId(), activeNode);
        parents.get(activeNode.getId()).setParent("");
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
            nodeAdjacentArcs = this.graph.getadjacentArcBack(currentNode.getId(), currentNode.getArrivalTime());
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                TDArc arc = nodeAdjacentArcs.get(i);
                Map.Entry<String, Double> depTime = arc.GetClosestConnectionBack(currentNode.getArrivalTime(), currentNode.getTrip_id(), endTime);
                if (depTime == null)
                    continue;
                Double time = depTime.getValue();
                if (depTime.getValue() == -1)
                    time = currentNode.getArrivalTime();
                distToAdjNode = currentNode.getArrivalTime() - time + arc.getCost();
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) {
                    activeNode = new ActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), depTime.getKey(), time - arc.getCost());
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
