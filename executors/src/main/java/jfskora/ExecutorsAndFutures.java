package jfskora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecutorsAndFutures {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExecutorsAndFutures.class);
    private final static Integer MAX_SLEEP = 10;

    private final int numExecutors;
    private final int numTasks;

    private String[] words = new String[]{
        "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliett", "kilo", "lima",
        "mike", "november", "oscar", "papa", "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey",
        "xray", "yankee", "zulu"
    };

    private Map<Integer, Future<DemoResult>> taskFutures = new ConcurrentHashMap<>();

    private class DemoResult {
        private final int index;
        private final String name;
        private final String desc;
        DemoResult(final int index, final String name, final String desc) {
            this.index = index;
            this.name = name;
            this.desc = desc;
        }
        public int getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return desc;
        }
    }

    private class DemoTask implements Callable<DemoResult> {
        private final int index;
        private final String name;
        private final String desc;

        DemoTask(final int index, final String name) {
            this.index = index;
            this.name = name;
            this.desc = String.format("%03d-%-8s", index, name);
        }

        @Override
        public DemoResult call() throws Exception {
            LOGGER.info("task starting {} {} {} {}",
                    String.format("%-12s", getDescription()), "            ", "            ",
                    Thread.currentThread().getName());
            Thread.sleep((index % MAX_SLEEP) * 100);
            DemoResult result = new DemoResult(index, name, desc);
            LOGGER.info("task stopping {} {} {} {}",
                    "            ", String.format("%-12s", getDescription()), "            ",
                    Thread.currentThread().getName());
            return result;
        }

        public int getIndex() {
            return index;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return desc;
        }
    }

    ExecutorsAndFutures(final int numExecutors, final int numTasks) {
        this.numExecutors = numExecutors;
        this.numTasks = numTasks;
    }

    private void run() {
        ExecutorService executor = Executors.newFixedThreadPool(numExecutors);

        List<DemoTask> tasks = new ArrayList<>();

        LOGGER.info("creating and submitting tasks");
        LOGGER.info("task header   {} {} {} {}",
                "starting    ", "stopping    ", "finished    ",
                "other");
        LOGGER.info("------------- {} {} {} {}",
                "------------", "------------", "------------", "---------------");
        for (int i = 0; i < numTasks; i++) {
            DemoTask task = new DemoTask(i, words[i % words.length]);
            tasks.add(task);
            taskFutures.put(i, executor.submit(task));
        }

        while (!taskFutures.isEmpty()) {
            for (Integer id : taskFutures.keySet()) {
                Future<DemoResult> future = taskFutures.get(id);
                if (future.isDone()) {
                    try {
                        DemoResult result = future.get();
                        taskFutures.remove(id);
                        LOGGER.info("task finished {} {} {} {} left",
                                "            ", "            ", String.format("%-12s", result.getDescription()),
                                taskFutures.size());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<Runnable> failed = executor.shutdownNow();
        LOGGER.info("done failed={}", failed.size());
    }

    public static void main(String[] args) {
        LOGGER.info("main starting (logger)");
        System.out.println("main starting (sysout)");
        ExecutorsAndFutures test = new ExecutorsAndFutures(3, 35);
        test.run();
    }
}
