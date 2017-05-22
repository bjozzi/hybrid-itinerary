package API;

import TimeExpanded.RunParser;
import TimeExpanded.TEGraph;

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
        RunParser r = new RunParser();
        r.run("gtfsIceland");
        //r.run("gtfs");
        context.setAttribute("graph", r.g);
        context.setAttribute("nodeOrders", r.nodeOrders);
        context.setAttribute("stopNames", r.stopNames);
        context.setAttribute("transferPatterns", r.transferPatterns);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        context.removeAttribute("graph");
    }
}
