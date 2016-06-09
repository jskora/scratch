package jfskora;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("Convert2Lambda")
public class MultiValueThreadTesting {

    private static final int LOOPS = 10;
    private static final int RUNNERS = 25;

    private Integer counter;
    private Integer jobsRun;
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    private AtomicInteger atomicJobsRun = new AtomicInteger(0);

    private void snooze(Random random, int n) {
        try {
            Thread.sleep(n * 10 + random.nextInt(250));
//            Thread.sleep(n * 5 + random.nextInt(n * 10));
//            Thread.sleep(random.nextInt(25) + random.nextInt(250));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() {
        counter = 0;
        jobsRun = 0;
        atomicCounter.set(0);
        atomicJobsRun.set(0);
    }

    @Test
    public void multiValueColliding() {
        List<Thread> jobs = new ArrayList<>();
        for (int runner = 0; runner < RUNNERS; runner++) {
            final int local_n = runner;
            jobs.add(new Thread(new Runnable() {
                public void run() {                     
                    Random sleeper = new Random();
                    for (int loop = 0; loop < LOOPS; loop++) {
                        snooze(sleeper, local_n);
                        int x = counter;
                        x++;
                        snooze(sleeper, local_n);
                        counter = x;
                        snooze(sleeper, local_n);
                    }
                    snooze(sleeper, local_n);
                    jobsRun++;
                }
            }));
        }
        jobs.forEach(Thread::start);
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, counter.intValue());
        assertNotEquals(RUNNERS, jobsRun.intValue());
    }

    @Test
    public void multiValueAtomicFailing() {
        List<Thread> jobs = new ArrayList<>();
        for (int runner = 0; runner < RUNNERS; runner++) {
            final int local_n = runner;
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int loop = 0; loop < LOOPS; loop++) {
                        snooze(sleeper, local_n);
                        int x = atomicCounter.get();
                        x++;
                        snooze(sleeper, local_n);
                        atomicCounter.set(x);
                        snooze(sleeper, local_n);
                    }
                    int y = atomicJobsRun.get();
                    y++;
                    atomicJobsRun.set(y);
                }
            }));
        }
        jobs.forEach(Thread::start);
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, atomicCounter.intValue());
        assertNotEquals(RUNNERS, atomicJobsRun.intValue());
    }

    @Test
    public void multiValueAtomicWorkingButNotQuite() {
        List<Thread> jobs = new ArrayList<>();
        for (int runner = 0; runner < RUNNERS; runner++) {
            final int local_n = runner;
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int loop = 0; loop < LOOPS; loop++) {
                        snooze(sleeper, local_n);
                        atomicCounter.addAndGet(1);
                        snooze(sleeper, local_n);
                    }
                    atomicJobsRun.incrementAndGet();
                }
            }));
        }
        jobs.forEach(Thread::start);
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, atomicCounter.intValue());
        assertNotEquals(RUNNERS, atomicJobsRun.intValue());
    }

    @Test
    public void multiValueAtomicWorkingFinally() {
        List<Thread> jobs = new ArrayList<>();
        for (int runner = 0; runner < RUNNERS; runner++) {
            final int local_n = runner;
            jobs.add(new Thread(new Runnable() {
                public void run() {
                    Random sleeper = new Random();
                    for (int loop = 0; loop < LOOPS; loop++) {
                        snooze(sleeper, local_n);
                        synchronized(MultiValueThreadTesting.this) {
                            atomicCounter.addAndGet(1);
                        }
                        snooze(sleeper, local_n);
                    }
                    synchronized(MultiValueThreadTesting.this) {
                        atomicJobsRun.incrementAndGet();
                    }
                }
            }));
        }
        jobs.forEach(Thread::start);
        for (Thread job : jobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertNotEquals(RUNNERS * LOOPS, atomicCounter.intValue());
        assertNotEquals(RUNNERS, atomicJobsRun.intValue());
    }
}
