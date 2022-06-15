package spring.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import spring.schedule.config.ScheduleConfig;
import spring.schedule.service.BusinessScheduleTaskService;

import java.util.concurrent.TimeUnit;

@ContextConfiguration(classes = {ScheduleConfig.class})
@WebAppConfiguration("web")
@ExtendWith(SpringExtension.class)
class SchedulingIntegrationTest {

    @Autowired
    BusinessScheduleTaskService service;

    @Test
    void Run() throws InterruptedException {
        System.out.println(service);

        TimeUnit.SECONDS.sleep(3600);
    }

}
