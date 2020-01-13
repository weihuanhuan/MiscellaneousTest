package lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizedTest {

    public static void main(String[] args) {

        String lockA = new String();
        Object lockB = new Object();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lockA) {
                        System.out.println("thread1-lockA.synchronized");
                        TimeUnit.MILLISECONDS.sleep(1);

                        synchronized (lockB) {
                            System.out.println("thread1-lockB.synchronized");
                            System.out.println("thread1-lockB.wait");
                            //wait 会放弃当前同步块的对象锁，但是不会放弃外层同步块对象的锁
                            lockB.wait(1000 * 5);
                            System.out.println("thread1-lockB.wakeUp");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(25);
                    synchronized (lockB) {
                        System.out.println("thread2-lockB.synchronized");
                        //sleep 不会放弃当前同步块的对象锁
                        TimeUnit.MILLISECONDS.sleep(1000 * 3);
                        System.out.println("thread2-lockB.notifyAll");
                        lockB.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.shutdown();
    }

//    thread1-lockA.synchronized
//    thread1-lockB.synchronized
//    thread1-lockB.wait
//    thread2-lockB.synchronized
//    thread2-lockB.notifyAll
//    thread1-lockB.wakeUp

//    IDEA 的 run 和 debug 获取的线程栈有一丢丢不同，
//    对于 Object.wait 的调用，run依旧显示 - locked <0x000000071631dbc8>, 同时 - waiting on <0x000000071631dbc8>
//    而 debug 则不显示 locked 该对象, 并提示 waiting for pool-1-thread-2@599 to release lock on <0x25c>
//    很明显 run 的显示风格更接近 jstack的样子，而 debug 的显示应该是 IDEA 自己优化后的显示方案。

//IDEA run  DumpThread
//"pool-1-thread-1" #12 prio=5 os_prio=0 tid=0x0000000028499800 nid=0x1418 in Object.wait() [0x0000000029a3e000]
//    java.lang.Thread.State: TIMED_WAITING (on object monitor)
//    at java.lang.Object.wait(Native Method)
//            - waiting on <0x000000071631dbc8> (a java.lang.Object)
//    at lock.LockTest$1.run(LockTest.java:28)
//            - locked <0x000000071631dbc8> (a java.lang.Object)
//            - locked <0x000000071631dbb0> (a java.lang.String)
//    at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
//    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
//    at java.lang.Thread.run(Thread.java:748)
//
//"pool-1-thread-2" #13 prio=5 os_prio=0 tid=0x000000002849c800 nid=0x5348 waiting on condition [0x0000000029b3f000]
//    java.lang.Thread.State: TIMED_WAITING (sleeping)
//    at java.lang.Thread.sleep(Native Method)
//    at java.lang.Thread.sleep(Thread.java:340)
//    at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
//    at lock.LockTest$2.run(LockTest.java:46)
//            - locked <0x000000071631dbc8> (a java.lang.Object)
//    at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//    at java.util.concurrent.FutureTask.run(FutureTask.java:266)
//    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
//    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
//    at java.lang.Thread.run(Thread.java:748)

////IDEA debug  get Thread Dump
//"pool-1-thread-1@591" prio=5 tid=0xe nid=NA waiting
//    java.lang.Thread.State: WAITING
//    waiting for pool-1-thread-2@599 to release lock on <0x25c> (a java.lang.Object)
//    at java.lang.Object.wait(Object.java:-1)
//    at lock.LockTest$1.run(LockTest.java:28)
//            - locked <0x25d> (a java.lang.String)
//    at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//    at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266)
//    at java.util.concurrent.FutureTask.run(FutureTask.java:-1)
//    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
//    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
//    at java.lang.Thread.run(Thread.java:748)
//
//"pool-1-thread-2@599" prio=5 tid=0xf nid=NA sleeping
//    java.lang.Thread.State: TIMED_WAITING
//    blocks pool-1-thread-1@591
//    at java.lang.Thread.sleep(Thread.java:-1)
//    at java.lang.Thread.sleep(Thread.java:340)
//    at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
//    at lock.LockTest$2.run(LockTest.java:46)
//            - locked <0x25c> (a java.lang.Object)
//    at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
//    at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266)
//    at java.util.concurrent.FutureTask.run(FutureTask.java:-1)
//    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
//    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
//    at java.lang.Thread.run(Thread.java:748)

}
