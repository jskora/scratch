package jfskora;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class DirectorySizeBench {

    private static final File TARGET_DIR = new File("D:/tools/IntelliJ IDEA 2016.2.5/bin");
    private static final int ITERATIONS = 10;

//    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
//    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
//    private static final Runtime runtime = Runtime.getRuntime();

    private static long getSize(final File directory) {
        if (!directory.exists()) {
            return 0L;
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Must specify a directory but specified " + directory);
        }

        // List all files in the Index Directory.
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }

        long sum = 0L;
        for (final File file : files) {
            sum += file.length();
        }

        return sum;
    }

    private static long getSize2(final File directory) {
        if (!directory.exists()) {
            return 0L;
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Must specify a directory but specified " + directory);
        }

        // List all files in the Index Directory.
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }

        long sum = 0L;
        for (int i = 0; i < files.length; i++) {
            sum += files[i].length();
        }

        return sum;
    }

    private static long sizeOfDirectory(final File directory) {
        return 0; //return FileUtils.sizeOfDirectory(directory);
    }

    public static void main(String[] args) {
        long t0 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vGetSize = getSize(TARGET_DIR);
            if (i==0) {
                System.out.println("        getSize size=" + vGetSize);
            }
        }
        long t1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vSizeOfDirectory = 0; //sizeOfDirectory(TARGET_DIR);
            if (i==0) {
                System.out.println("sizeOfDirectory size=" + vSizeOfDirectory);
            }
        }
        long t2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vGetSize2 = getSize2(TARGET_DIR);
            if (i==0) {
                System.out.println("       getSize2 size=" + vGetSize2);
            }
        }
        long t3 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vGetSize = getSize(TARGET_DIR);
            if (i==0) {
                System.out.println("        getSize size=" + vGetSize);
            }
        }
        long t4 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vSizeOfDirectory = 0; //sizeOfDirectory(TARGET_DIR);
            if (i==0) {
                System.out.println("sizeOfDirectory size=" + vSizeOfDirectory);
            }
        }
        long t5 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            final long vGetSize2 = getSize2(TARGET_DIR);
            if (i==0) {
                System.out.println("       getSize2 size=" + vGetSize2);
            }
        }
        long t6 = System.nanoTime();

        System.out.println("        getSize nanos = " + (t1 - t0));
        System.out.println("sizeOfDirectory nanos = " + (t2 - t1));
        System.out.println("       getSize2 nanos = " + (t3 - t2));
        System.out.println("        getSize nanos = " + (t4 - t3));
        System.out.println("sizeOfDirectory nanos = " + (t5 - t4));
        System.out.println("       getSize2 nanos = " + (t6 - t5));
    }
}
