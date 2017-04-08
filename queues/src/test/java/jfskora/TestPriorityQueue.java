package jfskora;

import com.google.common.collect.MinMaxPriorityQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TestPriorityQueue {

    static final String msgInit = "start     compares= %3d (%3d)";
    static final String msgAdd  = "add  %2s   compares= %3d (%3d)";
    static final String msgPoll = "poll %2s   compares= %3d (%3d)";

    long start;
    boolean DEBUG = true;

    @Rule
    public TestName name = new TestName();

    @Before
    public void before() {
        System.out.println("\n" + name.getMethodName() + " starting");
        start = System.nanoTime();
    }

    @After
    public void after() {
        System.out.println(name.getMethodName() + " took " + (System.nanoTime() - start) + " nanos");
    }

    @Test
    public void testJavaUtilPriorityQueueBasic() throws NoSuchFieldException, IllegalAccessException {

        final Long[] counter = new Long[1];
        counter[0] = 0L;
        Comparator<String> compString = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                counter[0]++;
                return o1.compareTo(o2);
            }
        };

        PriorityQueue<String> testQueue = new PriorityQueue<>(20, compString);
        testQueue(testQueue, counter);
    }

    @Test
    public void testGuavaMinMaxPriorityQueueBasic() throws NoSuchFieldException, IllegalAccessException {

        final Long[] counter = new Long[1];
        counter[0] = 0L;
        Comparator<String> compString = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                counter[0]++;
                return o1.compareTo(o2);
            }
        };

        MinMaxPriorityQueue<String> testQueue = MinMaxPriorityQueue.orderedBy(compString).expectedSize(20).create();
        testQueue(testQueue, counter);
    }

    @Test
    public void testJavaUtilPriorityBlockingQueueBasic() throws NoSuchFieldException, IllegalAccessException {

        final Long[] counter = new Long[1];
        counter[0] = 0L;
        Comparator<String> compString = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                counter[0]++;
                return o1.compareTo(o2);
            }
        };

        PriorityBlockingQueue<String> testQueue = new PriorityBlockingQueue<>(20, compString);
        testQueue(testQueue, counter);
    }
    
    private void testQueue(final AbstractQueue<String> tempQueue, final Long[] counter) throws NoSuchFieldException, IllegalAccessException {
        dumpQueue("init", null, counter, tempQueue);
        dumpQueue("add", "3", counter, tempQueue);
        dumpQueue("add", "5", counter, tempQueue);
        dumpQueue("add", "1", counter, tempQueue);
        dumpQueue("add", "7", counter, tempQueue);
        dumpQueue("add", "6", counter, tempQueue);
        dumpQueue("add", "2", counter, tempQueue);
        dumpQueue("add", "4", counter, tempQueue);
        dumpQueue("add", "8", counter, tempQueue);
        dumpQueue("add", "9", counter, tempQueue);
        dumpQueue("add", "A", counter, tempQueue);
        dumpQueue("add", "B", counter, tempQueue);
        dumpQueue("add", "C", counter, tempQueue);
        dumpQueue("add", "D", counter, tempQueue);
        dumpQueue("add", "E", counter, tempQueue);
        dumpQueue("add", "F", counter, tempQueue);
        dumpQueue("add", "G", counter, tempQueue);
        int pollCount = 0;
        while (tempQueue.size() > 0) {
            dumpQueue("poll", null, counter, tempQueue);
            if (++pollCount == 2) {
                dumpQueue("add", "1", counter, tempQueue);
            }
        }
    }

    private void dumpQueue(String action, String value, Long[] compares, AbstractQueue<String> tmpQueue) throws NoSuchFieldException, IllegalAccessException {
        Long oldCompares = compares[0];
        switch (action) {
            case "init":
                if (DEBUG) {
                    System.out.print(String.format(msgInit, compares[0] - oldCompares, compares[0]));
                }
                break;
            case "add":
                tmpQueue.add(value);
                if (DEBUG) {
                    System.out.print(String.format(msgAdd, value, compares[0] - oldCompares, compares[0]));
                }
                break;
            case "poll":
                String poll = tmpQueue.poll();
                if (DEBUG) {
                    System.out.print(String.format(msgPoll, poll, compares[0] - oldCompares, compares[0]));
                }
                break;
        }
        if (DEBUG) {
            System.out.print("   queue= ");
            Class klass = tmpQueue.getClass();
            Field fieldQueue = klass.getDeclaredField("queue");
            fieldQueue.setAccessible(true);
            Object[] objects = (Object[]) fieldQueue.get(tmpQueue);
            for (Object object : objects) {
                if (object != null) {
                    System.out.print((String) object + " ");
                }
            }
            System.out.println("");
        }
    }

}