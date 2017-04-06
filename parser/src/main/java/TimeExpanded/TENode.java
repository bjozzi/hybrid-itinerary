package TimeExpanded;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TENode {
    private String ID;
    private String station;
    private float lat;
    private float lng;

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public TENode(String ID, float lat, float lng) {
        this.ID = ID;
        this.lat = lat;
        this.lng = lng;
    }


}
