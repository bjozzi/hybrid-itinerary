package basic;

/**
 * Created by Karlis on 2017.03.23..
 */
public class Agency {
    public String agency_id;
    public String agency_name;
    public String agency_url;
    public String agency_timezone;
    public String agency_lang;
    public String agency_phone;


    public Agency(String agency_id, String agency_name, String agency_url, String agency_timezone, String agency_lang, String agency_phone) {
        this.agency_id = agency_id;
        this.agency_name = agency_name;
        this.agency_url = agency_url;
        this.agency_timezone = agency_timezone;
        this.agency_lang = agency_lang;
        this.agency_phone = agency_phone;
    }
}
