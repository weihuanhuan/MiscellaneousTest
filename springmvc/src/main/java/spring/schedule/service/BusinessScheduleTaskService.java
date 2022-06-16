package spring.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.scheduling.ScheduleTaskService;

import java.util.ArrayList;
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

        //avoid ConcurrentModificationException
        List<BusinessBean> copy = new ArrayList<>(businessBeans);

        //filter
        return (copy).stream()
                .filter(businessBean -> businessService.isValid(businessBean))
                .map(builder::build)
                .collect(Collectors.toList());
    }

}
