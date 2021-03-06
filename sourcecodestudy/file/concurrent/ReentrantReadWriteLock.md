# 读写锁ReentrantReadWriteLock

读写锁维护了一对锁，一个读锁和一个写锁，通过分离读锁和写锁，使得**并发性相比一般的排他锁有了很大提升**。除了保证写操作对读操作的可见性以及并发性的提升之外，读写锁能够**简化读写交互场景的编程方式**。假设在程序中定义一个共享的用作缓存数据结构，它大部分时间提供读服务（例如查询和搜索），而写操作占有的时间很少，但是写操作完成之后的更新需要对后续的读服务可见。
一般情况下，读写锁的性能都会比排它锁好，因为**大多数场景读是多于写的**。在读多于写的情况下，读写锁能够提供比排它锁更好的并发性和吞吐量

当线程获取读锁其他线程对于读锁和写锁均被阻塞

### 读写锁的特性

* 支持公平性选择，默认非公平，非公平优于公平
* 支持重进入
* 支持锁降级，写锁能降级成读锁

### 读写状态
源码中有一个state成员变量就是用来表示当前状态，高16位标示读，低16位标示写

![图1](https://github.com/shanyao19940801/SourceCodeReadNote/blob/master/sourcecodestudy/file/image/ReentrantReadWriteLock01.JPG)

    private volatile int state;

我们看到源码中有下面几个常量

        static final int SHARED_SHIFT   = 16;//32位的中间位
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);//写锁的移动单位，每当一个线程获取写锁state基础上加上这个值
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;//最大重入锁次数
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;



#### 读状态

每当一个线程获取读锁则在SHARED_UNIT加上state，这就相当于在高16位上加一<br>
下面通过源码来具体分析是如何实现的

       protected final int tryAcquireShared(int unused) {
			//下面这段英文解释的已经很详细了
            /*
             * Walkthrough:
             * 1. If write lock held by another thread, fail.
             * 2. Otherwise, this thread is eligible for
             *    lock wrt state, so ask if it should block
             *    because of queue policy. If not, try
             *    to grant by CASing state and updating count.
             *    Note that step does not check for reentrant
             *    acquires, which is postponed to full version
             *    to avoid having to check hold count in
             *    the more typical non-reentrant case.
             * 3. If step 2 fails either because thread
             *    apparently not eligible or CAS fails or count
             *    saturated, chain to version with full retry loop.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            if (exclusiveCount(c) != 0 &&
                getExclusiveOwnerThread() != current)
                return -1;
            int r = sharedCount(c);
            if (!readerShouldBlock() &&
                r < MAX_COUNT &&
                compareAndSetState(c, c + SHARED_UNIT)) {
                if (r == 0) {
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {
                    firstReaderHoldCount++;
                } else {
                    HoldCounter rh = cachedHoldCounter;
                    if (rh == null || rh.tid != getThreadId(current))
                        cachedHoldCounter = rh = readHolds.get();
                    else if (rh.count == 0)
                        readHolds.set(rh);
                    rh.count++;
                }
                return 1;
            }
            return fullTryAcquireShared(current);
        }

		//这个方法用来确定当前线程已经进入锁的次数
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }



#### 写状态

写状态不可共享所以一般只会有同个线程多次进入，每次state+1

       protected final boolean tryAcquire(int acquires) {
            /*
             * Walkthrough:
             * 1. If read count nonzero or write count nonzero
             *    and owner is a different thread, fail.
             * 2. If count would saturate, fail. (This can only
             *    happen if count is already nonzero.)
             * 3. Otherwise, this thread is eligible for lock if
             *    it is either a reentrant acquire or
             *    queue policy allows it. If so, update state
             *    and set owner.
             */
            Thread current = Thread.currentThread();
			//获取当前锁状态，如果c不等于0说明当前有线程正在占用锁状态
            int c = getState();
			//c与低16位全部是1的值进行与运算，根据c，w是否为0可以判断出当前锁的读写状态
			//
            int w = exclusiveCount(c);
            if (c != 0) {
                // (Note: if c != 0 and w == 0 then shared count != 0)
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }
            if (writerShouldBlock() ||
                !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }


### 锁降级

顾名思义锁从写状态降级到读状态，注意如果线程持有写锁然后释放再持有读锁这种情况不算锁降级，只有当前持有写锁再获取读锁才能成为锁降级。

    public static void processDate() {
        r.lock();
        try {
            if (!update) {
                //必须先释放读锁
                r.unlock();
                //锁降级从写锁获取到开始
                w.lock();
                try {
                    if (!update) {
                        //准备数据的流程
                        update = true;
                    }
                    r.lock();
                } finally {
                    w.unlock();
                }
                //锁降级完成，锁降级为读锁

            }
        } finally {
            r.unlock();
        }
    }


* 锁降级中读锁的获取是否必要呢？

答案是必要的。主要是为了**保证数据的可见性**，如果当前线程不获取读锁而是直接释放写锁，假设此刻另一个线程（记作线程T）获取了写锁并修改了数据，那么当前线程无法感知线程T的数据更新。如果当前线程获取读锁，即遵循锁降级的步骤，则线程T将会被阻塞，直到当前线程使用数据并释放读锁之后，线程T才能获取写锁进行数据更新。