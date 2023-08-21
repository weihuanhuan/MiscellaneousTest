package spring.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.ErrorHandler;
import spring.schedule.scheduling.ScheduleTaskAutoManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

        //TODO 这里存在初始化顺序的问题，如果 ScheduleTaskAutoManager.getAutoUpdateTask 内部使用的 db service 还没有初始化完成，
        // 那么这里 autoUpdateTask 所触发的自动调用就可能在 db service 可用前被调用，使得业务出错。
        // 不过我们这里的测试场景由于存在 initialUpdateDelay 的延迟调用，所以测试时，是不会出现这个情况的。
        // 理论上，我们应该将这些方法移动到 scheduler 初始化之外，并使用 spring context 的 @EventListener 机制，保证在 ioc 容器完全初始化后在调用这些任务
        // 对于传统的 web 环境我们可以使用 ContextLoaderListener.contextInitialized 来 AbstractApplicationContext.publishEvent(java.lang.Object) 事件
        Optional.ofNullable(autoManagers).orElse(Collections.emptyList()).forEach(service -> {
            if (autoUpdateTask) {
                taskRegistrar.addFixedDelayTask(service.getAutoUpdateTask());
            }
            if (autoScheduleTask) {
                taskRegistrar.addFixedDelayTask(service.getAutoScheduleTask());
            }
        });

        taskRegistrar.afterPropertiesSet();

        //config for underlying ScheduledThreadPoolExecutor with after shutdown policy.
        TaskScheduler scheduler = taskRegistrar.getScheduler();
        if (scheduler instanceof ThreadPoolTaskScheduler) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = ((ThreadPoolTaskScheduler) scheduler).getScheduledThreadPoolExecutor();
            scheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        } else {
            //should never happen.
            throw new IllegalStateException("org.springframework.scheduling.config.ScheduledTaskRegistrar.getScheduler cannot be null!");
        }
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
