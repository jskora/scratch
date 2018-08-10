package scratch.lazy;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.function.Supplier;

@Slf4j
public class TestLazyEvaluation {

    static boolean compute(String str) {
        log.info("  executing");
        return str.contains("a");
    }

    static String eagerMatch(boolean a, boolean b) {
        return a && b ? "match" : "incompatible";
    }

    static String lazyMatch(Supplier<Boolean> a, Supplier<Boolean> b) {
        return a.get() && b.get() ? "match" : "incompatible";
    }

    @Test
    public void testEagerEvaluation() {
        log.info("eager evaluation");
        log.info("  * {} *", eagerMatch(compute("bb"), compute("aa")));
    }

    @Test
    public void testLazyEvaluation() {
        log.info("lazy evaluation");
        log.info("  * {} *", lazyMatch(() -> compute("bb"), () -> compute("aa")));
    }


}
