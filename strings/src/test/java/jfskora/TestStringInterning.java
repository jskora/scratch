package jfskora;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class TestStringInterning {

    @Test
    public void testEqualsVsSameNoIntern() {
        final String temp1 = "abc";
        final String temp2 = "abc";
        final String temp3 = new StringBuilder().append("a").append("bc").toString();

        // all values are equal
        assertEquals(temp1, temp2);
        assertEquals(temp1, temp3);
        assertEquals(temp2, temp3);

        // temp1 and temp2 are same string, temp3 is not
        assertSame(temp1, temp2);
        assertNotSame(temp1, temp3);
        assertNotSame(temp2, temp3);
    }

    @Test
    public void testEqualsVsSameWithIntern() {
        final String temp1 = "static string value";
        String temp2 = temp1;
        assertEquals(temp1, temp2);
        assertSame(temp1, temp2);

        String temp3 = "static string value";
        assertEquals(temp1, temp3);
        assertSame(temp1, temp3);

        String temp4 = "static string".concat(" value");
        assertEquals(temp1, temp4);
        assertNotSame(temp1, temp4);

        String temp5 = temp4;
        assertEquals(temp1, temp5);
        assertNotSame(temp1, temp5);
        assertSame(temp4, temp5);

        String temp6 = temp4.intern();
        assertEquals(temp1, temp6);
        assertSame(temp1, temp6);
        assertNotSame(temp4, temp6);

        String temp7= temp1.intern();
        assertEquals(temp1, temp7);
        assertSame(temp1, temp7);
        assertNotSame(temp4, temp7);
    }

    @Test
    public void testBulkStringsCompareWithAndWithoutInterning() {
        final List<String> bulkStrings0 = new ArrayList<>();
        final List<String> bulkStrings1 = new ArrayList<>();
        final List<String> bulkStrings2 = new ArrayList<>();
        final NumberFormat numberFormat = NumberFormat.getInstance();

        final long memTime0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int i = 0; i < 1000000; i++) {
            bulkStrings0.add("base string".concat(" suffix") + " and more");
        }
        final long memTime1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int i = 0; i < bulkStrings0.size() - 1; i++) {
            assertEquals(bulkStrings0.get(i), bulkStrings0.get(i+1));
            assertNotSame(bulkStrings0.get(i), bulkStrings0.get(i+1));
        }
        final long memNet1 = memTime1 - memTime0;
        System.out.printf("non-intern\nstart=%d\n  end=%d\n used=%d\n\n", memTime0, memTime1, memNet1);

        final long memTime2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int i = 0; i < 1000000; i++) {
            bulkStrings1.add(("base string".concat(" suffix") + " and more").intern());
        }
        final long memTime3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int i = 0; i < bulkStrings1.size() - 1; i++) {
            assertEquals(bulkStrings1.get(i), bulkStrings1.get(i+1));
            assertSame(bulkStrings1.get(i), bulkStrings1.get(i+1));
        }
        final long memNet3 = memTime3 - memTime2;
        System.out.printf("with interning\nstart=%d\n  end=%d\n used=%d\n\n", memTime2, memTime3, memNet3);

        final long memTime4 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        bulkStrings2.add(("base string".concat(" suffix") + " and more").intern());
        for (int i = 0; i < 999999; i++) {
            bulkStrings2.add(("base string".concat(" suffix") + " and more"));
        }
        final long memTime5 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        assertEquals(bulkStrings1.get(0), bulkStrings2.get(0));
        assertSame(bulkStrings1.get(0), bulkStrings2.get(0));
        assertEquals(bulkStrings1.get(1), bulkStrings2.get(1));
        assertNotSame(bulkStrings1.get(1), bulkStrings2.get(1));
        for (int i = 0; i < bulkStrings2.size() - 1; i++) {
            assertEquals(bulkStrings2.get(i), bulkStrings2.get(i+1));
            assertNotSame(bulkStrings2.get(i), bulkStrings2.get(i+1));
        }
        final long memNet5 = memTime5 - memTime4;
        System.out.printf("with interning\nstart=%d\n  end=%d\n used=%d\n\n", memTime4, memTime5, memNet5);

        System.out.printf("intern1 vs non-intern savings = %11s\n", numberFormat.format(memNet1 - memNet3));
        System.out.printf("intern2 vs intern1    savings = %11s\n", numberFormat.format(memNet1 - memNet5));
        System.out.printf("intern2 vs non-intern savings = %11s\n", numberFormat.format(memNet3 - memNet5));
    }
}
