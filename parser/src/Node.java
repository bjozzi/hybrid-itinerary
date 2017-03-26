/**
 * Created by Karlis on 2017.03.23..
 */
public class Node {
    private String ID;
    private float lat;
    private float lng;

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public Node(String ID, float lat, float lng) {
        this.ID = ID;
        this.lat = lat;
        this.lng = lng;
    }


}
