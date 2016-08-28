import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestBadIpAddress {

    public static void main(String[] args) throws UnknownHostException {
        final InetAddress address = InetAddress.getByName("300.300.300.300");
        System.out.println("Error: InetAddress.getByName(\"300.300.300.300\") returned " + address
                + " instead of throwing UnknownHostException");
    }

}
