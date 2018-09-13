package jdk.util.concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {
    static Map<String,String> map = new HashMap<>();
    static ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    static ReentrantReadWriteLock rw1 = new ReentrantReadWriteLock();
    static final int SHARED_SHIFT   = 16;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
    static Lock r = rw.readLock();
    static Lock w = rw.writeLock();
    static Lock w1 = rw1.writeLock();

    public static Object getValue(String key) {
        try {
            w.lock();
            r.lock();
            return map.get(key);
        } finally {
            System.out.println("unclock");
            r.unlock();
        }
    }

    static void testW() {
//        r.lock();

        w.lock();
        w.lock();
        w.lock();
        w.lock();
    }

    static void testR() {
        r.lock();
        r.lock();
    }
    public static void main(String[] args) {
//        testR();
        testW();
        getValue("1");
        System.out.println(SHARED_UNIT);
        System.out.println(MAX_COUNT);
        System.out.println(EXCLUSIVE_MASK);
    }
}
