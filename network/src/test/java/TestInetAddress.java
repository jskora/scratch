
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.fail;

public class TestInetAddress {

    @Test(expected = UnknownHostException.class)
    public void testBadIPAddress() throws UnknownHostException {
        final String BAD_IP = "300.300.300.300";
        final InetAddress address = InetAddress.getByName(BAD_IP);
        System.out.println("Address = " + address);
        fail("Error, should throw UnknownHostException before reaching here");
    }
}
