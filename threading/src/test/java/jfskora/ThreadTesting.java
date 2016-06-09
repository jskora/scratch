package jfskora;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ThreadTesting {

    Integer counter;

    @Test
    public void threadsBadly() {
        counter = 0;
        List<Thread> jobs = new ArrayList<>();
        jobs.add(new Thread(new Runnable() {
            public void run() {
                for (int n = 0; n < 5; n++) {
                    int x = counter;
                    x++;
                    counter = x;
                    System.out.printf("thread=%s counter=%d", Thread.currentThread().getName(), counter);
                }
            }
        }));
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
        System.out.println("-done-");
    }
}
