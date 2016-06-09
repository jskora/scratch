package jfskora;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SingleValueThreadTesting {

    private static final Integer LOOPS = 5;
    private static final Integer RUNNERS = 5;

    private Integer counter;
    private AtomicInteger atomicCounter = new AtomicInteger(0);

    @Before
    public void before() {
        counter = 0;
        atomicCounter.set(0);
    }

    @Test
    public void singleValueColliding() {
        List<Thread> jobs = new ArrayList<>();
        for (int n = 0; n < RUNNERS; n++) {
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int n = 0; n < LOOPS; n++) {
                        int x = counter;
                        x++;
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        counter = x;
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }));
        }
        for (Thread job : jobs) {
            job.start();
        }
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, counter.intValue());
    }

    @Test
    public void singleValueAtomicFailing() {
        List<Thread> jobs = new ArrayList<>();
        for (int n = 0; n < RUNNERS; n++) {
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int n = 0; n < LOOPS; n++) {
                        int x = atomicCounter.get();
                        x++;
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        atomicCounter.set(x);
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }));
        }
        for (Thread job : jobs) {
            job.start();
        }
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, atomicCounter.get());
    }

    @Test
    public void singleValueAtomicWorking() {
        List<Thread> jobs = new ArrayList<>();
        for (int n = 0; n < RUNNERS; n++) {
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int n = 0; n < LOOPS; n++) {
                        int x = atomicCounter.addAndGet(1);
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(50 + sleeper.nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }));
        }
        for (Thread job : jobs) {
            job.start();
        }
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals(RUNNERS * LOOPS, atomicCounter.get());
    }
}
