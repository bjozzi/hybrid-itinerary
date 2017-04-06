package basic;

/**
 * Created by Karlis on 2017.03.25..
 */
public class Test {
    public static void main(String[] args) {
        testComputeShortestPath();
    }

    static void testComputeShortestPath() {
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
        Double dk = d.computeShortestPath("1", "4");

        if(dk == 3)
            System.out.println(dk+" "+d.shortestPathToString("1","4"));
    else
            System.out.println("wrong "+dk );
    }
}
