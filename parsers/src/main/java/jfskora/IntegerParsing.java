package jfskora;

import java.util.regex.Pattern;

public class IntegerParsing {

    private final static long ITERS = 10000;

    private static final Pattern intPattern = Pattern.compile("\\d+");

    public static void main( String[] args ) {
        String[] samples = new String[]{"123", "0123", "1230", "123 0", "123_0", "123.0", "123x", null};
        assert(intPattern.matcher("1234").matches());
        for (String sample : samples) {
            long[] times = new long[3];
            long runtime;
            for (int q = 0; q < 3; q++) {
//                System.out.printf(" sample=%6s", sample == null ? "null" : sample);
                for (int m = 0; m < 3; m++) {
                    runtime = run(sample, m);
//                    System.out.printf(" time(%1d)=%9d", m, runtime);
                    times[m] += runtime;
                }
//                System.out.println("");
            }
            System.out.printf(" sample=%6s", sample == null ? "null" : sample);
            for (int m = 0; m < 3; m++) {
                System.out.printf(" avg time(%d)=%9d", m, times[m] / 3);
            }
            System.out.println("");
            System.out.println("");
        }
    }

    private static long run(final String sample, final int method) {

        long time0 = System.nanoTime();
        Integer tmp;
        for (int i = 0; i < ITERS; i++) {
            switch(method) {
                case 0:
                    try { tmp = toInteger0(sample); } catch (Exception ignore) { }
                    break;
                case 1:
                    try { tmp = toInteger1(sample); } catch (Exception ignore) { }
                    break;
                case 2:
                    try { tmp = toInteger2(sample); } catch (Exception ignore) { }
                    break;
            }
        }
        long time1 = System.nanoTime();
        return time1 - time0;
    }

    private static Integer toInteger0(final String value) {
        if (value == null) {
            return null;
        }
        if (!intPattern.matcher(value).matches()) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            return null;
        }
    }

    private static Integer toInteger1(final String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            return null;
        }
    }

    private static Integer toInteger2(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final Exception e) {
            return null;
        }
    }

}
