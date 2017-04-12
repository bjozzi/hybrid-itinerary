import TimeExpanded.TEDijkstra;
import TimeExpanded.TEGraph;
import basic.Dijkstra;
import basic.Graph;

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
        TEGraph g = new TEGraph();
        g.createNode("1", "A", 48, 1);
        g.createNode("2", "A", 54, 1);
        g.createNode("3", "A", 60, 1);
        g.createNode("4", "A", 66, 1);

        g.createNode("5", "A", 60, 1);g.createNode("22", "C", 64, 2);
        g.createNode("6", "A", 72, 1);g.createNode("23", "C", 76, 2);
        g.createNode("7", "A", 84, 1);g.createNode("24", "C", 88, 2);
        g.createNode("8", "A", 96, 1);g.createNode("25", "C", 100, 2);

        g.createNode("9", "B", 48, 1);g.createNode("26", "C", 51, 2);
        g.createNode("10", "B", 51, 1);g.createNode("27", "C", 54, 2);
        g.createNode("18", "B", 53, 2);
        g.createNode("11", "B", 54, 1);g.createNode("28", "C", 57, 2);
        g.createNode("12", "B", 57, 1);g.createNode("29", "C", 60, 2);
        g.createNode("19", "B", 59, 2);
        g.createNode("13", "B", 60, 1);g.createNode("30", "C", 63, 2);
        g.createNode("14", "B", 63, 1);g.createNode("31", "C", 66, 2);
        g.createNode("20", "B", 65, 2);
        g.createNode("15", "B", 66, 1);g.createNode("32", "C", 69, 2);
        g.createNode("16", "B", 69, 1);g.createNode("33", "C", 72, 2);
        g.createNode("21", "B", 71, 2);
        g.createNode("17", "B", 72, 1);g.createNode("34", "C", 75, 2);

        g.addEdge("1","2",6, "A");
        g.addEdge("2","3",6, "A");
        g.addEdge("3","5",0, "A");
        g.addEdge("5","4",6, "A");

        g.addEdge("4","6",6, "A");
        g.addEdge("6","7",12, "A");
        g.addEdge("7","8",12, "A");

        g.addEdge("9","10",3, "B");
        g.addEdge("10","18",2, "B");
        g.addEdge("18","11",1, "B");
        g.addEdge("11","12",3, "B");
        g.addEdge("12","19",2, "B");
        g.addEdge("19","13",1, "B");
        g.addEdge("13","14",3, "B");
        g.addEdge("14","20",2, "B");
        g.addEdge("20","15",1, "B");
        g.addEdge("15","16",3, "B");
        g.addEdge("16","21",2, "B");
        g.addEdge("21","17",1, "B");


        g.addEdge("1", "18",5, "B");
        g.addEdge("2", "19",5, "B");
        g.addEdge("3", "20",5, "B");
        g.addEdge("4", "21",5, "B");

        g.addEdge("5", "22",4, "C");
        g.addEdge("6", "23",4, "C");
        g.addEdge("7", "24",4, "C");
        g.addEdge("8", "25",4, "C");

        g.addEdge("9", "26",3, "C");
        g.addEdge("10", "27",3, "C");
        g.addEdge("11", "28",3, "C");
        g.addEdge("12", "29",3, "C");
        g.addEdge("13", "30",3, "C");
        g.addEdge("14", "31",3, "C");
        g.addEdge("15", "32",3, "C");
        g.addEdge("16", "33",3, "C");
        g.addEdge("17", "34",3, "C");

        TEDijkstra d = new TEDijkstra(g);

        String  dic = d.computeShortestPath("1", "A", "C");
        String endNodeId = dic.split(";")[1];
        //System.out.println(d.shortestPathToString("1", endNodeId));
        //assertEquals(dic, 57.0);
    }

}