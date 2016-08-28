
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.fail;

public class TestInetAddress {

    @Test(expected = UnknownHostException.class)
    public void testBadIPAddress() throws UnknownHostException {
        final InetAddress address = InetAddress.getByName("300.300.300.300");
        fail("Error, InetAddress(\"300.300.300.300\") returned " + address + " instead of throwing UnknownHostException");
    }
}
