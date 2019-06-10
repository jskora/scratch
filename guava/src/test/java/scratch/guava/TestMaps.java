package scratch.guava;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestMaps {

    static List<String> uniqLenStrings = Arrays.asList("aardvark", "dog", "fish", "gopher", "hamster", "chimp");

    static List<String> nonUniqLengthStrings = Arrays.asList("aardvark", "dog", "fish", "gopher", "hamster", "chimp", "squirrel");

    @Test
    public void testUniqueIndex() {
        ImmutableMap<Integer, String> stringsByLength = Maps.uniqueIndex(uniqLenStrings, new Function<String, Integer>() {
            @Override
            public Integer apply(String string) {
                return string.length();
            }
        });
        assertThat(stringsByLength.entrySet(), hasSize(uniqLenStrings.size()));
        assertThat(stringsByLength.keySet(), hasSize(uniqLenStrings.size()));

        assertThat(stringsByLength.values(), everyItem(isIn(uniqLenStrings)));
        assertThat(stringsByLength.keySet(), contains(uniqLenStrings.stream().map(String::length).toArray()));
        assertThat(stringsByLength.values(), containsInAnyOrder(uniqLenStrings.toArray()));
    }

    @Test
    public void testNonUniqueIndex() {
        ImmutableListMultimap<Integer, String> stringsByLength = Multimaps.index(nonUniqLengthStrings, new Function<String, Integer>() {
            @Override
            public Integer apply(String string) {
                return string.length();
            }
        });
        assertThat(stringsByLength.entries(), hasSize(nonUniqLengthStrings.size()));
        assertThat(stringsByLength.keySet(), not(hasSize(nonUniqLengthStrings.size())));


    }

    @Test
    public void testMultimap() {
        Multimap llmmap = LinkedListMultimap.create();
        llmmap.put("c", "coal");
        llmmap.put("a", "apple");
        llmmap.put("a", "abalama");
        llmmap.put("a", "alaska");
        llmmap.put("b", "boat");
        llmmap.put("b", "banana");
        System.out.println(llmmap);

        Multimap almmap = ArrayListMultimap.create();
        almmap.put("c", "coal");
        almmap.put("a", "apple");
        almmap.put("a", "abalama");
        almmap.put("a", "alaska");
        almmap.put("b", "boat");
        almmap.put("b", "banana");
        System.out.println(almmap);
    }

    @Test
    public void testListMultimap() {
        ListMultimap test = ArrayListMultimap.create();
        test.put("a", "apple");
        test.put("a", "abalama");
        test.put("a", "alaska");
        test.put("b", "banana");
        test.put("b", "boat");
        test.put("c", "coal");
        System.out.println(test);
        System.out.println(test.size());
        System.out.println(test.keySet().size());
        System.out.println(test.asMap().size());
        System.out.println(test.asMap());
        System.out.println(test.getClass().getCanonicalName());
        System.out.println(test.asMap().getClass().getCanonicalName());
        List testa = test.get("a");
        System.out.println(testa);
        test.put("a", "atom");
        System.out.println(testa);
    }
}
