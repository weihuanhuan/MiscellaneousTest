package spring.schedule.scheduling;

import java.util.Objects;

abstract public class ScheduleTask implements Runnable {

    private final String name;

    private final ScheduleTaskManager manager;

    public ScheduleTask(String name, ScheduleTaskManager manager) {
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.manager = Objects.requireNonNull(manager, "manager cannot be null!");
    }

    @Override
    public void run() {
        try {
            doTask();
            String format = String.format("finish task named [%s]", name);
            System.out.println(format);
        } catch (Throwable throwable) {
            boolean addSuspendTask = manager.addSuspendTask(this);
            String format = String.format("suspend task [%s] with status [%s] due to [%s]", name, addSuspendTask, throwable.getMessage());
            System.out.println(format);
        }
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
