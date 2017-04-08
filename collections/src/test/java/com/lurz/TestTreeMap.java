package com.lurz;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
}
