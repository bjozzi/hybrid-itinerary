package API;

import TimeDependent.TDDijkstra;
import TimeExpanded.RunParser;
import TimeExpanded.TEGraph;
import basic.Main;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by bjozz on 4/13/2017.
 */
public class LoadConfigurationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        String feed = "gtfsIceland";



        RunParser r = new RunParser();
        r.run(feed);
        //r.run("gtfs");
        context.setAttribute("graph", r.g);
        context.setAttribute("nodeOrders", r.nodeOrders);
        context.setAttribute("stopNames", r.stopNames);
        context.setAttribute("transferPatterns", r.transferPatterns);


        Main tdd = new Main();
        tdd.stopNames = r.stopNames;
        tdd.Stops = r.stops;

        tdd.main("C:\\Users\\bjozz\\Desktop\\"+feed+"\\");
        tdd.createGraph();
        tdd.reverseGraph();
        TDDijkstra d = new TDDijkstra(tdd.g);
        TDDijkstra dReversed = new TDDijkstra(tdd.gReversed);

        context.setAttribute("tddGraph", d);
        context.setAttribute("stopNames", tdd.stopNames);
        context.setAttribute("mainTD", tdd);



    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        context.removeAttribute("graph");
    }
}
