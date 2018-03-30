package jfskora;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestWidget1 {

    private Widget1 widget1;

    @Before
    public void before() {
        widget1 = new Widget1();
    }

    @Test
    public void testTwelve() {
        assertEquals((Long)12L, widget1.getTwelve());
    }

    @Test
    public void testSixtyTwo() {
        assertEquals((Long)62L, widget1.getSixtyTwo());
    }

    @Test
    public void testFactor() {
        assertEquals(new Double(62.0 / 12), widget1.getFactor());
    }
}
