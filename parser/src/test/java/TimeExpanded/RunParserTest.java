package TimeExpanded;

import basic.Main;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static TimeExpanded.RunParser.NOcomparator;
import static TimeExpanded.RunParser.TimeInMinutes;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Created by bjozz on 4/13/2017.
 */
public class RunParserTest {
    @Test
    public void run() throws Exception {
        TEGraph g = new TEGraph();
        RunParser r = new RunParser();
        r.run();
        g = r.g;
        TEDijkstra d = new TEDijkstra(g);



        //thisted: 000785000100G -> 000787009900G

        String stopId = "000008600858";
        //Date yesterday = new Date(System.currentTimeMillis() - 1000L * 60L * 30L * 23L);
        ArrayList<NodeOrder> se = (ArrayList<NodeOrder>) r.nodeOrders.get(stopId).stream().parallel().filter(x->x.minute >= TimeInMinutes(new Date()) ).sorted(NOcomparator).collect(toList());
        String nodeId = se.get(0).nodeId;//"419956041041760_000008600858";//
        System.out.println(nodeId);
        String dk = d.computeShortestPath(nodeId, stopId, "000008600620G");//"000008600702G");//"000008600856G");//42027006425959_000008600702G//42026896419811_000008600620G
        System.out.println(dk);
        String endNodeId = dk.split(";")[1];
        System.out.println(g.getNode(nodeId).time);
        System.out.println(g.getNode(endNodeId).time);
        System.out.println(Main.MinutesToTime(g.getNode(nodeId).time));
        System.out.println(Main.MinutesToTime(g.getNode(endNodeId).time));
        System.out.println(d.shortestPathToString(nodeId, endNodeId, r.stopNames));

    }

}