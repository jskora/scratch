package jfskora;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStreamsEOF {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Before
    public void before() {
        // nothing to see here
    }

    @After
    public void after() {
        // nothing to see here
    }

    @Test
    public void testReadAfterEOFByteBuffer() throws IOException {
        final byte[] inputBytes = "this is the test input data".getBytes();
        byte[] buffer = new byte[2];
        int bytesRead;

        InputStream input = new ByteArrayInputStream(inputBytes);
        while (true) {
            bytesRead = input.read(buffer, 0, buffer.length);
            assertTrue(bytesRead >= -1 && bytesRead <= buffer.length);
            System.out.println("bytesRead=" + bytesRead + " buffer='" +
                    ((bytesRead == -1) ? "" : new String(Arrays.copyOfRange(buffer, 0, bytesRead))) + "'");
            if (bytesRead == -1) break;
        }
        assertEquals(-1, bytesRead);
        bytesRead = input.read(buffer, 0, 1);
        assertEquals(-1, bytesRead);
        System.out.println("bytesRead=" + bytesRead + " buffer='" +
                ((bytesRead == -1) ? "" : new String(Arrays.copyOfRange(buffer, 0, bytesRead))) + "'");
    }

    @Test
    public void testReadAfterEOFFile() throws IOException {
        final byte[] inputBytes = "this is the test input data to be segmented into 2 separate writes".getBytes();
        final int inputHalfLength = inputBytes.length / 2;
        final byte[] inputFirstHalf = Arrays.copyOfRange(inputBytes, 0, inputHalfLength);
        final byte[] inputSecondHalf = Arrays.copyOfRange(inputBytes, inputHalfLength, inputBytes.length - inputHalfLength);

        byte[] buffer = new byte[2];
        int bytesRead;
        File tempFile = folder.newFile();
        FileOutputStream output = new FileOutputStream(tempFile);
        FileInputStream input = new FileInputStream(tempFile);
        output.write(inputFirstHalf);
        output.write(inputSecondHalf);
        output.close();

        InputStream bufferedInput = new BufferedInputStream(input);
        int n = 0;
        while (true) {
            n += 1;
//            if (n == 10) {
//                output.write(inputSecondHalf);
//                output.close();
//            }
            bytesRead = bufferedInput.read(buffer, 0, buffer.length);
            assertTrue(bytesRead >= -1 && bytesRead <= buffer.length);
            System.out.println("bytesRead=" + bytesRead + " buffer='" +
                    ((bytesRead == -1) ? "" : new String(Arrays.copyOfRange(buffer, 0, bytesRead))) + "'");
            if (bytesRead == -1) break;
        }
        assertEquals(-1, bytesRead);
        bytesRead = input.read(buffer, 0, 1);
        assertEquals(-1, bytesRead);
        System.out.println("bytesRead=" + bytesRead + " buffer='" +
                ((bytesRead == -1) ? "" : new String(Arrays.copyOfRange(buffer, 0, bytesRead))) + "'");
    }
}
