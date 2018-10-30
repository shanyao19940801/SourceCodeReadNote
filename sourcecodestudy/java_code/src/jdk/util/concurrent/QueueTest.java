package jdk.util.concurrent;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by user on 2018/4/26.
 */
public class QueueTest {
    @Test
    public void blockQueueTest() throws InterruptedException {
        LinkedBlockingQueue queue = new LinkedBlockingQueue(12);
        LinkedBlockingQueue queue1 = new LinkedBlockingQueue();
//        queue.
        queue.put("");
        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(2);
        arrayBlockingQueue.put("1");
//        arrayBlockingQueue.putIndex;
//        arrayBlockingQueue.ite
//        queue.l
//        queue.put("","");;
    }

    public void finalTest() {}
}
