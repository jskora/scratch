package scratch.lazy;

import com.google.common.collect.HashMultimap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class TestMultimap {

    @Test
    public void testMultimap1() {
        HashMultimap map = HashMultimap.create();
        map.put("field1", "value1a");
        map.putAll("field2", Arrays.asList("value1b", "value2b", "value3b"));

        HashMultimap submap = HashMultimap.create();
        submap.putAll("test", Arrays.asList("value1c", "value2c", "value3c"));
        submap.putAll("field3", submap.entries());
        map.putAll(submap);

        log.info("map.forEach");
        map.forEach((k, v) ->
                log.info("   {} = {}", k, v));

        log.info("map.asMap.forEach");
        map.asMap().forEach((k, v) ->
                log.info("   {} = {}", k, v));
    }
}
