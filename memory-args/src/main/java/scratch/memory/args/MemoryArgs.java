package scratch.memory.args;

import java.text.DecimalFormat;
import java.time.Instant;

/**
 * A simple utility to dump the JVM runtime memory status at startup.
 */
public class MemoryArgs {

    private final static Runtime runtime = Runtime.getRuntime();

    /**
     * A simple utility to dump the JVM runtime memory status at startup.
     */
    public static void main(String[] args) {
        // 128 TB = 140,737,488,355,328 bytes --> 19 columns
        DecimalFormat decimal = new DecimalFormat("#,###");
        System.out.println("Startup memory @ " + Instant.now().toString());
        System.out.println(String.format("Total: %19s", decimal.format(runtime.totalMemory())));
        System.out.println(String.format("Free:  %19s", decimal.format(runtime.freeMemory())));
        System.out.println();
    }
}
