package jfskora;

import java.util.Arrays;

public class Philosopher implements Runnable {

    private final static Integer NUM_PHILOSOPHERS = 5;
    private final static Long EAT_TIME = 1000L;

    private final static ForkLock[] forks = new ForkLock[NUM_PHILOSOPHERS];

    private final Integer index;

    private Philosopher(Integer index) {
        this.index = index;
    }

    public void run() {
        Integer leftForkIndex = index;
        Integer rightForkIndex = index == 0 ? NUM_PHILOSOPHERS - 1 : index - 1;

        ForkPair forkPair = new ForkPair(leftForkIndex, rightForkIndex, forks);

        //noinspection InfiniteLoopStatement
        while (true) {
            forkPair.pickUp();
            System.out.println("Philosopher " + index + " eats                           " + index);
            System.out.flush();
            try {
                Thread.sleep(EAT_TIME);
            } catch (InterruptedException ignored) {
            }
            forkPair.putDown();
        }
    }

    public static void main( String[] args )
    {
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

        for (Integer i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ForkLock(i, forks);
        }

        for (Integer i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i);
        }

        System.out.println("Philosophers have been seated " + ForkLock.lockStates(forks));
        for (Philosopher philosopher : philosophers) {
            new Thread(philosopher, philosopher.index.toString()).start();
        }
    }
}
