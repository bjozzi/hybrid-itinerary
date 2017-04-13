package TimeExpanded;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bjozz on 4/12/2017.
 */
public class GTFSReaderTest {
    @Test
    public void main() throws Exception {
        GTFSReader p = new GTFSReader();
        GTFSReader p2 = new GTFSReader();
        p.run();
        p2.sequential();
        assertEquals(p.stopTimes.size(), 1332119);
        assertEquals(p.weekday.size(), 608);
        assertEquals(p.saturday.size(), 450);
        assertEquals(p.sunday.size(), 425);
        assertEquals(p.trips.size(), 46202);
        assertEquals(p.transfers.size(), 25170);
    }

    @Test
    public void parseFile() throws Exception {

    }

}