package spring.schedule.task;

import org.springframework.beans.factory.annotation.Autowired;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.service.BusinessService;

public class BusinessScheduleTask extends ScheduleTask<BusinessBean> {

    @Autowired
    private BusinessService businessService;

    public BusinessScheduleTask(String name, BusinessBean target) {
        super(name, target);
    }

    @Override
    public void doSchedule(BusinessBean businessBean) {
        businessService.doBusiness(businessBean);
    }

}
