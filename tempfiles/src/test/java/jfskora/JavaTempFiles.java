package jfskora;

import com.sun.deploy.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class JavaTempFiles {

    private final static String PATH1 = "/tmp/file1.tmp";

    private final static Long SLEEP_MILLIS_1 = 10000L;
    private final static Long MILLIS_PER_DOT = 1000L;

    private Long pid;

    @Before
    public void Before() {
        pid = getPID();
        System.out.println("pid = " + pid);
    }

    @Test
    public void FileOpenWriteDelete() throws IOException, InterruptedException {
        File testfile = new File(PATH1);
        FileOutputStream out1 = new FileOutputStream(testfile);

        System.out.println("\nfile opened " + testfile.getAbsolutePath());
        checkLSOF(testfile);

        out1.write("testing 123".getBytes());
        System.out.println("\nfile written " + testfile.getAbsolutePath());
        checkLSOF(testfile);

        if (testfile.delete()) {
            System.out.println("\nfile deleted" + testfile.getAbsolutePath());
        } else {
            System.out.println("\ndelete failed" + testfile.getAbsolutePath());
        }
        checkLSOF(testfile);

        out1.close();
        System.out.println("\nfile closed" + testfile.getAbsolutePath());
        checkLSOF(testfile);
    }

    private void checkLSOF(File testfile) throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"bash", "-c", "lsof -p" + pid.toString() + " 2>/dev/null | awk 'NR < 2 || /" + testfile.getAbsolutePath().replace("/", "\\/") + "/'"};
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

    private static long getPID() {
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
