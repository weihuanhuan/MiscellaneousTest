package spring.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import spring.schedule.config.ScheduleConfig;
import spring.schedule.service.BusinessService;
import spring.schedule.service.BusinessTaskManager;

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

        businessService.addBusinessBean();

        businessService.addBusinessBean();

        TimeUnit.SECONDS.sleep(3600);
    }

}
