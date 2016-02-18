package jfskora;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class TestLinkedBlockingQueue {

    //LinkedBlockingQueue object created with size 1
    private static LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>(1);

    //Producer class that will item in LinkedBlockingQueue object
    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int i = 1;
                while (i <= 5) {
                    //add item
                    lbq.put("A" + i);
                    System.out.println("A" + i + " added.");
                    i++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Consumer class that will consume or remove item from LinkedBlockingQueue object
    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                int i = 1;
                while (i <= 5) {
                    //removes the item
                    String s = lbq.take();
                    System.out.println(s + " removed.");
                    i++;
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void TestConsumerProducer() {
        Producer producer = new TestLinkedBlockingQueue.Producer();
        Thread prod = new Thread(producer);
        prod.start();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
        Consumer consumer = new TestLinkedBlockingQueue.Consumer();
        Thread cons = new Thread(consumer);
        cons.start();
        while ((prod.getState() != Thread.State.TERMINATED) || (cons.getState() != Thread.State.TERMINATED)) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignore) {
            }
        }
    }

}
