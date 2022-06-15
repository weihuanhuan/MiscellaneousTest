package spring.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@ComponentScan("spring.schedule.service")
public class ScheduleConfig implements SchedulingConfigurer {

    private static final int MAX_POOL_SIZE = 32;

    private static final String SCHEDULE_THREAD_PREFIX = "task-scheduler-";

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        int poolSize = Runtime.getRuntime().availableProcessors() * 4;
        scheduler.setPoolSize(Math.min(poolSize, MAX_POOL_SIZE));
        scheduler.setThreadNamePrefix(SCHEDULE_THREAD_PREFIX);
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());

        taskRegistrar.afterPropertiesSet();
    }

}
