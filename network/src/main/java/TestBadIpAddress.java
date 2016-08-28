import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class TestBadIpAddress {

    public static void main(String[] args) throws UnknownHostException, NoSuchFieldException, IllegalAccessException {
        for (String property : Arrays.asList("java.version", "java.vendor", "os.name", "os.arch")) {
            System.out.println(property + " = " + System.getProperty(property));
        }
        for (String fieldName : Arrays.asList("IPv4", "IPv6", "preferIPv6Address", "impl")) {
            final Field field = InetAddress.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            System.out.println("InetAddress." + field.getName() + " = " + field.get(null));
        }
        System.out.println("----------------------------------------");
        System.out.println("running InetAddress.getByName(\"300.300.300.300\")");
        final InetAddress address = InetAddress.getByName("300.300.300.300");
        System.out.println("Error: InetAddress.getByName(\"300.300.300.300\") returned " + address
                + " instead of throwing UnknownHostException");
    }

}
