import TimeExpanded.NodeOrder;
import TimeExpanded.RunParser;
import TimeExpanded.TEDijkstra;
import TimeExpanded.TEGraph;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static TimeExpanded.RunParser.TimeInMinutes;
import static java.util.stream.Collectors.toList;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/helloworld")
public class HelloWorld {
    // The Java method will process HTTP GET requests


    @Context
    private ServletContext context;

    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String getClichedMessage() {

        TEGraph graph = (TEGraph) context.getAttribute("graph");
        Map<String, ArrayList<NodeOrder>> nodeOrders = (Map<String, ArrayList<NodeOrder>>) context.getAttribute("nodeOrders");
        Map<String, String> stopNames = (Map<String, String>) context.getAttribute("stopNames");
        TEDijkstra teDijkstra = new TEDijkstra(graph);
        //String shortestPath = teDijkstra.computeShortestPath(graph);


        String stopId = "000008600858";
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(RunParser.NOcomparator).collect(toList());
        String nodeId = se.get(0).nodeId;
        System.out.println(nodeId);
        String dk = teDijkstra.computeShortestPath(nodeId, stopId, "000008600020G");
        System.out.println(dk);
        String endNodeId = dk.split(";")[1];
        System.out.println(teDijkstra.shortestPathToString(nodeId, endNodeId, stopNames));
        // Return some cliched textual content
        return teDijkstra.shortestPathToString(nodeId, endNodeId, stopNames);
    }
}