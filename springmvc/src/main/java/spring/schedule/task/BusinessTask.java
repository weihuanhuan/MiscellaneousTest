package spring.schedule.task;

import org.springframework.beans.factory.annotation.Autowired;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskManager;
import spring.schedule.service.BusinessService;

public class BusinessTask extends ScheduleTask {

    @Autowired
    private BusinessService businessService;

    private final BusinessBean businessBean;

    public BusinessTask(String name, ScheduleTaskManager manager, BusinessBean businessBean) {
        super(name, manager);
        this.businessBean = businessBean;
    }

    @Override
    public void doTask() throws Exception {
        businessService.doBusiness(businessBean);
    }

}
