package jfskora;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathTests {

    DecimalFormat df;

    @Before
    public void before() {
        df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
    }

    @After
    public void after() {
        // nothing
    }

    @Test
    public void testModuloInt() {
        final int size = 16;

        System.out.println("\ntestModuloInt size=" + size);

        long start = System.nanoTime();
        for(int i = 0; i < Integer.MAX_VALUE; i++) {
            getNextIndexInt(size, i);
        }
        long duration1 = System.nanoTime() - start;
        System.out.println("Time taken by Modulo (%) operator --> " + duration1 + "ns.");

        start = System.nanoTime();
        final int shiftFactor = size - 1;
        for(int i = 0; i < Integer.MAX_VALUE; i++) {
            getNextIndexBitwiseInt(shiftFactor, i);
        }
        long duration2 = System.nanoTime() - start;
        System.out.println("Time taken by bitwise AND --> " + duration2 + "ns.");
        long diffTime = duration2 - duration1;
        double diffPct = 100L * new Long(diffTime).doubleValue() / new Long(duration1).doubleValue();
        System.out.println("Improvement --> " + diffTime + "ns " + df.format(diffPct) + "%.");
    }

    @Test
    public void testModuloLong() {
        final long size = 16;

        System.out.println("\ntestModuloLong size=" + size);

        long start = System.nanoTime();
        for(int i = 0; i < Integer.MAX_VALUE; i++) {
            getNextIndexLong(size, i);
        }
        long duration1 = System.nanoTime() - start;
        System.out.println("Time taken by Modulo (%) operator --> " + duration1 + "ns.");

        start = System.nanoTime();
        final long shiftFactor = size - 1;
        for(int i = 0; i < Integer.MAX_VALUE; i++) {
            getNextIndexBitwiseLong(shiftFactor, i);
        }
        long duration2 = System.nanoTime() - start;
        System.out.println("Time taken by bitwise AND --> " + duration2 + "ns.");
        long diffTime = duration2 - duration1;
        double diffPct = 100L * new Long(diffTime).doubleValue() / new Long(duration1).doubleValue();
        System.out.println("Improvement --> " + diffTime + "ns " + df.format(diffPct) + "%.");
    }

    private static int getNextIndexInt(int size, int nextInt) {
        return nextInt % size;
    }

    private static int getNextIndexBitwiseInt(int size, int nextInt) {
        return nextInt & size;
    }

    private static long getNextIndexLong(long size, long nextLong) {
        return nextLong % size;
    }

    private static long getNextIndexBitwiseLong(long size, long nextLong) {
        return nextLong & size;
    }
}
