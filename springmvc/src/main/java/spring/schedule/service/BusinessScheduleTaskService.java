package spring.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessScheduleTaskService extends ScheduleTaskService<BusinessBean> {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessScheduleTaskBuilder builder;

    @Override
    protected List<ScheduleTask<BusinessBean>> findScheduleTask() {
        List<BusinessBean> businessBeans = businessService.getBusinessBeans();
        if (businessBeans == null || businessBeans.isEmpty()) {
            return Collections.emptyList();
        }

        return businessBeans.stream()
                .filter(businessBean -> businessService.isValid(businessBean))
                .map(builder::build)
                .collect(Collectors.toList());
    }

}
