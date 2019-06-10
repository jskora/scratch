package jfskora.subprocess;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TrackSubprocess {


    public static void main(String[] args) {
        final String logFile = "/tmp/tracksubprocess.log";
        Runnable runnable = () -> {
            String[] command = ("/bin/ls -lR / >" + logFile).split("\\ ");
            try {
                new ProcessBuilder()
                        .command(command)
                        .start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        followExternalLogfile(runnable, logFile);
    }

    private static void followExternalLogfile(final Runnable runnable, final String logFilename) {
        Thread child = new Thread(runnable);
        child.start();

        System.out.println("    -----------------------------------------------------------------");

        File logFile = new File(logFilename);
        while (!logFile.exists()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(logFilename), StandardCharsets.UTF_8)) {
            int lineCount = 0;
            char[] buf = new char[10240];
            int used = 0;
            while (child.isAlive()) {
                try {
                    child.join(1000);
                    while (reader.ready()) {
                        used += reader.read(buf, used, buf.length - used);
                        int eolPos = ArrayUtils.indexOf(buf, '\n');
                        eolPos = (eolPos > used) ? -1 : eolPos;
                        while (eolPos != -1 && eolPos < used) {
                            System.out.println(String.format("    %5d %s", ++lineCount,
                                    new String(Arrays.copyOfRange(buf, 0, eolPos))));
                            System.arraycopy(buf, eolPos + 1, buf, 0, used - (eolPos + 1));
                            used -= (eolPos + 1);
                            eolPos = ArrayUtils.indexOf(buf, '\n');
                            eolPos = (eolPos > used) ? -1 : eolPos;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("    -----------------------------------------------------------------");
    }
}
