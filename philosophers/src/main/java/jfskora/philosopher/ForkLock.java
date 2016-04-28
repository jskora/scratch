package jfskora.philosopher;

import java.util.concurrent.locks.ReentrantLock;

class ForkLock extends ReentrantLock {

    private Integer index;
    private ForkLock[] forks;

    ForkLock(Integer i, ForkLock[] forks) {
        super();
        index = i;
        this.forks = forks;
    }

    @Override
    public void lock() {
        super.lock();
        System.out.println("Philosopher " + getOwner().getName() + " picks up  fork " + index + lockStates(forks));
        System.out.flush();
    }

    @Override
    public void unlock() {
        final String owner = getOwner().getName();
        super.unlock();
        System.out.println("Philosopher " + owner + " puts down fork " + index + lockStates(forks));
        System.out.flush();
    }

    static synchronized String lockStates(ForkLock[] lockForks) {
        final StringBuilder states = new StringBuilder(" state = ");
        for (ForkLock fork : lockForks) {
            states.append(fork.isLocked() ? fork.getOwner().getName() : "-");
        }
        return states.toString();
    }
}
