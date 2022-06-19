package spring.schedule.service;

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

        BusinessTask businessTask = new BusinessTask(taskName, manager, bean);
        businessTask.setPeriod(1000);
        businessTask.setMayInterruptIfRunning(true);
        businessTask.setSuspendOnError(true);
        businessTask.setSuspendImmediate(true);
        return businessTask;
    }

}
