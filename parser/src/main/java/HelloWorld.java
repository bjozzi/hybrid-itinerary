import TimeExpanded.*;
import basic.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static TimeExpanded.RunParser.NOcomparator;
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

        TEGraph g = (TEGraph) context.getAttribute("graph");
        HashMap<String, List<TransferPattern>> transferPatterns = (HashMap<String, List<TransferPattern>>) context.getAttribute("transferPatterns");
        Map<String, ArrayList<NodeOrder>> nodeOrders = (Map<String, ArrayList<NodeOrder>>) context.getAttribute("nodeOrders");
        Map<String, String> stopNames = (Map<String, String>) context.getAttribute("stopNames");
        TEDijkstra d = new TEDijkstra(g);
        //String shortestPath = teDijkstra.computeShortestPath(graph);


        String stopId = "90000317";
        String targetNodeId = "90000295";
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(RunParser.NOcomparator).collect(toList());
        String nodeId = se.get(0).nodeId;
        String dk = d.computeShortestPath(nodeId, stopId, targetNodeId);
        String endNodeId = dk;
        // Return some cliched textual content
        return d.shortestPathToString(nodeId, endNodeId, stopNames);
    }


    @GET
    @Path("/tp/{param}")
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String getTransferPattern(@PathParam("param") String profile) {

        TEGraph g = (TEGraph) context.getAttribute("graph");
        HashMap<String, List<TransferPattern>> transferPatterns = (HashMap<String, List<TransferPattern>>) context.getAttribute("transferPatterns");
        Map<String, ArrayList<NodeOrder>> nodeOrders = (Map<String, ArrayList<NodeOrder>>) context.getAttribute("nodeOrders");
        Map<String, String> stopNames = (Map<String, String>) context.getAttribute("stopNames");
        TEDijkstra d = new TEDijkstra(g);
        //String shortestPath = teDijkstra.computeShortestPath(graph);
        Map<String, Object> data = new HashMap<String, Object>();
        Map<List<String>, List<List<Double>>> tps = new HashMap<>();

        String result = "";
        String stopId = "90000317";
        String targetNodeId = "90000748";//"90000295";
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(stopId).stream().parallel()
                .filter(x->x.type == 2 )
                .filter(x->x.minute >= TimeInMinutes(new Date()) ).distinct().sorted(NOcomparator).collect(toList());

        List<TransferPattern> localPath = new ArrayList<>();
        int i = 0;
        boolean global = false;
        String endNodeId = "";
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            String dk = d.DijkstraWithHubStations(nodeId, stopId, targetNodeId);
            endNodeId = dk.split(";")[1];
            String dist = dk.split(";")[0];
            double startTime = g.getNode(nodeId).time;
            double endTime = g.getNode(endNodeId).time;
            double duration = endTime - startTime;
            System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
            System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));

            if(dist.equals("0.0")){
                global = true;
                TransferPattern localPattern = d.transferPattern(nodeId,endNodeId);
                localPath.add(localPattern);
            }else{
                TransferPattern localPattern = d.transferPattern(nodeId,endNodeId);
                localPath.add(localPattern);
                result += d.shortestPathToString(nodeId, endNodeId, stopNames);
                result += "######################################################################################";
            }
            i++;
            if(i>5)break;
        }

        Map<List<String>, List<List<Double>>> localtps = new HashMap<>();
        for (TransferPattern tp : localPath){
            if(localtps.containsKey(tp.transferPattern)){
                localtps.get(tp.transferPattern).add(tp.timeOrder);
            }else{
                List<List<Double>> list = new ArrayList<>();
                list.add(tp.timeOrder);
                localtps.put(tp.transferPattern, list);
            }
        }

        if(global){
            for(TransferPattern tp : transferPatterns.get(endNodeId.split("_")[1]).stream().filter(z->z.endStation.equals(targetNodeId)).collect(toList())){
                tps.put(tp.transferPattern, tp.timeOrders);
            }
        }




        data.put("localPatterns", localtps);
        data.put("GlobalPatterns", tps);
        //data.put("path", result);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(data);

        return json;
    }


}