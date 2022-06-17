package spring.schedule.service;

import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskBuilder;
import spring.schedule.task.BusinessTask;

@Service
public class BusinessTaskBuilder extends ScheduleTaskBuilder<BusinessBean> {

    @Override
    public ScheduleTask doBuild(BusinessBean bean) {
        String group = bean.getGroup();
        String name = bean.getName();

        String taskName = group + "-" + name;
        return new BusinessTask(taskName, bean);
    }

}
