/**
 * Created by Karlis on 2017.03.25..
 */
class DijkstraTest {

    public static void main(String[] args) {
        testComputeShortestPath();
    }

    void testComputeShortestPath() {
        Graph graph = new Graph();
        for (int i = 0; i < 5; i++) {
            float lat = Float.parseFloat(1);
            float lon = Float.parseFloat(1);
            g.createNode(i, lat, lon);
        }
        g.addEdge(1, 2, 1, 1);
        g.addEdge(1, 5, 2, 3);
        g.addEdge(1, 3, 3, 1);
        g.addEdge(2, 5, 4, 1);
        g.addEdge(2, 4, 5, 3);
        g.addEdge(3, 4, 6, 3);
        g.addEdge(5, 4, 7, 1);

        Dijkstra d = new Dijkstra(graph);
        Double dk = d.computeShortestPath("1", "4");
        assertEquals(dk, 3)
    }
}
