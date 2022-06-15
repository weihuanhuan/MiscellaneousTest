package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

abstract public class ScheduleTaskService<T> {

    @Autowired
    private TaskScheduler taskScheduler;

    private List<ScheduleTask<T>> scheduleTasks = new ArrayList<>();

    @Scheduled(fixedRate = 5000)
    public void updateScheduleTask() {
        scheduleTasks = findScheduleTask();

        scheduleTasks.forEach(task -> taskScheduler.scheduleAtFixedRate(task, 1000));
    }

    abstract protected List<ScheduleTask<T>> findScheduleTask();

}
