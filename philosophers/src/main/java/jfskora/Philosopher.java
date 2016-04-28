package jfskora;

import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {
    private final static Integer NUM_PHILOSOPHERS = 4;
    private final static Long EAT_TIME = 1000L;

    private final static ForkLock[] forks = new ForkLock[NUM_PHILOSOPHERS];
    private final static Integer[] timeEaten = new Integer[NUM_PHILOSOPHERS];

    private final Integer index;

    private static class ForkLock extends ReentrantLock {
        Integer index;
        ForkLock(Integer i) {
            super();
            index = i;
        }
        String getOwnerName() {
            return getOwner().getName();
        }
        @Override
        public void lock() {
            super.lock();
            System.out.println("Philosopher " + getOwnerName() + " picks up  fork " + index + lockStates());
            System.out.flush();
        }

        @Override
        public void unlock() {
            final String owner = getOwnerName();
            super.unlock();
            System.out.println("Philosopher " + owner + " puts down fork " + index + lockStates());
            System.out.flush();
        }
    }

    private Philosopher(Integer index) {
        this.index = index;
        timeEaten[index] = 0;
    }

    public void run() {
        Integer leftForkIndex = index;
        Integer rightForkIndex = index != 0 ? index - 1 : NUM_PHILOSOPHERS - 1;

        ForkPair forkPair = new ForkPair(leftForkIndex, rightForkIndex);

        //noinspection InfiniteLoopStatement
        while (true) {
            forkPair.pickUp();
            System.out.println("Philosopher " + index + " eats.");
            System.out.flush();
            try {
                Thread.sleep(EAT_TIME);
            } catch (InterruptedException ignored) {
            }
            forkPair.putDown();
        }
    }

    private synchronized static String lockStates() {
        final StringBuilder states = new StringBuilder(" state = ");
        for (ForkLock fork : forks) {
            states.append(fork.isLocked() ? fork.getOwnerName() : "-");
        }
        return states.toString();
    }

    private class ForkPair {
        final ReentrantLock firstFork;
        final ReentrantLock secondFork;
        final Integer firstForkIndex;
        final Integer secondForkIndex;
        ForkPair(Integer leftIndex, Integer rightIndex) {
            if (leftIndex < rightIndex) {
                firstForkIndex = leftIndex;
                firstFork = forks[leftIndex];
                secondForkIndex = rightIndex;
                secondFork = forks[rightIndex];
            } else {
                firstForkIndex = rightIndex;
                firstFork = forks[rightIndex];
                secondForkIndex = leftIndex;
                secondFork = forks[leftIndex];
            }
        }

        void pickUp() {
            firstFork.lock();
            secondFork.lock();
        }

        void putDown() {
            firstFork.unlock();
            secondFork.unlock();
        }
    }

    public static void main( String[] args )
    {
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

        for (Integer i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ForkLock(i);
        }

        for (Integer i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i);
        }

        System.out.println("Philosophers have been seated " + Philosopher.lockStates());
        for (Philosopher philosopher : philosophers) {
            new Thread(philosopher, philosopher.index.toString()).start();
        }
    }
}
