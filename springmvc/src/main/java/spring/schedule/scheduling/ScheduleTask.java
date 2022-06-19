package spring.schedule.scheduling;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract public class ScheduleTask implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final String name;
    private final ScheduleTaskManager manager;

    private long period = 1000;
    private boolean mayInterruptIfRunning = true;
    private boolean suspendOnError = true;
    private boolean suspendImmediate = true;

    public ScheduleTask(String name, ScheduleTaskManager manager) {
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.manager = Objects.requireNonNull(manager, "manager cannot be null!");
    }

    @Override
    public void run() {
        try {
            doTask();
            String format = String.format("finished task named [%s].", name);
            logger.log(Level.FINE, format);
        } catch (Throwable throwable) {
            if (suspendOnError) {
                boolean suspend = manager.addSuspendTask(this, suspendImmediate);
                String format = String.format("error on named task [%s] is [%s] to immediately suspend task with result [%s]!",
                        name, suspendImmediate, suspend);
                logger.log(Level.WARNING, format, throwable);
            } else {
                String format = String.format("error on named task [%s] but we just ignore it.", name);
                logger.log(Level.WARNING, format, throwable);
            }
        }
    }

    abstract protected void doTask();

    public String getName() {
        return name;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean isMayInterruptIfRunning() {
        return mayInterruptIfRunning;
    }

    public void setMayInterruptIfRunning(boolean mayInterruptIfRunning) {
        this.mayInterruptIfRunning = mayInterruptIfRunning;
    }

    public boolean isSuspendOnError() {
        return suspendOnError;
    }

    public void setSuspendOnError(boolean suspendOnError) {
        this.suspendOnError = suspendOnError;
    }

    public boolean isSuspendImmediate() {
        return suspendImmediate;
    }

    public void setSuspendImmediate(boolean suspendImmediate) {
        this.suspendImmediate = suspendImmediate;
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
        return Objects.equals(name, that.name) && Objects.equals(manager, that.manager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, manager);
    }

    @Override
    public String toString() {
        return "ScheduleTask{" +
                "name='" + name + '\'' +
                ", period=" + period +
                ", mayInterruptIfRunning=" + mayInterruptIfRunning +
                ", suspendOnError=" + suspendOnError +
                ", suspendImmediate=" + suspendImmediate +
                '}';
    }

}
