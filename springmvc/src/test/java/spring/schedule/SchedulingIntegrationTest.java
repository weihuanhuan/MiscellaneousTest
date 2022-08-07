package spring.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import spring.schedule.config.ScheduleConfig;
import spring.schedule.entity.BusinessBean;
import spring.schedule.scheduling.ScheduleTask;
import spring.schedule.service.BusinessService;
import spring.schedule.service.BusinessTaskManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = {ScheduleConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
class SchedulingIntegrationTest {

    @Autowired
    ScheduleConfig scheduleConfig;

    @Autowired
    BusinessTaskManager businessTaskService;

    @Autowired
    BusinessService businessService;

    @Test
    void Run() throws InterruptedException {
        System.out.println(scheduleConfig);
        System.out.println(businessTaskService);

        //测试结束时间，用来停止 spring 容器，以触发 org.springframework.scheduling.concurrent.ExecutorConfigurationSupport.shutdown
        TimeUnit.MILLISECONDS.sleep(1000 * 30);

        System.out.println("###################### stop test run ######################");

        Set<ScheduleTask> workScheduleTasks = businessTaskService.getWorkScheduleTasks();
        Set<ScheduleTask> newScheduleTasks = businessTaskService.getNewScheduleTasks();
        Set<ScheduleTask> suspendScheduleTasks = businessTaskService.getSuspendScheduleTasks();
        System.out.println("workScheduleTasks.size()=" + workScheduleTasks.size());
        System.out.println("newScheduleTasks.size()=" + newScheduleTasks.size());
        System.out.println("suspendScheduleTasks.size()=" + suspendScheduleTasks.size());

        Map<String, BusinessBean> businessBeansMap = businessService.getBusinessBeansMap();
        System.out.println("businessBeansMap.size()=" + businessBeansMap.size());
    }

}
