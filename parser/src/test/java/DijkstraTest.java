import TimeExpanded.NodeOrder;
import TimeExpanded.TEActiveNode;
import TimeExpanded.TEDijkstra;
import TimeExpanded.TEGraph;
import basic.Dijkstra;
import basic.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by bjozz on 4/5/2017.
 */
public class DijkstraTest {
    @org.junit.Test
    public void computeShortestPath() throws Exception {

        Graph g = new Graph();
        for (int i = 1; i <= 5; i++) {
            float lat = 1;
            float lon = 1;
            g.createNode(i+"", lat, lon);
        }
        g.addEdge("1", "2", 1);
        g.addEdge(1+"", 5+"", 3);
        g.addEdge(1+"", 3+"", 1);
        g.addEdge(2+"", 5+"",1);
        g.addEdge(2+"", 4+"",3);
        g.addEdge(3+"", 4+"", 3);
        g.addEdge(5+"", 4+"", 1);

        Dijkstra d = new Dijkstra(g);
        double dk = d.computeShortestPath("1", "4");

        assertEquals(dk, 3.0, 1);

        if(dk == 3)
            System.out.println(dk+" "+d.shortestPathToString("1","4"));
        else
            System.out.println("wrong "+dk );

    }

    @org.junit.Test
    public void TEDijkstraTest() throws Exception{
        TEGraph g = getTeGraph();

        TEDijkstra d = new TEDijkstra(g);

        String  dic = d.computeShortestPath("1", "A", "C");
        String endNodeId = dic.split(";")[1];
        //System.out.println(d.shortestPathToString("1", endNodeId));
        //assertEquals(dic, 57.0);
    }



    private TEGraph getTeGraph() {
        TEGraph g = new TEGraph();
        /*g.createNode("1_A", "A", 48, 1);
        g.createNode("2_A", "A", 54, 1);
        g.createNode("3_A", "A", 60, 1);
        g.createNode("4_A", "A", 66, 1);

        g.createNode("5_A", "A", 60, 1);
        g.createNode("22_C", "C", 64, 2);
        g.createNode("6_A", "A", 72, 1);
        g.createNode("23_C", "C", 76, 2);
        g.createNode("7_A", "A", 84, 1);
        g.createNode("24_C", "C", 88, 2);
        g.createNode("8_A", "A", 96, 1);
        g.createNode("25_C", "C", 100, 2);

        g.createNode("9_B", "B", 48, 1);
        g.createNode("26_C", "C", 51, 2);
        g.createNode("10_B", "B", 51, 1);
        g.createNode("27_C", "C", 54, 2);
        g.createNode("18_B", "B", 53, 2);
        g.createNode("11_B", "B", 54, 1);
        g.createNode("28_C", "C", 57, 2);
        g.createNode("12_B", "B", 57, 1);
        g.createNode("29_C", "C", 60, 2);
        g.createNode("19_B", "B", 59, 2);
        g.createNode("13_B", "B", 60, 1);
        g.createNode("30_C", "C", 63, 2);
        g.createNode("14_B", "B", 63, 1);
        g.createNode("31_C", "C", 66, 2);
        g.createNode("20_B", "B", 65, 2);
        g.createNode("15_B", "B", 66, 1);
        g.createNode("32_C", "C", 69, 2);
        g.createNode("16_B", "B", 69, 1);
        g.createNode("33_C", "C", 72, 2);
        g.createNode("21_B", "B", 71, 2);
        g.createNode("17_B", "B", 72, 1);
        g.createNode("34_C", "C", 75, 2);

        g.addEdge("1_A","2_A",6, "A");
        g.addEdge("2_A","3_A",6, "A");
        g.addEdge("3_A","5_A",0, "A");
        g.addEdge("5_A","4_A",6, "A");
        g.addEdge("4_A","6_A",6, "A");
        g.addEdge("6_A","7_A",12, "A");
        g.addEdge("7_A","8_A",12, "A");

        g.addEdge("9_B","10_B",3, "B");
        g.addEdge("10_B","18_B",2, "B");
        g.addEdge("18_B","11_B",1, "B");
        g.addEdge("11_B","12_B",3, "B");
        g.addEdge("12_B","19_B",2, "B");
        g.addEdge("19_B","13_B",1, "B");
        g.addEdge("13_B","14_B",3, "B");
        g.addEdge("14_B","20_B",2, "B");
        g.addEdge("20_B","15_B",1, "B");
        g.addEdge("15_B","16_B",3, "B");
        g.addEdge("16_B","21_B",2, "B");
        g.addEdge("21_B","17_B",1, "B");


        g.addEdge("1_A", "18_B",5, "B");
        g.addEdge("2_A", "19_B",5, "B");
        g.addEdge("3_A", "20_B",5, "B");
        g.addEdge("4_A", "21_B",5, "B");

        g.addEdge("5_A", "22_C",4, "C");
        g.addEdge("6_A", "23_C",4, "C");
        g.addEdge("7_A", "24_C",4, "C");
        g.addEdge("8_A", "25_C",4, "C");

        g.addEdge("9_B", "26_C",3, "C");
        g.addEdge("10_B", "27_C",3, "C");
        g.addEdge("11_B", "28_C",3, "C");
        g.addEdge("12_B", "29_C",3, "C");
        g.addEdge("13_B", "30_C",3, "C");
        g.addEdge("14_B", "31_C",3, "C");
        g.addEdge("15_B", "32_C",3, "C");
        g.addEdge("16_B", "33_C",3, "C");
        g.addEdge("17_B", "34_C",3, "C");*/
        return g;
    }

}