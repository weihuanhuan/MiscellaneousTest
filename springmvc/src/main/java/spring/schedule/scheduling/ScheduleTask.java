package spring.schedule.scheduling;

import java.util.Objects;

abstract public class ScheduleTask<T> implements Runnable {

    private final String name;

    private final T target;

    public ScheduleTask(String name, T target) {
        Objects.requireNonNull(name, "schedule task's name cannot be null!");
        Objects.requireNonNull(target, "schedule task's target cannot be null!");

        this.name = name;
        this.target = target;
    }

    @Override
    public void run() {
        doSchedule(target);
    }

    abstract protected void doSchedule(T target);

    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleTask<T> that = (ScheduleTask<T>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
