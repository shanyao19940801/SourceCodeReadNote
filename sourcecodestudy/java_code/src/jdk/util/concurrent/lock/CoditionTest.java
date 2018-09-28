package jdk.util.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoditionTest {
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    public void conditionWait() throws InterruptedException {
        lock.lock();
        try {
            condition.wait();
        } finally {
            lock.unlock();
        }
    }

    public void conditionSignal() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
