package concurrency.ThreadTest.threadlocal;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 1/9/2020.
 */
public class ThreadLocalTest {

    public static void main(String[] args) {
        ThreadLocalHolder threadLocalHolder1 = new ThreadLocalHolder("LocalA");
        ThreadLocalHolder threadLocalHolder2 = new ThreadLocalHolder("LocalB");

        TestThread test1 = new TestThread("Thread1");
        TestThread test2 = new TestThread("Thread2");

        test1.start();
        test2.start();

        boolean first = false;
        while (true) {
            if (first) {
                test1.setThreadLocalHolder(threadLocalHolder1);
            } else {
                test1.setThreadLocalHolder(threadLocalHolder2);
            }

            if (first) {
                test2.setThreadLocalHolder(threadLocalHolder1);
            } else {
                test2.setThreadLocalHolder(threadLocalHolder2);
            }

            first = !first;
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class ThreadLocalHolder {

        String index;
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        public ThreadLocalHolder(String index) {
            this.index = index;
        }

        public String getValue() {
            String name = threadLocal.get();
            if (name == null) {
                String threadName = Thread.currentThread().getName();

                name = threadName + "-" + index;
                if ("Thread2".equals(threadName)) {
                    return name + "-" + "DUMMY";
                }
                threadLocal.set(name);
            }
            return name;
        }

    }

    static class TestThread extends Thread {

        transient ThreadLocalHolder threadLocalHolder;

        public TestThread(String name) {
            super(name);
        }

        public void setThreadLocalHolder(ThreadLocalHolder threadLocalHolder) {
            this.threadLocalHolder = threadLocalHolder;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (threadLocalHolder != null) {
                        //线程的本地存储，不同线程之间是相互隔离的。
                        System.out.println(threadLocalHolder.getValue());
                    }

                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
