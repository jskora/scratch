import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TestBadIpAddress {

    public static void main(String[] args) {
        final String BAD_IP = "300.300.300.300";
        printConfiguration();
        try {
            System.out.println("running InetAddress.getByName(" + BAD_IP + ")");
            System.out.println("result = " + InetAddress.getByName(BAD_IP));
            System.out.println("\nFAIL: InetAddress.getByName(" + BAD_IP + ") did not throw UnknownHostException");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("SUCCESS: InetAddress.getByName(" + BAD_IP + ") threw UnknownHostException");
        }
    }

    private static void printConfiguration() {
        try {
            for (String property : Arrays.asList("java.version", "java.vendor", "os.name", "os.arch")) {
                System.out.println(property + " = " + System.getProperty(property));
            }
            for (String fieldName : Arrays.asList("IPv4", "IPv6", "preferIPv6Address", "impl")) {
                final Field field = InetAddress.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                if (fieldName.equals("impl")) {
                    System.out.println("InetAddress." + field.getName() + ".class = " + field.get(null).getClass().getCanonicalName());
                } else {
                    System.out.println("InetAddress." + field.getName() + " = " + field.get(null) + " (" + field.getType().getName() + ")");
                }
            }
            System.out.println("");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
