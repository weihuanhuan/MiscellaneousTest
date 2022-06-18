package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Objects;

abstract public class ScheduleTaskBuilder<T> {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    protected final ScheduleTaskManager manager;

    public ScheduleTaskBuilder(ScheduleTaskManager manager) {
        this.manager = Objects.requireNonNull(manager, "manager cannot be null!");
    }

    public ScheduleTask build(T bean) {
        ScheduleTask scheduleTask = doBuild(bean);
        beanFactory.autowireBean(scheduleTask);
        return scheduleTask;
    }

    abstract protected ScheduleTask doBuild(T bean);

}
