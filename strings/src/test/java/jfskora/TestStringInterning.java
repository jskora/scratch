package jfskora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class TestStringInterning {

    @Test
    public void testliteralsAreTheSame() {
        final String temp1 = "abc";
        final String temp2 = "abc";
        final String temp3 = new StringBuilder().append("a").append("bc").toString();

        assertEquals(temp1, temp2);
        assertEquals(temp1, temp3);
        assertEquals(temp2, temp3);

        assertSame(temp1, temp2);
        assertNotSame(temp1, temp3);
        assertNotSame(temp2, temp3);
    }
}
