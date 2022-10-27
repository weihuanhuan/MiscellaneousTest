package concurrency.ThreadTest.deadlock;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 10/21/2019.
 */
public class ThreadDeadLock {

    public static void main(String[] args) {

        Object o1 = new Object();
        Object o2 = new Object();

        Thread thread1 = new Thread(new DeadLockTask(o1, o2));
        Thread thread2 = new Thread(new DeadLockTask(o2, o1));

        thread1.start();
        thread2.start();

        //JF jstack 可以发现死锁信息
//        Found one Java-level deadlock:
//=============================
//        "Thread-1":
//        waiting to lock monitor 0x000000001986dd38 (object 0x0000000780e16e20, a java.lang.Object),
//        which is held by "Thread-0"
//        "Thread-0":
//        waiting to lock monitor 0x0000000019870518 (object 0x0000000780e16e10, a java.lang.Object),
//        which is held by "Thread-1"
//
//        Java stack information for the threads listed above:
//===================================================
//        "Thread-1":
//        at ThreadTest.deadlock.ThreadDeadLock$DeadLockTask.run(ThreadDeadLock.java:64)
//        - waiting to lock <0x0000000780e16e20> (a java.lang.Object)
//        - locked <0x0000000780e16e10> (a java.lang.Object)
//        at java.lang.Thread.run(Thread.java:748)
//        "Thread-0":
//        at ThreadTest.deadlock.ThreadDeadLock$DeadLockTask.run(ThreadDeadLock.java:64)
//        - waiting to lock <0x0000000780e16e10> (a java.lang.Object)
//        - locked <0x0000000780e16e20> (a java.lang.Object)
//        at java.lang.Thread.run(Thread.java:748)
//
//        Found 1 deadlock.
    }

    static class DeadLockTask implements Runnable {

        Object o1;
        Object o2;

        public DeadLockTask(Object o1, Object o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        @Override
        public void run() {
            try {
                synchronized (o2) {
                    TimeUnit.SECONDS.sleep(3);
                    synchronized (o1) {
                        TimeUnit.SECONDS.sleep(3600);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
