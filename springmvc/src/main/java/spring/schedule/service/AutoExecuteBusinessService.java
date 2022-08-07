package spring.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.util.Map;

@Service
public class AutoExecuteBusinessService {

    @Autowired
    private BusinessService businessService;

    //spring 注解添加的 task，是通过 ScheduledTaskRegistrar.afterPropertiesSet 进行调度的，使用统一的 scheduler。
    //由于我们的任务数量远大于线程数量，而 @Scheduled 和他们使用同一个 scheduler，就会出现线程不够用，导致实际创建 bean 的速度比较慢
    @Scheduled(fixedDelay = 5)
    public void addBusinessBean() {
        Map<String, BusinessBean> businessBeansMap = businessService.getBusinessBeansMap();
        if (businessBeansMap.size() > 1000) {
            return;
        }

        //持续补充任务，防止所有的任务被 BusinessService.isValid 和 BusinessService.doBusiness 中发生的异常所清空
        businessService.addBusinessBean();
    }

}
