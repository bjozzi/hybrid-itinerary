package basic;

import TD.TDActiveNode;
import TimeExpanded.RunParser;
import org.junit.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static TimeExpanded.RunParser.TimeInMinutes;
import static org.junit.Assert.*;

/**
 * Created by bjozz on 5/24/2017.
 */
public class MainTest {

    @org.junit.Test
    public void run(){
        String feed = "gtfsIceland";



        RunParser r = new RunParser();
        r.run(feed);

        Main tdd = new Main();
        tdd.stopNames = r.stopNames;
        tdd.Stops = r.stops;

        tdd.main("C:\\Users\\bjozz\\Desktop\\"+feed+"\\");
        tdd.createGraph();
        tdd.reverseGraph();

        String startNode = "90000018";
        String targetNode = "90000295";//"90000295";

        Date timeNow = new Date();
        double timeInMinutes = TimeInMinutes(timeNow);
        Map<String, Object> data = new HashMap<String, Object>();

        try{

            HashMap<String, TDActiveNode> parents = tdd.g.TimeDependentDijkstra(startNode, targetNode, timeInMinutes);
            double endTime = parents.get(targetNode).distance;
            List<Map<String, TDActiveNode>> trees = new ArrayList<>();
            List<Future<?>> futures = new ArrayList<Future<?>>();
            int TaskCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(TaskCount);
            int minutes = 30;
            double time = endTime;
            Object _lock = new Object();
            for (int i = 0; i < TaskCount; i++) {

                final int from = minutes / TaskCount * i;
                final int to = (i + 1 >= minutes) ? minutes : minutes / TaskCount * (i + 1);

                futures.add(executor.submit(() -> {

                    final String nodeId = targetNode;
                    for (double k = time + from; k < time + to; k++)
                        synchronized (_lock) {
                            HashMap<String, TDActiveNode> par = tdd.gReversed.ComputeISPT(targetNode, k, timeInMinutes);
                            trees.add(par);
                        }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
            List<Map<String, TDActiveNode>> reduced = Main.TreesContainingStartStation(trees, startNode);
            if (reduced.size() > 0) {
                //send the reduced tree
            }

            data.put("tree", reduced);
            data.put("path", Main.shortestPathName(startNode,targetNode,parents,tdd.stopNames) );
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}