package spring.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import spring.schedule.scheduling.TaskManagementService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
@PropertySource(value = "classpath:spring-schedule.properties", ignoreResourceNotFound = true)
@EnableScheduling
@ComponentScan(basePackages = {"spring.schedule.scheduling", "spring.schedule.service"})
public class ScheduleConfig implements SchedulingConfigurer {

    @Value("${schedule.scheduler.max.pool.size:32}")
    private int maxPoolSize;
    @Value("${schedule.scheduler.thread.prefix:task-scheduler-}")
    private String scheduleThreadPrefix;

    @Value("${schedule.scheduler.auto.update.task}")
    private boolean autoUpdateTask;
    @Value("${schedule.scheduler.auto.schedule.task}")
    private boolean autoScheduleTask;

    @Autowired(required = false)
    private List<TaskManagementService> serviceList;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        int poolSize = Runtime.getRuntime().availableProcessors() * 4;
        scheduler.setPoolSize(Math.min(poolSize, maxPoolSize));
        scheduler.setThreadNamePrefix(scheduleThreadPrefix);
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());

        Optional.ofNullable(serviceList).orElse(Collections.emptyList()).forEach(service -> {
            if (autoUpdateTask) {
                taskRegistrar.addFixedDelayTask(service.createUpdateTask());
            }
            if (autoScheduleTask) {
                taskRegistrar.addFixedDelayTask(service.createScheduleTask());
            }
        });

        taskRegistrar.afterPropertiesSet();
    }

}
