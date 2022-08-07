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
            //support thread interrupt signal
            boolean interrupted = Thread.currentThread().isInterrupted();
            if (mayInterruptIfRunning && interrupted) {
                String format = String.format("variable Thread.currentThread().isInterrupted() is [%s] on named task [%s], just return to end run method!", interrupted, name);
                logger.log(Level.WARNING, format);
                return;
            }

            doTask();
            String format = String.format("finished task named [%s].", name);
            logger.log(Level.FINE, format);
        } catch (Throwable throwable) {
            if (suspendOnError) {
                boolean suspend = manager.addSuspendTask(this, suspendImmediate);
                String format = String.format("error [%s]:[%s] occur on named task [%s], it is [%s] to immediately suspend task with result [%s]!",
                        throwable.getClass(), throwable.getMessage(), name, suspendImmediate, suspend);
                logger.log(Level.FINE, format);
            }

            //support thread interrupt signal, and propagate interrupt signal
            //这里注意我们的 run 方法中没有使用 loop 执行任务的，所以这里不论是否 return， 这个任务的 thread 都是会结束的
            if (mayInterruptIfRunning && throwable instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                String format = String.format("catch an InterruptedException on named task [%s], just return to end run method!", name);
                logger.log(Level.WARNING, format, throwable);
                return;
            }

            //propagate to
            // org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler.setErrorHandler
            // org.springframework.scheduling.support.DelegatingErrorHandlingRunnable
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else if (throwable instanceof Error) {
                throw (Error) throwable;
            } else {
                String format = String.format("error [%s]:[%s] occur on named task [%s], but there is no need to propagate it, so we just ignore!",
                        throwable.getClass(), throwable.getMessage(), name);
                logger.log(Level.FINE, format);
            }
        }
    }

    abstract protected void doTask() throws Exception;

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
