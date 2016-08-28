package jfskora;


import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FileTimestamps
{

    private static File tempFile;

    private static final String OS_NAME = System.getProperty("os.name");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z", Locale.US);

    @Rule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    @SuppressWarnings("unused")
    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        // create
        tempFile = tempFolder.newFile("test.txt");
//        FileOutputStream out = new FileOutputStream(tempFile);
//        out.write(new byte[]{0});
//        out.close();

        if (OS_NAME.contains("Linux")) {
            osCommand("touch " + tempFile.getPath());
            System.out.println(linuxStat(tempFile));
        }

        // update
        Thread.sleep(2000L);
//        out = new FileOutputStream(tempFile, true);
//        out.write(new byte[]{1});
//        out.close();

        if (OS_NAME.contains("Linux")) {
            osCommand("echo \"test\" >> " + tempFile.getPath());
            System.out.println(linuxStat(tempFile));
        }

        // access
        Thread.sleep(2000L);
//        FileInputStream in = new FileInputStream(tempFile);
        //noinspection ResultOfMethodCallIgnored
//        in.read();
//        in.close();

        if (OS_NAME.contains("Linux")) {
            osCommand("cat " + tempFile.getPath() + " > /dev/null");
            System.out.println(linuxStat(tempFile));
        }
    }

    private static String linuxStat(File statFile) throws IOException, InterruptedException {
        return osCommand("stat " + statFile.getPath());
    }

    private static String osCommand(String command) throws IOException, InterruptedException {
        final StringBuilder output = new StringBuilder();
        String line;
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        System.out.println("\n" + command);
        while ((line = reader.readLine())!= null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    @Test
    public void testBasicAttributes() throws IOException {
        BasicFileAttributeView view1b = Files.getFileAttributeView(tempFile.toPath(), BasicFileAttributeView.class);
        BasicFileAttributes basicFileAttributes = view1b.readAttributes();
        System.out.println("\nBasicFileAttributes for " + tempFile.getName());
        System.out.println("creation= " + formatter.format(basicFileAttributes.creationTime().toMillis()));
        System.out.println("access  = " + formatter.format(basicFileAttributes.lastAccessTime().toMillis()));
        System.out.println("modified= " + formatter.format(basicFileAttributes.lastModifiedTime().toMillis()));
    }

    @Test
    public void testPosixAttributes() throws IOException {
        PosixFileAttributeView view1b = Files.getFileAttributeView(tempFile.toPath(), PosixFileAttributeView.class);
        PosixFileAttributes posixFileAttributes = view1b.readAttributes();
        System.out.println("\nPosixFileAttributes for " + tempFile.getName());
        System.out.println("creation= " + formatter.format(posixFileAttributes.creationTime().toMillis()));
        System.out.println("access  = " + formatter.format(posixFileAttributes.lastAccessTime().toMillis()));
        System.out.println("modified= " + formatter.format(posixFileAttributes.lastModifiedTime().toMillis()));
    }
}
