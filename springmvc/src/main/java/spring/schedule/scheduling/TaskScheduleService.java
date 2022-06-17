package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TaskScheduleService {

    @Autowired
    private TaskScheduler taskScheduler;

    private final Map<ScheduleTask, ScheduledFuture<?>> taskScheduledFutureMap = new ConcurrentHashMap<>();

    public ScheduledFuture<?> schedule(ScheduleTask scheduleTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(scheduleTask, 1000);
        return taskScheduledFutureMap.put(scheduleTask, scheduledFuture);
    }

    public boolean cancel(ScheduleTask scheduleTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduledFutureMap.get(scheduleTask);
        if (scheduledFuture == null) {
            return false;
        }
        return scheduledFuture.cancel(true);
    }

}
