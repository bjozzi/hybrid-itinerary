package TD;


import basic.Main;

import java.util.*;

/**
 * Created by Karlis on 2017.05.18..
 */
public class TDGraph {
    private HashMap<String, TDNode> Nodes;
    private HashMap<String, List<TDArc>> adjacentArcs;

    public TDGraph() {
        Nodes = new HashMap<>();
        adjacentArcs = new HashMap<>();
    }

    public HashMap<String, TDNode> getNodes() {
        return Nodes;
    }

    public HashMap<String, List<TDArc>> getAdjacentArcs() {
        return adjacentArcs;
    }

    public List<TDArc> getAdjacentArcs(String nodeID) {
        return adjacentArcs.get(nodeID);
    }

    public void setNodes(HashMap<String, TDNode> nodes) {
        Nodes = new HashMap<String, TDNode>(nodes);
    }

    public void createNode(String ID, String Name, String lat, String lng) {
        TDNode tdn = new TDNode(ID, Name, lat, lng);
        setNodes(tdn);
    }

    public void addArc(String from, String to, double cost, double accessTime, String tripID) {
        TDNode uNode = Nodes.get(from);
        TDNode vNode = Nodes.get(to);
        if (uNode == null || vNode == null)
            return;

        if (adjacentArcs.get(from) != null && adjacentArcs.get(from).stream().anyMatch(x -> x.toID.equals(to))) {
            adjacentArcs.get(from).stream().filter(x -> x.toID.equals(to)).forEach(y -> y.setAccessTime(accessTime, tripID));
        } else {
            TDArc arc = new TDArc(to, cost, accessTime, tripID);
            setAdjacentArcs(from, arc);
        }
    }

    public void setNodes(TDNode node) {
        Nodes.put(node.nodeID, node);
    }

    public void setAdjacentArcs(String fromID, TDArc arc) {
        if (!adjacentArcs.containsKey(fromID))
            adjacentArcs.put(fromID, new ArrayList<TDArc>());
        adjacentArcs.get(fromID).add(arc);
    }


    public HashMap<String, TDActiveNode> TimeDependentDijkstra(String startNode, String targetNode, double StartTime) {
        PriorityQueue<TDActiveNode> activeNodes = new PriorityQueue<>(100);
        HashMap<String, TDActiveNode> parents = new HashMap<>();
        HashSet<String> visitedNodes = new HashSet<>();
        double distanceToAdj = 0;
        TDActiveNode currentNode;
        TDActiveNode activeNode = new TDActiveNode(startNode, StartTime, "", "");
        parents.put(startNode, activeNode);
        activeNodes.add(activeNode);
        while (!activeNodes.isEmpty()) {
            currentNode = activeNodes.poll();
            if (visitedNodes.contains(currentNode.nodeID))
                continue;

            visitedNodes.add(currentNode.nodeID);

            if (targetNode.equals(currentNode.nodeID))
                break;
            List<TDArc> adjac = getAdjacentArcs(currentNode.nodeID);
            if (adjac == null)
                continue;
            for (TDArc arc : adjac) {
               /* if (visitedNodes.contains(arc.toID))
                    continue;*/
                Map.Entry<Double, List<String>> movingaround = arc.getAccessTime(currentNode.distance);//, currentNode.trips);
                if (movingaround == null)
                    continue;
                double time = movingaround.getKey();
                if (time == -1)
                    time = currentNode.distance;
                distanceToAdj = time + arc.cost;
                boolean isInParents = parents.containsKey(arc.toID);
                if (!isInParents || parents.get(arc.toID).distance > distanceToAdj) {
                    activeNode = new TDActiveNode(arc.toID, distanceToAdj, currentNode.nodeID, movingaround.getValue());
                    if (!isInParents) {
                        parents.put(activeNode.nodeID, activeNode);
                    } else {
                        parents.get(arc.toID).distance = distanceToAdj;
                        parents.get(arc.toID).parentID = currentNode.nodeID;
                    }
                    activeNodes.add(activeNode);
                }
            }
        }
        String parentNode = targetNode;
        String path = "";
        path = Nodes.get(parentNode).Name + "@" + Main.MinutesToTime(parents.get(parentNode).distance);

        while (!parentNode.equals(startNode)) {
            parentNode = parents.get(parentNode).parentID;
            if (parentNode.equals(""))
                break;
            path = Nodes.get(parentNode).Name + "@" + Main.MinutesToTime(parents.get(parentNode).distance) + ">" + path;
        }
        System.out.println(path);

        return parents;
    }

    public HashMap<String, TDActiveNode> ComputeISPT(String startNode, double StartTime, double endTime) {
        PriorityQueue<TDActiveNode> activeNodes = new PriorityQueue<>(100);
        HashMap<String, TDActiveNode> parents = new HashMap<>();
        HashSet<String> visitedNodes = new HashSet<>();
        double distanceToAdj = 0;
        TDActiveNode currentNode;
        TDActiveNode activeNode = new TDActiveNode(startNode, 0, "", "", StartTime);
        parents.put(startNode, activeNode);
        activeNodes.add(activeNode);
        while (!activeNodes.isEmpty()) {
            currentNode = activeNodes.poll();
            if (visitedNodes.contains(currentNode.nodeID))
                continue;

            visitedNodes.add(currentNode.nodeID);

            if (currentNode.time <= endTime)
                continue;
            List<TDArc> adjac = getAdjacentArcs(currentNode.nodeID);
            if (adjac == null)
                continue;
            for (TDArc arc : adjac) {
                Map.Entry<Double, List<String>> movingaround = arc.getAccessTimeBack(currentNode.time);//, currentNode.trips);
                if (movingaround == null)
                    continue;
                double time = movingaround.getKey();
                if (time == -1)
                    time = currentNode.distance;
                distanceToAdj = currentNode.distance + currentNode.time - time + arc.cost;
                boolean isInParents = parents.containsKey(arc.toID);
                if (time - arc.cost < endTime)
                    continue;
                if (!isInParents || parents.get(arc.toID).distance > distanceToAdj) {
                    activeNode = new TDActiveNode(arc.toID, distanceToAdj, currentNode.nodeID, movingaround.getValue(), time - arc.cost);
                    if (!isInParents) {
                        parents.put(activeNode.nodeID, activeNode);
                    } else {
                        parents.get(arc.toID).distance = distanceToAdj;
                        parents.get(arc.toID).parentID = currentNode.nodeID;
                    }
                    activeNodes.add(activeNode);
                }
            }
        }
        return parents;
    }
}
