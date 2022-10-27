package concurrency.ThreadTest.filter;

/**
 * Created by JasonFitch on 3/15/2019.
 */
public interface UpdateFilter<T, K> {

    boolean put(T key, K value);

    K get();

    int size();
}
