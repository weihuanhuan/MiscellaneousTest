package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Objects;

abstract public class ScheduleTaskBuilder<T> {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    protected final ScheduleTaskManager manager;

    @Value("${schedule.default.task.period:15000}")
    protected long period;
    @Value("${schedule.default.task.may-interrupt-if-running}")
    protected boolean mayInterruptIfRunning;

    @Value("${schedule.default.task.suspend.on-error}")
    protected boolean suspendOnError;
    @Value("${schedule.default.task.suspend.immediate}")
    protected boolean suspendImmediate;

    public ScheduleTaskBuilder(ScheduleTaskManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager cannot be null!");
    }

    public ScheduleTask build(T bean) {
        ScheduleTask scheduleTask = doBuild(bean);
        beanFactory.autowireBean(scheduleTask);
        scheduleTask.setPeriod(period);
        scheduleTask.setMayInterruptIfRunning(mayInterruptIfRunning);
        scheduleTask.setSuspendOnError(suspendOnError);
        scheduleTask.setSuspendImmediate(suspendImmediate);
        return scheduleTask;
    }

    abstract protected ScheduleTask doBuild(T bean);

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

}
