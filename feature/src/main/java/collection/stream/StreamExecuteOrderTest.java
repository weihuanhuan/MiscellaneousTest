package collection.stream;

import collection.stream.bean.StreamCollectionContainer;
import collection.stream.bean.StreamTestObject;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamExecuteOrderTest {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {

        testFilterSortedCollectOrder();

    }

    private static void testFilterSortedCollectOrder() {
        List<StreamTestObject> objects = new LinkedList<>();
        initList(4, objects);

        //将 java.util.Spliterator 转换为 java.util.stream.Stream, spliterator 底层依旧使用的 java.util.Iterator
        StreamCollectionContainer<StreamTestObject> container = new StreamCollectionContainer<>(objects);
        Stream<StreamTestObject> stream = StreamSupport.stream(container.spliterator(), false);

        List<StreamTestObject> twoEvenSquares = stream
                .filter(n -> n.getType() % 2 == 0)
                .sorted(Comparator.comparing(StreamTestObject::getPriority))
                .collect(Collectors.toList());

        System.out.println("################ result ################");
        for (StreamTestObject i : twoEvenSquares) {
            System.out.println(i);
        }

    }

    private static void initList(int count, List<StreamTestObject> list) {
        int index = count;
        while (index-- > 0) {
            list.add(newComparedObject());
        }
    }

    private static StreamTestObject newComparedObject() {
        int i = atomicInteger.incrementAndGet();
        return new StreamTestObject(i, 10000 + i);
    }

}
