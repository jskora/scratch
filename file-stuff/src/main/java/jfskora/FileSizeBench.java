package jfskora;

import java.io.*;
import java.net.*;
import java.util.*;

public enum FileSizeBench {

    LENGTH {
        @Override
        public long getResult() throws Exception {
            File me = new File(FileSizeBench.class.getResource(
                    "FileSizeBench.class").getFile());
            return me.length();
        }
    },
    CHANNEL {
        @Override
        public long getResult() throws Exception {
            FileInputStream fis = null;
            try {
                File me = new File(FileSizeBench.class.getResource(
                        "FileSizeBench.class").getFile());
                fis = new FileInputStream(me);
                return fis.getChannel().size();
            } finally {
                fis.close();
            }
        }
    },
    URL {
        @Override
        public long getResult() throws Exception {
            InputStream stream = null;
            try {
                URL url = FileSizeBench.class
                        .getResource("FileSizeBench.class");
                stream = url.openStream();
                return stream.available();
            } finally {
                stream.close();
            }
        }
    };

    public abstract long getResult() throws Exception;

    public static void main(String[] args) throws Exception {
        int runs = 5;
        int iterations = 50;

        EnumMap<FileSizeBench, Long> durations = new EnumMap<FileSizeBench, Long>(FileSizeBench.class);

        for (int i = 0; i < runs; i++) {
            for (FileSizeBench test : values()) {
                if (!durations.containsKey(test)) {
                    durations.put(test, 0l);
                }
                long duration = testNow(test, iterations);
                durations.put(test, durations.get(test) + duration);
                // System.out.println(test + " took: " + duration + ", per iteration: " + ((double)duration / (double)iterations));
            }
        }

        for (Map.Entry<FileSizeBench, Long> entry : durations.entrySet()) {
            System.out.println();
            System.out.println(entry.getKey() + " sum: " + entry.getValue() + ", per Iteration: " + ((double)entry.getValue() / (double)(runs * iterations)));
        }

    }

    private static long testNow(FileSizeBench test, int iterations)
            throws Exception {
        long result = -1;
        long before = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (result == -1) {
                result = test.getResult();
                //System.out.println(result);
            } else if ((result = test.getResult()) != result) {
                throw new Exception("variance detected!");
            }
        }
        return (System.nanoTime() - before) / 1000;
    }

}