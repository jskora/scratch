package com.lurz;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestTreeMap {

    @Before
    public void before() {

    }

    @Test
    public void testSortedOutput() {


        final TreeMap<String, String> treeMap = new TreeMap<>();
//        final String ENTRY_FORMAT = "p.k=%s p.v=%s  c.k=%s c.v=%s  diff=%d";

        treeMap.put("java.arg.test",  "last");
        treeMap.put("java.arg.3",     "3");
        treeMap.put("java.arg.1",     "1");
        treeMap.put("java.arg.8",     "8");
        treeMap.put("java.arg.inmid", "mid");
        treeMap.put("java.arg.5",     "5");
        treeMap.put("java.arg.9",     "9");
        treeMap.put("java.arg.first", "first");

        Iterator<String> treeIter = treeMap.keySet().iterator();
        assertTrue(treeIter.hasNext());

        String priorKey = treeIter.next();
//        System.out.println(String.format(ENTRY_FORMAT, priorKey, treeMap.get(priorKey), "", "", 0));
        System.out.println(String.format("%s=%s", priorKey, treeMap.get(priorKey)));

        while (treeIter.hasNext()) {
            final String currKey = treeIter.next();
//            System.out.println(String.format(ENTRY_FORMAT, priorKey, treeMap.get(priorKey), currKey, treeMap.get(currKey), currKey.compareTo(priorKey)));
            System.out.println(String.format("%s=%s", currKey, treeMap.get(currKey)));
            assertTrue(currKey.compareTo(priorKey) >= 1);
            priorKey = currKey;
        }

    }

    @Test
    public void testCeilingEntry() {
        final TreeMap<Double, String> map = new TreeMap<>();
        map.put(1.0, "one");
        map.put(4.0, "four");
        map.put(2.0, "two");
        map.put(3.0, "three");
        map.put(2.5, "two point five");
        assertNotNull(map.ceilingEntry(1.5));
        assertNull(map.ceilingEntry(6.0));
    }

    @Test
    public void testCeilingEntryWeight() {
        final TreeMap<Double, String> map = new TreeMap<>();
        double total = 0;
        total += 1.0; map.put(total, "one");
        total += 4.0; map.put(total, "four");
        total += 2.0; map.put(total, "two");
        total += 3.0; map.put(total, "three");
        total += 2.5; map.put(total, "two point five");
        assertNotNull(map.ceilingEntry(1.0));
        assertNull(map.ceilingEntry(6.0));
    }



    @Test
    public void testCeilingEntryWeight2() {
        final TreeMap<Double, String> map = new TreeMap<>();
        double total = 0;
        total += 1.0; map.put(total, "one");
        total += 2.0; map.put(total, "two");
        total += 3.0; map.put(total, "three");
        total += 99.0; map.put(total, "ninety-nine");
        System.out.println(map.keySet());
        Map.Entry value = map.ceilingEntry(total);
        System.out.println(value);
        assertNotNull(value);
        value = map.ceilingEntry(total);
        System.out.println(value);
        assertNotNull(value);
        Random random = new Random();
        Map<String, Integer> hits = new HashMap<>();
        for (String k : map.values()) {
            hits.put(k, 0);
        }
        for (int n = 0; n < 10000; n++) {
            Map.Entry<Double, String> temp = map.ceilingEntry(random.nextDouble() * total);
            hits.put(temp.getValue(), hits.get(temp.getValue()) + 1);
        }
        for (Map.Entry<String, Integer> hit : hits.entrySet()) {
            System.out.println(String.format("%s = %d %f", hit.getKey(), hit.getValue(), hit.getValue().doubleValue() * 100.0 / 10000));
        }
    }
}
