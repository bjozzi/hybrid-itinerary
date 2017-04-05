import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karlis on 2017.03.23..
 */
public class GeoJson {
    private String type;
    private Geometry geometry;
    private Map<String, Object> properties;

    public GeoJson() {
        super();
    }

    public GeoJson(String type, Geometry geometry, Map<String, Object> properties) {
        super();
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public static GeoJson arcToGeoJson(Node a, Node b) {
        GeoJson gj = new GeoJson();
        gj.setProperties(new HashMap<String, Object>());
        gj.setType("Feature");
        GeoJson.Geometry g = new GeoJson.Geometry();
        g.setType("LineString");
        List<Object> c = new ArrayList<Object>();
        List<Float> acoo = new ArrayList<Float>();
        List<Float> bcoo = new ArrayList<Float>();
        acoo.add(a.getLng());
        acoo.add(a.getLat());
        bcoo.add(b.getLng());
        bcoo.add(b.getLat());

        c.add(acoo);
        c.add(bcoo);

        g.setCoordinates(c);
        gj.setGeometry(g);
        return gj;
    }

    public static GeoJson arcToGeoJson(Node a, Node b, Integer traffic) {
        GeoJson arc = arcToGeoJson(a, b);
        arc.getProperties().put("traffic", traffic);
        return arc;
    }

    public static class Geometry {
        private String type;
        private List<Object> coordinates;

        public Geometry() {
        }

        public Geometry(String type, List<Object> coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Object> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Object> coordinates) {
            this.coordinates = coordinates;
        }

    }
}