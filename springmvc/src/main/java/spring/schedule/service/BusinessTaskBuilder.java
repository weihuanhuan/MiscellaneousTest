package spring.schedule.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskBuilder;
import spring.schedule.task.BusinessTask;

@Service
public class BusinessTaskBuilder extends ScheduleTaskBuilder<BusinessBean> {

    public BusinessTaskBuilder(BusinessTaskManager manager) {
        super(manager);
    }

    @Override
    public ScheduleTask doBuild(BusinessBean bean) {
        String group = bean.getGroup();
        String name = bean.getName();
        String taskName = group + "-" + name;

        return new BusinessTask(taskName, manager, bean);
    }

    @Override
    @Value("${schedule.business.task.period}")
    public void setPeriod(long period) {
        super.setPeriod(period);
    }

    @Override
    @Value("${schedule.business.task.may-interrupt-if-running}")
    public void setMayInterruptIfRunning(boolean mayInterruptIfRunning) {
        super.setMayInterruptIfRunning(mayInterruptIfRunning);
    }

    @Override
    @Value("${schedule.business.task.suspend.on-error}")
    public void setSuspendOnError(boolean suspendOnError) {
        super.setSuspendOnError(suspendOnError);
    }

    @Override
    @Value("${schedule.business.task.suspend.immediate}")
    public void setSuspendImmediate(boolean suspendImmediate) {
        super.setSuspendImmediate(suspendImmediate);
    }

}
