package jfskora;

import com.sun.management.UnixOperatingSystemMXBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class JvmStatus {

    private static final int NUM_FILES = 100;

    public static void main( String[] args ) {
        File[] files = new File[NUM_FILES];
        FileOutputStream[] fout = new FileOutputStream[NUM_FILES];

        System.out.println("Number of open fd - start      : " + getOpenFileCount());
        for (int i = 0; i < NUM_FILES; i++) {
            files[i] = new File("/tmp/fout" + i);
        }
        System.out.println("Number of open fd - before open: " + getOpenFileCount());
        try {
            for (int x = 0; x < NUM_FILES; x++) {
                fout[x] = new FileOutputStream(files[x]);
            }
            System.out.println("Number of open fd - after open : " + getOpenFileCount());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            for (int x = 0; x < NUM_FILES; x++) {
                fout[x].close();
            }
            System.out.println("Number of open fd - after close: " + getOpenFileCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number of open fd - done       : " + getOpenFileCount());
    }

    private static long getOpenFileCount() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof UnixOperatingSystemMXBean) {
            return ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
        } else {
            return 0;
        }
    }
}
