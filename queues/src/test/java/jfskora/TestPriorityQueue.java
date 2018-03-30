package jfskora;

import com.google.common.collect.MinMaxPriorityQueue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TestPriorityQueue {

    static final String msgInit = "start     compares= %3d (%3d)";
    static final String msgAdd  = "add  %2s   compares= %3d (%3d)";
    static final String msgPoll = "poll %2s   compares= %3d (%3d)";

    static Map<String, List<Long>> runTimes = new HashMap<>();

    boolean DEBUG = false;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testAllQueues() throws NoSuchFieldException, IllegalAccessException, InstantiationException {

        final Long[] counter = new Long[1];
        counter[0] = 0L;
        Comparator<String> compString = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                counter[0]++;
                return o1.compareTo(o2);
            }
        };

        PriorityQueue<String> testPriorityQueue;
        PriorityBlockingQueue<String> testPriorityBlockingQueue;
        PriorityQueueExtended<String> testPriorityQueueExtended;
        MinMaxPriorityQueue<String> testMinMaxPriorityQueue;

        runTimes.put(PriorityQueue.class.getName(), new ArrayList<>());
        runTimes.put(PriorityBlockingQueue.class.getName(), new ArrayList<>());
        runTimes.put(PriorityQueueExtended.class.getName(), new ArrayList<>());
        runTimes.put(MinMaxPriorityQueue.class.getName(), new ArrayList<>());

        testPriorityQueue = new PriorityQueue<>(20, compString);
        testQueue(testPriorityQueue, counter);
        testPriorityBlockingQueue = new PriorityBlockingQueue<>(20, compString);
        testQueue(testPriorityBlockingQueue, counter);
        testPriorityQueueExtended = new PriorityQueueExtended<>(20, compString);
        testQueue(testPriorityQueueExtended, counter);
        testMinMaxPriorityQueue = MinMaxPriorityQueue.orderedBy(compString).expectedSize(20).create();
        testQueue(testMinMaxPriorityQueue, counter);

        System.out.println("");
        int width = runTimes.keySet().stream().mapToInt(String::length).max().orElse(10);
        for (String key : runTimes.keySet()) {
            long total = runTimes.get(key).stream().mapToLong(Long::longValue).sum();
            long avg = total / runTimes.get(key).size();
            System.out.println(String.format("%-" + width + "s %12d %12d", key, total, avg));
        }
    }

    @Ignore
    @Test
    public void testJavaUtilPriorityQueue() throws NoSuchFieldException, IllegalAccessException, InstantiationException {

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
        runTimes.put(testQueue.getClass().getName(), new ArrayList<>());
        testQueue(testQueue, counter);
    }

    @Ignore
    @Test
    public void testJavaUtilPriorityBlockingQueue() throws NoSuchFieldException, IllegalAccessException, InstantiationException {

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
        runTimes.put(testQueue.getClass().getName(), new ArrayList<>());
        testQueue(testQueue, counter);
    }

    @Ignore
    @Test
    public void testPriorityQueueExtended() throws NoSuchFieldException, IllegalAccessException, InstantiationException {

        final Long[] counter = new Long[1];
        counter[0] = 0L;
        Comparator<String> compString = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                counter[0]++;
                return o1.compareTo(o2);
            }
        };

        PriorityQueueExtended<String> testQueue = new PriorityQueueExtended<>(20, compString);
        runTimes.put(testQueue.getClass().getName(), new ArrayList<>());
        testQueue(testQueue, counter);
    }

    @Ignore
    @Test
    public void testGuavaMinMaxPriorityQueue() throws NoSuchFieldException, IllegalAccessException, InstantiationException {

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
        runTimes.put(testQueue.getClass().getName(), new ArrayList<>());
        testQueue(testQueue, counter);
    }

    private void testQueue(final AbstractQueue<String> tempQueue, final Long[] counter) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        long start = System.nanoTime();
        List<String> charStrings = new ArrayList<>();
        for (int c = 32; c <= 122; c++) {
            charStrings.add(String.valueOf((char)c));
        }

        modifyQueue("init", null, counter, tempQueue);

        String buf = "351762489ABCDEFG";
        for (int i = 0; i < buf.length(); i++) {
            modifyQueue("add", buf.substring(i, i+1), counter, tempQueue);
        }
        for (int j = 0; j < 10000; j++) {
            for (String s : charStrings) {
                tempQueue.add(s);
                modifyQueue("add", s, counter, tempQueue);
            }
        }
        while (tempQueue.size() > 0) {
            modifyQueue("poll", null, counter, tempQueue);
        }
//        Class queueKlass = tempQueue.getClass();
//        AbstractQueue dupe = (AbstractQueue) queueKlass.newInstance();
//        while (tempQueue.size() > 0) {
//            dupe.add(tempQueue.poll());
//        }
        long runtime = System.nanoTime() - start;
        System.out.println(tempQueue.getClass().getName() + " took " + runtime + " nanos");
        runTimes.get(tempQueue.getClass().getName()).add(runtime);
    }

    private void modifyQueue(String action, String value, Long[] compares, AbstractQueue<String> tmpQueue) throws NoSuchFieldException, IllegalAccessException {
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
            dumpQueue(tmpQueue);
        }
    }

    private void dumpQueue(AbstractQueue<String> tmpQueue) throws NoSuchFieldException, IllegalAccessException {
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