package collection.MapTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentHashMapAddWhenIteratorTest {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        //并发添加
        Thread addThread = new Thread(new AddTask(map), "addThread");
        addThread.start();

        //并发迭代
        boolean useLocalMap = true;
        Thread iteratorThread = new Thread(new IteratorTask(map, useLocalMap), "iteratorThread");
        iteratorThread.start();

        //java.util.concurrent.ConcurrentHashMap 不会出现 java.util.ConcurrentModificationException
        Thread.currentThread().join();
    }

    private static class AddTask implements Runnable {

        private final Map<Integer, Integer> map;

        public AddTask(Map<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            while (true) {
                Integer incrementAndGet = ATOMIC_INTEGER.incrementAndGet();
                map.put(incrementAndGet, incrementAndGet);

                //添加的速度比较块
                System.out.println("put:" + incrementAndGet);
                sleep(100);
            }
        }
    }


    private static class IteratorTask implements Runnable {

        private final Map<Integer, Integer> map;

        private final boolean useLocalMap;

        public IteratorTask(Map<Integer, Integer> map, boolean useLocalMap) {
            this.map = map;
            this.useLocalMap = useLocalMap;
        }

        @Override
        public void run() {
            // 等待添加一些元素
            sleep(500);

            // 本意中想要处理的元素数量
            int expectedMapSize = map.size();
            System.out.println("expectedMapSize:" + expectedMapSize);

            Map<Integer, Integer> workMap;
            //如果不使用 useLocalMap 的话，则在迭代开始后所 put 的元素，也是有会被迭代器看见的，
            // 而且由于 hash map 没有顺序，所以迭代开始后所添加的元素，可能会提前被迭代到，甚至会早于哪些在迭代开始前就添加了的元素
            if (useLocalMap) {
                // 所以我们会看到很多并发场景下，对于集合的遍历，如果不加锁的话，一般是要再 new 一个集合的，这样子就可以防止这里所分析的问题了。
                Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
                workMap = new HashMap<>(map);
            } else {
                workMap = map;
            }

            //记录初始数量之后，以及真实进行迭代前存在时间差，所以使用 expectedMapSize 来控制需要迭代的数量是不准确的
            // 而且由于 hash map 没有顺序，即使迭代数量是准确的，但是可能计数了迭代开始后所添加的元素，使得在迭代开始前就添加了的元素没有被统计到
            sleep(300);
            int workMapSize = workMap.size();
            System.out.println("workMapSize:" + workMapSize);

            // 复制 map 对象期间也可能存在时间差
            sleep(300);
            Collection<Integer> values = workMap.values();
            int workValuesSize1 = values.size();
            System.out.println("workValuesSize1:" + workValuesSize1);

            // 获取迭代器期间也可能存在时间差，
            // 而且 java.util.Map.values 视图并不是一份独立的 copy ，如果底层 map 变化了这个 value 视图也会变化
            sleep(300);
            int workValuesSize2 = values.size();
            System.out.println("workValuesSize2:" + workValuesSize2);
            Iterator<Integer> iterator = values.iterator();

            // 记录真实处理的元素
            List<Integer> actualList = new LinkedList<>();
            while (iterator.hasNext()) {
                Integer next = iterator.next();
                actualList.add(next);

                //迭代的速度比较慢
                System.out.println("next:" + next);
                sleep(200);
            }

            System.out.println("actualList.size():" + actualList.size());
            System.out.println("actualList:" + actualList);

            System.exit(0);
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
