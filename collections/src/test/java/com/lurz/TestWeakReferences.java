package com.lurz;

import org.junit.Before;
import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestWeakReferences {

    @Before
    public void before() {

    }

    class Widget {
        String name;
        String memo;
        public Widget(String name, String memo) {
            this.name = name;
            this.memo = memo;
        }
        public void finalize() {
            System.out.println(" " + this.name + " finalized()");
        }
    }

    @Test
    public void testWeakHashMapDropsValues() {
        final WeakHashMap<Widget, String> testMap = new WeakHashMap<>();
        Widget widget1 = new Widget("widget1", "water is wet");
        Widget widget2 = new Widget("widget2", "sand is dry");
        Widget widget3 = new Widget("widget3", "cake is lie");

        testMap.put(widget1, widget1.name);
        testMap.put(widget2, widget2.name);
        testMap.put(widget3, widget3.name);

        assertEquals(3, testMap.size());

        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
        PhantomReference<Widget> widget1Phantom = new PhantomReference<>(widget1, null);
        widget1 = null;
        System.gc();
        System.gc();
        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
        PhantomReference<Widget> widget2Phantom = new PhantomReference<>(widget2, null);
        widget2 = null;
        System.gc();
        System.gc();
        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
        System.out.println("size=" + testMap.size() + " => \n   " + testMap.keySet().stream().map(v -> v.toString()).collect(Collectors.joining("\n   ")));
    }
}
