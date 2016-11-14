package jfskora;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public class FileSizeBench2 {

    public static void main(String[] args) throws Exception {
        int runs = 1000;
        int iterations = 1;

        final String FILENAME = "file-listFiles/target/classes/data100mb.bin";
        final File file = new File(FILENAME);
        final FileChannel channel = new FileInputStream(FILENAME).getChannel();
        final RandomAccessFile raf = new RandomAccessFile(FILENAME, "r");

        HashMap<String, Double> times = new HashMap<>();
        times.put("file", 0.0);
        times.put("channel", 0.0);
        times.put("raf", 0.0);

        long start;
        for (int i = 0; i < runs; ++i) {
            long l = file.length();

            start = System.nanoTime();
            for (int j = 0; j < iterations; ++j)
                if (l != file.length()) throw new Exception();
            times.put("file", times.get("file") + System.nanoTime() - start);

            start = System.nanoTime();
            for (int j = 0; j < iterations; ++j)
                if (l != channel.size()) throw new Exception();
            times.put("channel", times.get("channel") + System.nanoTime() - start);

            start = System.nanoTime();
            for (int j = 0; j < iterations; ++j)
                if (l != raf.length()) throw new Exception();
            times.put("raf", times.get("raf") + System.nanoTime() - start);
        }
        for (Map.Entry<String, Double> entry : times.entrySet()) {
            System.out.println(
                    entry.getKey() + " sum: " + 1e-3 * entry.getValue() +
                            ", per Iteration: " + (1e-3 * entry.getValue() / runs / iterations));
        }
    }
}
