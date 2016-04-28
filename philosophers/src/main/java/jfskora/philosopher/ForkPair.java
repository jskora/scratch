package jfskora.philosopher;

import jfskora.philosopher.ForkLock;

import java.util.concurrent.locks.ReentrantLock;

public class ForkPair {
    final ReentrantLock firstFork;
    final ReentrantLock secondFork;
    final Integer firstForkIndex;
    final Integer secondForkIndex;
    ForkPair(Integer leftIndex, Integer rightIndex, ForkLock[] forks) {
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
