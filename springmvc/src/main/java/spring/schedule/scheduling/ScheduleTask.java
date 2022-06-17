package spring.schedule.scheduling;

import java.util.Objects;

abstract public class ScheduleTask implements Runnable {

    private final String name;

    public ScheduleTask(String name) {
        this.name = Objects.requireNonNull(name, "schedule task's name cannot be null!");
    }

    @Override
    public void run() {
        doTask();
    }

    abstract protected void doTask();

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleTask that = (ScheduleTask) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
