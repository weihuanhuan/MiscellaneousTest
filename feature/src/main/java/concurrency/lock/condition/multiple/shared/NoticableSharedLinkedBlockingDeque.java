package concurrency.lock.condition.multiple.shared;

import concurrency.lock.condition.queue.LinkedBlockingDeque;

public class NoticableSharedLinkedBlockingDeque<E> extends LinkedBlockingDeque<E> {

    private final NoticableSharedCondition noticableSharedCondition;

    public NoticableSharedLinkedBlockingDeque(boolean fairness, NoticableSharedCondition noticableSharedCondition) {
        this(Integer.MAX_VALUE, fairness, noticableSharedCondition);
    }

    public NoticableSharedLinkedBlockingDeque(int capacity, boolean fairness, NoticableSharedCondition noticableSharedCondition) {
        super(capacity, fairness);
        this.noticableSharedCondition = noticableSharedCondition;

        this.noticableSharedCondition.registerDeque(this);
    }

    @Override
    protected void notice() {
        noticableSharedCondition.signalAll();
    }

    @Override
    public boolean hasTakeWaiters() {
        return super.hasTakeWaiters();
    }

}