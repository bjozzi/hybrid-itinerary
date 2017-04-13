package TimeExpanded;

/**
 * Created by Karlis on 2017.03.23..
 */
public class TENode {
    public String ID;
    public String stopId;
    public double time;
    public int type;

    public TENode(String ID, String stopId, double time, int type) {
        this.ID = ID;
        this.stopId = stopId;
        this.time = time;
        this.type = type;
    }


}
