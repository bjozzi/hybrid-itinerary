package basic;

import java.util.*;

/**
 * Created by Karlis on 2017.03.23..
 */
public class Dijkstra {
    private Graph graph;
    private Map<String, Double> visitedNodeMarks;
    private PriorityQueue<ActiveNode> activeNodes;
    private Map<String, ActiveNode> parents;

    private Comparator<ActiveNode> activeNodeComparator;

    public Dijkstra(Graph graph) {
        this();
        this.graph = graph;
    }

    private Dijkstra() {
        this.activeNodeComparator = new Comparator<ActiveNode>() {
            public int compare(ActiveNode o1, ActiveNode o2) {
                return (o1.getDist() - o2.getDist() < 0) ? -1 : 1;
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
     * @return {@link Double#NEGATIVE_INFINITY} if no shortest path was found,
     * otherwise a value indicating the total cost of the shortest path
     */
    public double computeShortestPath(String startNodeId, String targetNodeId) {

        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<Arc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;

        ActiveNode activeNode;
        ActiveNode currentNode;


        this.activeNodes = new PriorityQueue<ActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, ActiveNode>();
        activeNodes.add(new ActiveNode(startNodeId, 0.0, null));

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
            nodeAdjacentArcs = this.graph.getadjacentArc(currentNode.getId());
            // int currentLabel = graph.getNode(currentNode.getId()).getLabel();
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                Arc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = currentNode.getDist() + arc.getCost();
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new ActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId());
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

        return shortestPathCost;
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

    /**
     * Gets the list of node IDs lying on the shortest path between two nodes.
     *
     * @param startNodeId  - the source node
     * @param targetNodeId - the target node
     * @return a {@link List} containing the IDs of all nodes on the shortest path
     */
   /* public List<String> getPathNodeIDs(String startNodeId, String targetNodeId) {
        List<String> result = new ArrayList<String>();

        String next = targetNodeId;
        String current;
        result.add(next);

        while (next != startNodeId) {
            current = parents.get(next);
            if (current == null) break;
            result.add(current);
            next = current;
        }
        Collections.reverse(result);

        return result;
    }*/

    /*public List<GeoJson> dijkstraToGeoJson(String startNodeId, String targetNodeId) {
        List<GeoJson> result = new ArrayList<GeoJson>();

        String next = targetNodeId;
        String current;

        while (next != startNodeId) {
            current = parents.get(next);

            Node a = graph.getNode(current);
            Node b = graph.getNode(next);

            if (a != null && b != null) {
                GeoJson gj = GeoJson.arcToGeoJson(a, b);
                result.add(0, gj);
            } else {
                break;
            }

            next = current;
        }

        return result;
    }*/
}
