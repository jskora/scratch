package jfskora;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;

@SuppressWarnings("Convert2Lambda")
public class DemoExecutors {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    public static void main( String[] args ) {
        DemoExecutors executor = new DemoExecutors();
        executor.startJob1();
        executor.startJob2();
        executor.startJob3();
    }

    /*
     * Job1 - repeating task
     */
    private void startJob1() {
        final Runnable job1 = new Runnable() {
            @Override public void run() {
                System.out.println("job1.run() - started");
                System.out.println("job1.run() - sleeping 30 seconds");
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException ignore) {
                    System.out.println("job1.run() - sleeping interrupted");
                }
                System.out.println("job1.run() - finished");
            }
        };
        final ScheduledFuture<?> job1Handler = scheduler.scheduleAtFixedRate(job1, 10, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            @Override public void run() {
                System.out.println("job1Handler .run() - started");
                job1Handler.cancel(true);
                System.out.println("job1Handler .run() - finished");
            }
        }, 10, SECONDS);
    }

    /*
     * Job2 -
     */
    private void startJob2() {
        final ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.execute(new Runnable() {
            final AtomicInteger i = new AtomicInteger(1);
            @Override
            public void run() {
                final int ii = this.i.getAndIncrement();
                System.out.println("job2 (" + ii + ") - " + Thread.currentThread().getName());
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ignore) {
                }
                if (i.get() < 10) {
                    pool.execute(this);
                }
            }
        });
    }

    /*
     * Job3 -
     */
    private void startJob3() {
        final ExecutorService pool = Executors.newFixedThreadPool(3);
        final RunnableFuture<String> future = new RunnableFuture<String>() {
            AtomicReference<String> value = new AtomicReference<>(null);
            AtomicBoolean cancelled = new AtomicBoolean(false);
            AtomicBoolean done = new AtomicBoolean(false);
            @Override
            public void run() {
                System.out.println("job3.run() - started");
                if (!cancelled.get()) {
                    if (!value.compareAndSet(null, new SimpleDateFormat().format(new Date()))) {
                        System.out.println("job3.run() failed - value was not null");
                    } else {
                        if (!done.compareAndSet(false, true)) {
                            System.out.println("job3.run() failed - done was not false");
                        }
                    }
                } else {
                    System.out.println("job3.run() failed - cancelled was true");
                }
                System.out.println("job3.run() - finished");
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (!cancelled.compareAndSet(false, true)) {
                    System.out.println("job3.cancel() failed - cancelled was not false");
                }
                return true;
            }

            @Override
            public boolean isCancelled() {
                return cancelled.get();
            }

            @Override
            public boolean isDone() {
                return done.get();
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                if (done.get() && !isCancelled()) {
                    return value.get();
                } else {
                    return null;
                }
            }

            @Override
            public String get(long timeout, @SuppressWarnings("NullableProblems") TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
        pool.execute(future);
    }
}
