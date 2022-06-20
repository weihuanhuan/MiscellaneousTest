package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskScheduleService {

    @Autowired
    @Qualifier("scheduleTaskThreadPoolTaskScheduler")
    private TaskScheduler taskScheduler;

    private final Map<ScheduleTask, ScheduledFuture<?>> taskScheduledFutureMap = new ConcurrentHashMap<>();

    public boolean schedule(ScheduleTask scheduleTask) {
        Scheduler scheduler = new Scheduler();
        taskScheduledFutureMap.computeIfAbsent(scheduleTask, scheduler::scheduleAtFixedRate);
        return scheduler.scheduled;
    }

    public boolean cancel(ScheduleTask scheduleTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduledFutureMap.remove(scheduleTask);
        if (scheduledFuture == null) {
            return false;
        }
        return scheduledFuture.cancel(scheduleTask.isMayInterruptIfRunning());
    }

    private class Scheduler {

        private boolean scheduled = false;

        private ScheduledFuture<?> scheduleAtFixedRate(ScheduleTask scheduleTask) {
            long period = scheduleTask.getPeriod();
            ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(scheduleTask, period);
            scheduled = true;
            return scheduledFuture;
        }
    }

}
