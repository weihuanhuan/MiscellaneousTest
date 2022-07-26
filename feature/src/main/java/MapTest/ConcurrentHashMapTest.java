package MapTest;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentHashMapTest {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {

        Map<Integer, Object> map = new ConcurrentHashMap<>();

        //并发添加
        Thread thread1 = new Thread(new AddTask(map));
        thread1.start();

        //并发查询
        Thread thread2 = new Thread(new GetTask(map));
        thread2.start();

        //并发删除
        Thread thread3 = new Thread(new RemoveTask(map));
        thread3.start();

        //java.util.concurrent.ConcurrentHashMap 不会出现 java.util.ConcurrentModificationException
        Thread.currentThread().join();
    }

    private static class AddTask implements Runnable {

        Map<Integer, Object> map;

        public AddTask(Map<Integer, Object> map) {
            this.map = map;
        }

        @Override
        public void run() {
            while (true) {
                Integer incrementAndGet = ATOMIC_INTEGER.incrementAndGet();
                map.put(incrementAndGet, new Object());

                sleep(1);
            }
        }
    }

    private static class GetTask implements Runnable {

        Map<Integer, Object> map;

        public GetTask(Map<Integer, Object> map) {
            this.map = map;
        }

        @Override
        public void run() {
            while (true) {
                int l = RANDOM.nextInt(ATOMIC_INTEGER.get());
                Object o = map.get(l);

                sleep(3);
            }
        }
    }

    private static class RemoveTask implements Runnable {

        Map<Integer, Object> map;

        public RemoveTask(Map<Integer, Object> map) {
            this.map = map;
        }

        @Override
        public void run() {
            boolean slow = false;

            while (true) {
                //调节删除的速率，删除的快需要增加每次删除的数量，反之需要减少
                int count = 3 + (slow ? 1 : -1);

                Iterator<Map.Entry<Integer, Object>> iterator = map.entrySet().iterator();
                while (true) {
                    if (!iterator.hasNext()) {
                        slow = false;
                        System.out.println("!iterator.hasNext()");
                        break;
                    }

                    if (count-- < 0) {
                        slow = true;
                        String format = String.format("map.size()=[%s], count-- > 0", map.size());
                        System.out.println(format);
                        break;
                    }

                    iterator.next();
                    iterator.remove();
                }

                sleep(5);
            }
        }
    }

    private static void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
