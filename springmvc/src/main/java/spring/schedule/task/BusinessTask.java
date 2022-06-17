package spring.schedule.task;

import org.springframework.beans.factory.annotation.Autowired;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.service.BusinessService;

public class BusinessTask extends ScheduleTask {

    @Autowired
    private BusinessService businessService;

    private final BusinessBean businessBean;

    public BusinessTask(String name, BusinessBean businessBean) {
        super(name);
        this.businessBean = businessBean;
    }

    @Override
    public void doTask() {
        businessService.doBusiness(businessBean);
    }

}
