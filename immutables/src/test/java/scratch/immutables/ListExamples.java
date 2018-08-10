package scratch.immutables;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ListExamples {

    class Tuple extends AbstractMap.SimpleEntry {
        public Tuple(Object key, Object value) {
            super(key, value);
        }
    }

    @Test
    public void TestList1() {
        List<Tuple> words = new ArrayList<Tuple>();
        words.add(new Tuple("one", 1));
        words.add(new Tuple("two", 2));
        words.add(new Tuple("three", 3));
        ImmutableList<Tuple> iwords = ImmutableList.copyOf(words);

        words.add(new Tuple("four", 4));

        words.forEach((Tuple word) -> log.info("word={}", word));
        System.out.println();

        iwords.forEach((Object iword) -> log.info("iword={}", iword));
        System.out.println();


        words.get(0).setValue(5);

        words.forEach((Tuple word) -> log.info("word={}", word));
        System.out.println();

        iwords.forEach((Object iword) -> {
            log.info("iword={}", iword);
        });
        System.out.println();
    }
}
