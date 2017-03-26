/**
 * Created by Karlis on 2017.03.23..
 */
public class Transfer {
   public String from_stop_id,to_stop_id,transfer_type,min_transfer_time;

    public Transfer(String from_stop_id, String to_stop_id, String transfer_type, String min_transfer_time) {
        this.from_stop_id = from_stop_id;
        this.to_stop_id = to_stop_id;
        this.transfer_type = transfer_type;
        if (min_transfer_time.equals(""))
            min_transfer_time = "0";
        this.min_transfer_time = min_transfer_time;
    }
}
