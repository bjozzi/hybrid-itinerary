package TimeExpanded;

import basic.Graph;
import basic.Main;
import basic.Node;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by bjozz on 4/9/2017.
 */
public class TEDijkstra {

    private TEGraph graph;
    private Map<String, Double> visitedNodeMarks;
    private PriorityQueue<TEActiveNode> activeNodes;
    private Map<String, TEActiveNode> parents;
    private Set<String> stationsVisited;

    private Comparator<TEActiveNode> activeNodeComparator;

    public TEDijkstra(TEGraph graph) {
        this();
        this.graph = graph;
    }

    private TEDijkstra() {
        this.activeNodeComparator = new Comparator<TEActiveNode>() {
            public int compare(TEActiveNode o1, TEActiveNode o2) {
                return (o1.getDist() - o2.getDist() < 0) ? -1 : 1;
            }
        };
        this.visitedNodeMarks = new HashMap<String, Double>();
        this.activeNodes = new PriorityQueue<TEActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, TEActiveNode>();
        this.stationsVisited = new HashSet<>();
    }

    /**
     * Computes the shortest path between two nodes in a {@link Graph}.
     *
     * @param startNodeId  - the (source) node to start the search from
     * @param targetNodeId - the (target) node to reach
     * @return {@link Double#NEGATIVE_INFINITY} if no shortest path was found,
     * otherwise a value indicating the total cost of the shortest path
     */
    public String computeShortestPath(String startNodeId, String startStopId, String targetNodeId) {

        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<TEArc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;
        String endNodeId = "";

        TEActiveNode activeNode;
        TEActiveNode currentNode;



        this.activeNodes = new PriorityQueue<TEActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, TEActiveNode>();
        activeNodes.add(new TEActiveNode(startNodeId, 0.0, null, startStopId));

        String previousStation="";
        while (activeNodes.size() != 0) {
            currentNode = activeNodes.poll();
            //System.out.println(currentNode.getId());
            //System.out.println(currentNode.getDist());
            if (isVisited(currentNode.getId())) {
                continue;
            }

            // Mark as settled
            visitedNodeMarks.put(currentNode.getId(), currentNode.getDist());

            /*if(stationsVisited.contains(currentNode.getStopId()) && !currentNode.getStopId().equals(previousStation)){
                continue;
            }else if(!targetNodeId.equals(currentNode.getStopId())){
                stationsVisited.add(currentNode.getStopId());
            }
            previousStation = currentNode.getStopId();*/


            numSettledNodes++;

            // Found target
            if (currentNode.getStopId().equals(targetNodeId)) {
                if(shortestPathCost > currentNode.getDist()){
                    endNodeId = currentNode.getId();
                    shortestPathCost = currentNode.getDist();
                }
                //break;
            }
            if(numSettledNodes == 10000){
                break;
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
                TEArc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = currentNode.getDist() + arc.getCost();
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new TEActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), arc.stopId);
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

        //return shortestPathCost +";"+ endNodeId;
        return endNodeId;
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
     * @param stopNames
     * @return a string with a description of the path
     */
    public String shortestPathToString(String startNodeId, String targetNodeId, Map<String, String> stopNames) {
        String path = "";
        String pathName = "";
        String currentNodeId;
        String endNodeId="";

        currentNodeId = targetNodeId;
        path = path + currentNodeId;
        pathName = stopNames.get(currentNodeId.split("_")[1]);
        while (!currentNodeId.equals(startNodeId)) {
            try {
                currentNodeId = parents.get(currentNodeId).getParent();
            }catch (Exception e){
                System.out.println("shortest path not found" + e.getMessage());
                break;
            }
            path = currentNodeId + "->" + path;
            pathName = stopNames.get(currentNodeId.split("_")[1])+"@"+ Main.MinutesToTime( graph.getNode(currentNodeId).time )+ "trip:" + graph.getNode(currentNodeId).trip + "type:" + graph.getNode(currentNodeId).type + "->" + pathName;
            if (currentNodeId == null)
                break;
        }

        //return path +"\n" + pathName;
        return pathName;
    }

    public String shortestPathToStation(String stationId, String targetNodeId, Map<String, String> stopNames) {
        String path = "";
        String pathName = "";
        String currentNodeId;
        String endNodeId="";

        currentNodeId = targetNodeId;
        path = path + currentNodeId;
        pathName = stopNames.get(currentNodeId.split("_")[1]);
        while (!graph.getNode(currentNodeId).stopId.equals(stationId)) {
            try {
                currentNodeId = parents.get(currentNodeId).getParent();
            }catch (Exception e){
                System.out.println("shortest path not found" + e.getMessage());
                break;
            }
            path = currentNodeId + "->" + path;
            pathName = stopNames.get(currentNodeId.split("_")[1])+"@"+ Main.MinutesToTime( graph.getNode(currentNodeId).time )+ "trip:" + graph.getNode(currentNodeId).trip + "type:" + graph.getNode(currentNodeId).type + "->" + pathName;
            if (currentNodeId == null)
                break;
        }

        //return path +"\n" + pathName;
        return pathName;
    }


    /**
     * Construct a string containing a description of the shortest path
     * between two nodes, node for node.
     *
     * @param startNodeId  - the source node
     * @param targetNodeId - the target node
     * @param stopNames
     * @return a string with a description of the path
     */
    public ArrayList<String> transferPatternToString(String startNodeId, String targetNodeId, Map<String, String> stopNames) {
        String currentNodeId;
        String previousNode = "";
        ArrayList<String> transferPattern = new ArrayList<>();
        //transferPattern.add(startNodeId +"@"+ Main.MinutesToTime( graph.getNode(startNodeId).time )+ "type:" + graph.getNode(startNodeId).type);
        currentNodeId = targetNodeId;
        int counter = 0;
        while (currentNodeId != startNodeId) {
            previousNode = currentNodeId;
            currentNodeId = parents.get(currentNodeId).getParent();
            if(graph.getNode(currentNodeId).type == 1 && graph.getNode(previousNode).type == 2){
                transferPattern.add(stopNames.get(currentNodeId.split("_")[1])+"@"+ Main.MinutesToTime( graph.getNode(currentNodeId).time )+ "type:" + graph.getNode(currentNodeId).type );
                //transferPattern.add(currentNodeId+"@"+ Main.MinutesToTime( graph.getNode(currentNodeId).time )+ "type:" + graph.getNode(currentNodeId).type );
            }
            if (currentNodeId == null)
                break;
            if(transferPattern.size() > 100){
                System.out.print("break");
                break;
            }
            if(counter > 500){
                break;
            }
            counter++;
        }
        Collections.reverse(transferPattern);
        transferPattern.add(0,stopNames.get(startNodeId.split("_")[1]) +"@"+ Main.MinutesToTime( graph.getNode(startNodeId).time )+ "type:" + graph.getNode(startNodeId).type);
        transferPattern.add(stopNames.get(targetNodeId.split("_")[1])+"@"+ Main.MinutesToTime( graph.getNode(targetNodeId).time )+ "type:" + graph.getNode(targetNodeId).type);
        //transferPattern.add(targetNodeId+"@"+ Main.MinutesToTime( graph.getNode(targetNodeId).time )+ "type:" + graph.getNode(targetNodeId).type);
        return transferPattern;
    }


    public TransferPattern transferPattern(String startStationId, String targetNodeId) {
        String currentNodeId;
        String previousNode = "";
        TransferPattern transferPattern = new TransferPattern();
        List<Double> timeOrder = new ArrayList<>();
        currentNodeId = targetNodeId;
        while (!graph.getNode(currentNodeId).stopId.equals(startStationId)) {
            previousNode = currentNodeId;
            try {
                currentNodeId = parents.get(currentNodeId).getParent();
            }catch (Exception e){
                System.out.println("no path");
                break;
            }
            TENode currentNode = graph.getNode(currentNodeId);
            if(currentNode.type == 1 && graph.getNode(previousNode).type == 2){
                transferPattern.transferPattern.add(currentNodeId.split("_")[1]);
                timeOrder.add(currentNode.time);
                //transferPattern.add(stopNames.get(currentNodeId.split("_")[1])+"@"+ Main.MinutesToTime( graph.getNode(currentNodeId).time )+ "type:" + graph.getNode(currentNodeId).type );
            }
            if (currentNodeId == null)
                break;

        }
        Collections.reverse(transferPattern.transferPattern);
        Collections.reverse(timeOrder);
        try{
            TENode startNode = graph.getNode(currentNodeId);
            TENode endNode = graph.getNode(targetNodeId);


            transferPattern.startTime = startNode.time;
            transferPattern.transferPattern.add(0,startNode.stopId);
            timeOrder.add(0,startNode.time);

            transferPattern.transferPattern.add(targetNodeId.split("_")[1]);
            transferPattern.endTime = endNode.time;
            transferPattern.transferTime = transferPattern.endTime - transferPattern.startTime;

            timeOrder.add(endNode.time);
            transferPattern.timeOrder = timeOrder;
        }catch (Exception e){
            System.out.println("wrong transfer pattern s" + targetNodeId);
        }
        return transferPattern;
    }



    /**
     * Gets the list of node IDs lying on the shortest path between two nodes.
     *
     * @param startNodeId  - the source node
     * @param targetNodeId - the target node
     * @return a {@link List} containing the IDs of all nodes on the shortest path
     */
    public List<TEActiveNode> getPathNodeIDs(String startNodeId, TEActiveNode targetNodeId) {
        List<TEActiveNode> result = new ArrayList<TEActiveNode>();

        TEActiveNode next = targetNodeId;
        TEActiveNode current;
        result.add(next);

        while (!next.getId().equals(startNodeId)) {
            current = parents.get(next.getId());
            if (current == null) break;
            result.add(current);
            next = current;
        }
        Collections.reverse(result);

        return result;
    }


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




    /**
     * Computes the shortest path between set of nodes to a target station{@link Graph}.
     *
     * @param startNodeIds  - the (source) nodes to start the search from
     * @param targetStation - the (target) station to reach
     * @return {@link Double#NEGATIVE_INFINITY} if no shortest path was found,
     * otherwise a value indicating the total cost of the shortest path
     */
    public ArrayList<String> setDijkstra(List<NodeOrder> startNodeIds, String startStopId, String targetStation) {
        ArrayList<String> stationsFound = new ArrayList<>();
        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<TEArc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;
        String endNodeId = "";

        TEActiveNode activeNode;
        TEActiveNode currentNode;

        //graph.getNode(startNodeId);

        this.activeNodes = new PriorityQueue<TEActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, TEActiveNode>();
        for (NodeOrder n : startNodeIds){
            activeNodes.add(new TEActiveNode(n.nodeId, 0.0, null, startStopId));
        }

        String previousStation="";
        while (activeNodes.size() != 0) {
            currentNode = activeNodes.poll();

            if (isVisited(currentNode.getId())) {
                continue;
            }

            // Mark as settled
            visitedNodeMarks.put(currentNode.getId(), currentNode.getDist());


            numSettledNodes++;

            // Found target
            if (currentNode.getStopId().equals(targetStation)) {
                stationsFound.add(currentNode.getId());
                if(shortestPathCost > currentNode.getDist()){
                    endNodeId = currentNode.getId();
                    shortestPathCost = currentNode.getDist();
                }
                //break;
            }

            // Graph was apparently not connected
            if (numSettledNodes > graph.getNumNodes()) {
                System.out.println("There is no short path between startNode and targetNode");
                break;
            }

            // Discover all adjacent nodes
            nodeAdjacentArcs = graph.getadjacentArc(currentNode.getId());
            // int currentLabel = graph.getNode(currentNode.getId()).getLabel();
            if (nodeAdjacentArcs == null)
                continue;
            for (int i = 0; i < nodeAdjacentArcs.size(); i++) {
                TEArc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = currentNode.getDist() + arc.getCost();
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new TEActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), arc.stopId);
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

        return stationsFound;
    }







    public String DijkstraWithHubStations(String startNodeId, String startStopId, String targetNodeId) {
        List<String> hubs = Stream.of("90000055","90000295","10000802","90000366","90000018","90000075","13001300","14001500","16040619").collect(Collectors.toList());
        this.visitedNodeMarks = new HashMap<String, Double>();
        double shortestPathCost = Double.MAX_VALUE;
        List<TEArc> nodeAdjacentArcs;
        int numSettledNodes = 0;
        double distToAdjNode;
        String endNodeId = "";

        TEActiveNode activeNode;
        TEActiveNode currentNode;



        this.activeNodes = new PriorityQueue<TEActiveNode>(100, activeNodeComparator);
        this.parents = new HashMap<String, TEActiveNode>();
        activeNodes.add(new TEActiveNode(startNodeId, 0.0, null, startStopId));

        String previousStation="";
        while (activeNodes.size() != 0) {
            currentNode = activeNodes.poll();
            //System.out.println(currentNode.getId());
            //System.out.println(currentNode.getDist());
            if (isVisited(currentNode.getId())) {
                continue;
            }

            // Mark as settled
            visitedNodeMarks.put(currentNode.getId(), currentNode.getDist());


            numSettledNodes++;

            // Found target
            if (currentNode.getStopId().equals(targetNodeId)) {
                if(shortestPathCost > currentNode.getDist()){
                    endNodeId = currentNode.getId();
                    shortestPathCost = currentNode.getDist();
                }
            }
            if(hubs.contains(currentNode.getStopId())){
                endNodeId = currentNode.getId();
                shortestPathCost = 0;
            }
            if(numSettledNodes == 10000){
                break;
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
                TEArc arc = nodeAdjacentArcs.get(i);
                distToAdjNode = currentNode.getDist() + arc.getCost();
                if (shortestPathCost <= distToAdjNode)
                    continue;
                // Ensure the node hasn't been settled
                if (!parents.containsKey(arc.getHeadNodeID()) || parents.get(arc.getHeadNodeID()).getDist() > distToAdjNode) { // && currentLabel <= graph.getNode(arc.getHeadNodeId()).getLabel()) {
                    activeNode = new TEActiveNode(arc.getHeadNodeID(), distToAdjNode, currentNode.getId(), arc.stopId);
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

        return shortestPathCost +";"+ endNodeId;
        //return endNodeId;
    }







}

