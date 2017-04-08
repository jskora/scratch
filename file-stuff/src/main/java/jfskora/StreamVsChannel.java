package jfskora;

import java.io.*;
import java.nio.channels.Channel;
import java.nio.file.Files;
import java.nio.file.Path;

public class StreamVsChannel {

    private static String tempFilename = "/tmp/svc.dat";
    private static int blockSize = 1024;
    private static int blockCount = 1024;
    private static int totalBytes = blockSize * blockCount;

    public static void main(String[] args) {
        final StreamVsChannel svc = new StreamVsChannel();
        try {
            svc.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        System.out.println("writing temp file start");
        makeTempFile(tempFilename);
        System.out.println("writing temp file end");
        File tempFile = new File(tempFilename);

        long t0 = System.nanoTime();
        System.out.println("readBufferedInputStream start");
        readBufferedInputStream(tempFile);
        long t1 = System.nanoTime();
        System.out.println("readBufferedInputStream end = " + (t1 - t0) + " nanos");
    }

    private void readBufferedInputStream(File srcFile) throws IOException {
        final BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(srcFile), 10240);
        byte[] buffer = new byte[totalBytes];
        long bytes = 0;
        while (true) {
            int bytesRead = bufIn.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            bytes += bytesRead;
        }
        System.out.println("readBufferedInputStream " + srcFile.getName() + " read " + bytes + " bytes");
    }

    private void readChannel(File srcFile) throws IOException {
        final Channel channelIn = new F
        byte[] buffer = new byte[totalBytes];
        long bytes = 0;
        while (true) {
            int bytesRead = bufIn.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            bytes += bytesRead;
        }
        System.out.println("readBufferedInputStream " + srcFile.getName() + " read " + bytes + " bytes");
    }

    private void makeTempFile(String filename) {
        if (!Files.exists(new File(filename).toPath())) {
            Process p;
            try {
                p = Runtime.getRuntime().exec("dd if=/dev/urandom of=" + filename + " bs=" + blockSize + " count=" + blockCount);
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
