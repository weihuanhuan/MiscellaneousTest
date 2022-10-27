package collection.stream.bean;

import java.util.Iterator;
import java.util.List;

//包装原始的 list 用于控制其 iterator 的使用
//注意这里实现的是 java.lang.Iterable ,用于给 caller 提供 iterator 实例本身
public class StreamCollectionContainer<T> implements Iterable<T> {

    private final List<T> list;

    public StreamCollectionContainer(List<T> list) {
        this.list = list;
    }

    @Override
    public Iterator<T> iterator() {
        //包装原始的 iterator 用于打印其调用情况
        return new IteratorWrapper<>(list.iterator());
    }

    //注意这里实现的是 java.lang.Iterable ,用于提供 iterator 的 next 和 hasNext 方法实现
    private static class IteratorWrapper<T> implements Iterator<T> {

        Iterator<T> iterator;

        public IteratorWrapper(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override

        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            T next = iterator.next();
            System.out.println("next:" + next);
            return next;
        }
    }

}
