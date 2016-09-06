package jfskora;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryListingBench
{
    private static final File TEMP_DIR = new File("/local/jfskora/bench");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final Integer[] FILE_COUNTS = new Integer[]{100, 1_000, 10_000, 100_000, 200_000, 500_000, 750_000, 1_000_000};
    private static final Runtime runtime = Runtime.getRuntime();

    public static void main(String[] args) {
        System.out.println("Free Memory:" + runtime.freeMemory());
        System.out.println("Total Memory:" + runtime.totalMemory());
        System.out.println("Max Memory:" + runtime.maxMemory());
        System.out.println("");

        for (Integer count : FILE_COUNTS) {
            iterate(count);
        }
    }

    private static void iterate(Integer count) {

        /*
        Create folder with FILE_COUNT files in it.
         */
        System.out.println("calling runtime.gc() and creating temp files, count=" + count);
        runtime.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runtime.gc();
        try {
            Files.createDirectory(TEMP_DIR.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Integer n = count; n < count * 2; n++) {
            File tmp = new File(TEMP_DIR + FILE_SEPARATOR + "temp-" + n.toString() + ".tmp");
            try {
                FileOutputStream fos = new FileOutputStream(tmp);
                fos.write(0x13);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int countListFiles = 0;
        long startListFiles = System.nanoTime();
        long beforeListFile = runtime.totalMemory() - runtime.freeMemory();
        //noinspection ConstantConditions
        for (File f : TEMP_DIR.listFiles()) {
            if (f == null) {
                System.out.println("file is null");
            } else if (f.length() != 1) {
                System.out.println("file " + f.getName() + " size=" + f.length() + " not 1");
            }
            countListFiles += 1;
        }
        long stopListFiles = System.nanoTime();
        long afterListFile = runtime.totalMemory() - runtime.freeMemory();

        int countDirectoryStream = 0;
        long startDirectoryStream = System.nanoTime();
        long beforeDirectoryStream = runtime.totalMemory() - runtime.freeMemory();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(TEMP_DIR.toPath())) {
            for (Path p : directoryStream) {
                File pfile = p.toFile();
                if (pfile.length() != 1) {
                    System.out.println("file " + pfile.getName() + " size=" + pfile.length() + " not 1");
                }
                countDirectoryStream += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long stopDirectoryStream = System.nanoTime();
        long afterDirectoryStream = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("      listFiles count=" + countListFiles
                + " duration=" + (stopListFiles - startListFiles)
                + " memory=" + beforeListFile + "-" + afterListFile
                + " (" + (afterListFile - beforeListFile) + ")");
        System.out.println("directoryStream count=" + countDirectoryStream
                + " duration=" + (stopDirectoryStream - startDirectoryStream)
                + " memory=" + beforeDirectoryStream + "-" + afterDirectoryStream
                + " (" + (afterDirectoryStream - beforeDirectoryStream) + ")");

        /*
        Cleanup temp files
         */
        System.out.println("cleaning up temp files\n");
        for (Integer n = count; n < count * 2; n++) {
            File tmp = new File(TEMP_DIR + FILE_SEPARATOR + "temp-" + n.toString() + ".tmp");
            try {
                Files.delete(tmp.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.delete(TEMP_DIR.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
