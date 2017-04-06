package basic;

/**
 * Created by Karlis on 2017.03.23..
 */
public class stop {
    public String stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, location_type, parent_station;

    public stop(String stop_id, String stop_code, String stop_name, String stop_desc, String stop_lat, String stop_lon, String location_type, String parent_station) {
        this.stop_id = stop_id;
        this.stop_code = stop_code;
        this.stop_name = stop_name;
        this.stop_desc = stop_desc;
        this.stop_lat = stop_lat;
        this.stop_lon = stop_lon;
        this.location_type = location_type;
        this.parent_station = parent_station;
    }
}
