package concurrency.lock.condition.multiple.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NoticableSharedCondition {

    private final List<NoticableSharedLinkedBlockingDeque<?>> deques = new ArrayList<>();

    private final Lock sharedLock;
    private final Condition sharedCondition;

    public NoticableSharedCondition(boolean fairness) {
        this.sharedLock = new ReentrantLock(fairness);
        this.sharedCondition = sharedLock.newCondition();
    }

    public void registerDeque(NoticableSharedLinkedBlockingDeque<?> deque) {
        if (deque == null) {
            return;
        }

        deques.add(deque);
    }

    public boolean unregisterDeque(NoticableSharedLinkedBlockingDeque<?> deque) {
        if (deque == null) {
            return false;
        }

        return deques.remove(deque);
    }

    public boolean hasTakeWaiters() {
        for (NoticableSharedLinkedBlockingDeque<?> deque : deques) {
            if (deque.hasTakeWaiters()) {
                return true;
            }
        }
        return false;
    }

    public void signalAll() {
        sharedLock.lock();
        try {
            sharedCondition.signalAll();
        } finally {
            sharedLock.unlock();
        }
    }

    public void await() throws InterruptedException {
        sharedLock.lock();
        try {
            sharedCondition.await();
        } finally {
            sharedLock.unlock();
        }
    }

}