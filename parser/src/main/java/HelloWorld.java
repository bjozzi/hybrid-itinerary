import TD.TDActiveNode;
import TD.TDGraph;
import TimeExpanded.*;
import basic.ActiveNode;
import basic.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;

import static TimeExpanded.RunParser.NOcomparator;
import static TimeExpanded.RunParser.TimeInMinutes;
import static basic.Main.TreesContainingStartStation;
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
    @Path("/tp")
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String getTransferPattern(@QueryParam("start") String startId, @QueryParam("target") String targetId) {

        TEGraph g = (TEGraph) context.getAttribute("graph");
        HashMap<String, List<TransferPattern>> transferPatterns = (HashMap<String, List<TransferPattern>>) context.getAttribute("transferPatterns");
        Map<String, ArrayList<NodeOrder>> nodeOrders = (Map<String, ArrayList<NodeOrder>>) context.getAttribute("nodeOrders");
        Map<String, String> stopNames = (Map<String, String>) context.getAttribute("stopNames");
        TEDijkstra d = new TEDijkstra(g);
        //String shortestPath = teDijkstra.computeShortestPath(graph);
        Map<String, Object> data = new HashMap<String, Object>();
        Map<List<String>, List<List<Double>>> tps = new HashMap<>();

        String result = "";
        //String stopId = "90000317";
        //String targetNodeId = "90000748";//"90000295";
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) nodeOrders.get(startId).stream().parallel()
                .filter(x->x.type == 2 )
                .filter(x->x.minute >= TimeInMinutes(new Date()) ).distinct().sorted(NOcomparator).collect(toList());

        List<TransferPattern> localPath = new ArrayList<>();
        int i = 0;
        boolean global = false;
        String endNodeId = "";
        for(NodeOrder n : se){
            String nodeId = n.nodeId;
            String dk = d.DijkstraWithHubStations(nodeId, startId, targetId);
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
            //if(i>5)break;
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
            for(TransferPattern tp : transferPatterns.get(endNodeId.split("_")[1]).stream().filter(z->z.endStation.equals(targetId)).collect(toList())){
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

    @GET
    @Path("/tdd")
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
    public String timeDependent(@QueryParam("start") String startNode, @QueryParam("target") String targetNode, ContainerResponseContext responseContext) throws ParseException {


        TDGraph graph = (TDGraph) context.getAttribute("tddGraph");
        TDGraph gReversed = (TDGraph) context.getAttribute("reversedGraph");

        Map<String, String> stopNames = (Map<String, String>) context.getAttribute("stopNames");
        Main tdd = (Main) context.getAttribute("mainTD");
        Date timeNow = new Date();
        double timeInMinutes = TimeInMinutes(timeNow);
        Map<String, Object> data = new HashMap<String, Object>();

        try{

        HashMap<String, TDActiveNode> parents = graph.TimeDependentDijkstra(startNode, targetNode, timeInMinutes);
        double endTime = parents.get(targetNode).distance;
        List<Map<String, TDActiveNode>> trees = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<Future<?>>();
        int TaskCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(TaskCount);
        int minutes = 180;
        double time = endTime;
        double toTime = endTime+minutes;


        for (double k = time ; k < toTime; k++){
            HashMap<String, TDActiveNode> par = gReversed.ComputeISPT(targetNode, k, timeInMinutes);
            trees.add(par);
        }
        List<Map<String, TDActiveNode>> reduced = Main.TreesContainingStartStation(trees, startNode);
        /*if (reduced.size() > 0) {
            for(int i = 0; i<reduced.size(); i++){
                data.put(String.valueOf(i), gReversed.shortestPath(targetNode, startNode, reduced.get(i)));
            }
        }*/
            data.put("reduced", reduced);
            data.put("path",  Main.shortestPathName(startNode,targetNode,parents,stopNames) );

        }catch (Exception e){
            e.printStackTrace();
        }

        //data.put("path", result);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(data);


        return compress(json);
    }


    public String compress(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        String result = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            result = out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}