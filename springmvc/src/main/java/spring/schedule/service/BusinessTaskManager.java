package spring.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskAutoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessTaskManager extends ScheduleTaskAutoManager {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessTaskBuilder builder;

    @Override
    protected List<ScheduleTask> findScheduleTasks() {
        List<BusinessBean> businessBeans = businessService.getBusinessBeans();

        //avoid ConcurrentModificationException
        List<BusinessBean> copy = new ArrayList<>(businessBeans);

        //filter
        return (copy).stream().filter(businessBean -> {
            try {
                return businessService.isValid(businessBean);
            } catch (Throwable throwable) {
                return false;
            }
        }).map(builder::build).collect(Collectors.toList());
    }

    @Value("${schedule.business.initial.update.delay:15000}")
    public void setInitialUpdateDelay(long initialUpdateDelay) {
        super.setInitialUpdateDelay(initialUpdateDelay);
    }

    @Override
    @Value("${schedule.business.fixed.update.delay:15000}")
    public void setFixedUpdateDelay(long fixedUpdateDelay) {
        super.setFixedUpdateDelay(fixedUpdateDelay);
    }

    @Override
    @Value("${schedule.business.initial.schedule.delay:15000}")
    public void setInitialScheduleDelay(long initialScheduleDelay) {
        super.setInitialScheduleDelay(initialScheduleDelay);
    }

    @Override
    @Value("${schedule.business.fixed.schedule.delay:15000}")
    public void setFixedScheduleDelay(long fixedScheduleDelay) {
        super.setFixedScheduleDelay(fixedScheduleDelay);
    }

}
