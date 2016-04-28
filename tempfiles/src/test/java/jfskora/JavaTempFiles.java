package jfskora;

import com.sun.deploy.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class JavaTempFiles {

    final static String PATH1 = "/tmp/file1.tmp";

    final static Long SLEEP_MILLIS_1 = 10000L;
    final static Long MILLIS_PER_DOT = 1000L;

    final static String AWK_BINARY = "/usr/bin/awk";
    final static String LSOF_BINARY = "/usr/sbin/lsof";

    Long pid;

    @Before
    public void Before() {
        pid = getPID();
        System.out.println("pid = " + pid);
    }

    @Test
    public void FileOpenWriteDelete() throws IOException, InterruptedException {
        File testfile = new File(PATH1);
        FileOutputStream out1 = new FileOutputStream(testfile);
        out1.write("testing 123".getBytes());
        System.out.println("file written " + testfile.getAbsolutePath());
        System.out.println("sleeping");
        checkLSOF(testfile);
        sleepTimer(SLEEP_MILLIS_1);
        if (!testfile.delete()) {
            System.out.println("file deleted");
        } else {
            System.out.println("delete failed");
        }
        System.out.println("sleeping");
        sleepTimer(SLEEP_MILLIS_1);
        out1.close();
        System.out.println("file closed");
        System.out.println("sleeping");
        sleepTimer(SLEEP_MILLIS_1);
    }

    private void checkLSOF(File testfile) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {LSOF_BINARY, "-p" + pid.toString(), "|", AWK_BINARY, "'NR < 2 || /" + testfile.getAbsolutePath().replace("/", "\\/") + "/'"};
        System.out.println(StringUtils.join(Arrays.asList(commands), " "));
        Process proc = rt.exec(commands);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String line = stdInput.readLine();
        while (line != null) {
            System.out.println(line);
            line = stdInput.readLine();
        }
        line = stdError.readLine();
        while (line != null) {
            System.out.println(line);
            line = stdError.readLine();
        }
        proc.destroy();
    }

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    private void sleepTimer(long sleep_millis) throws InterruptedException {
        for (long slept = 0; slept < sleep_millis; slept += MILLIS_PER_DOT) {
            Thread.sleep(MILLIS_PER_DOT);
            System.out.print(".");
        }
        System.out.println("");
    }
}
