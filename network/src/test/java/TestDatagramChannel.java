import org.junit.Test;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

import static org.junit.Assert.assertTrue;

public class TestDatagramChannel {

    final static int BUF_SIZE_TARGET = 3 * 1024 * 1024;

    @Test
    public void testSetOption() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        final int bufSizeOrig = channel.getOption(StandardSocketOptions.SO_RCVBUF);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, BUF_SIZE_TARGET);
        final int bufSizeCurr = channel.getOption(StandardSocketOptions.SO_RCVBUF);
        System.out.println(String.format("orig=%d  updated=%d", bufSizeOrig, bufSizeCurr));
        assertTrue(bufSizeCurr != bufSizeOrig);
        assertTrue(bufSizeCurr == BUF_SIZE_TARGET);
    }
}

