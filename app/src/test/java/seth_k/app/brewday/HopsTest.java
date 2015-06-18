package seth_k.app.brewday;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Seth on 6/15/2015.
 */
public class HopsTest {
    @Test
    public void setsVarietyNameAndAlphaInConstructor() {
        Hops hop = new Hops("Cascade");
        assertEquals(hop.mName, "Cascade");
    }

    @Test
    public void toStringOutputsQuantityVarietyAndDuration() {
        Hops hop = new Hops("Cascade");
        hop.setBoilTime(60);

        hop.setAmount(0.25);
        assertEquals(".25oz Cascade hops (60 min)", hop.toString());
        hop.setAmount(2.0);
        assertEquals("2.00oz Cascade hops (60 min)", hop.toString());

    }
}