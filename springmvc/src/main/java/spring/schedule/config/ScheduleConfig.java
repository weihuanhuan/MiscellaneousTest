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
import org.springframework.util.ErrorHandler;
import spring.schedule.scheduling.ScheduleTaskAutoManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@PropertySource(value = "classpath:spring-schedule.properties", ignoreResourceNotFound = true)
@EnableScheduling
@ComponentScan(basePackages = {"spring.schedule.scheduling", "spring.schedule.service"})
public class ScheduleConfig implements SchedulingConfigurer {

    @Value("${schedule.scheduler.max.pool.size:32}")
    private int maxPoolSize;
    @Value("${schedule.scheduler.thread.prefix:default-task-scheduler-}")
    private String scheduleThreadPrefix;
    @Value("${schedule.scheduler.await.termination.seconds:10}")
    private int awaitTerminationSeconds;

    @Value("${schedule.scheduler.auto.update.task}")
    private boolean autoUpdateTask;
    @Value("${schedule.scheduler.auto.schedule.task}")
    private boolean autoScheduleTask;

    @Autowired(required = false)
    private List<ScheduleTaskAutoManager> autoManagers;

    //specify the bean name in order to inject with @Qualifier to prevent conflicts between beans of the same type
    @Bean(name = "scheduleTaskThreadPoolTaskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler scheduleTaskThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        int poolSize = Runtime.getRuntime().availableProcessors() * 4;
        scheduler.setPoolSize(Math.min(poolSize, maxPoolSize));
        scheduler.setThreadNamePrefix(scheduleThreadPrefix);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setDaemon(true);

        ErrorHandler scheduleErrorHandler = new ScheduleErrorHandler();
        scheduler.setErrorHandler(scheduleErrorHandler);
        ThreadPoolExecutor.DiscardPolicy discardPolicy = new ThreadPoolExecutor.DiscardPolicy();
        scheduler.setRejectedExecutionHandler(discardPolicy);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduleTaskThreadPoolTaskScheduler());

        Optional.ofNullable(autoManagers).orElse(Collections.emptyList()).forEach(service -> {
            if (autoUpdateTask) {
                taskRegistrar.addFixedDelayTask(service.getAutoUpdateTask());
            }
            if (autoScheduleTask) {
                taskRegistrar.addFixedDelayTask(service.getAutoScheduleTask());
            }
        });

        taskRegistrar.afterPropertiesSet();
    }

    @Override
    public String toString() {
        return "ScheduleConfig{" +
                "maxPoolSize=" + maxPoolSize +
                ", scheduleThreadPrefix='" + scheduleThreadPrefix + '\'' +
                ", awaitTerminationSeconds=" + awaitTerminationSeconds +
                ", autoUpdateTask=" + autoUpdateTask +
                ", autoScheduleTask=" + autoScheduleTask +
                ", autoManagers=" + autoManagers +
                '}';
    }

}
