package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

abstract public class ScheduleTaskBuilder<T> {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public ScheduleTask<T> build(T bean) {
        ScheduleTask<T> scheduleTask = doBuild(bean);
        beanFactory.autowireBean(scheduleTask);
        return scheduleTask;
    }

    abstract protected ScheduleTask<T> doBuild(T bean);

}
