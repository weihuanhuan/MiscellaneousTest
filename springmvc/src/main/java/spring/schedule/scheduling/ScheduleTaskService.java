package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class ScheduleTaskService<T> {

    @Autowired
    private TaskScheduler taskScheduler;

    private final Object lock = new Object();
    private final Set<ScheduleTask<T>> workScheduleTasks = new HashSet<>();
    private final Set<ScheduleTask<T>> suspendScheduleTasks = new HashSet<>();

    private final Map<ScheduleTask<T>, ScheduledFuture<?>> taskScheduledFutureMap = new HashMap<>();

    abstract protected List<ScheduleTask<T>> findScheduleTask();

    @Scheduled(initialDelay = 3 * 1000, fixedRate = 5 * 1000)
    public void updateTask() {
        List<ScheduleTask<T>> scheduleTask = findScheduleTask();

        //null safety for list itself and its element
        List<ScheduleTask<T>> newTasks = Optional.ofNullable(scheduleTask)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        synchronized (lock) {
            Set<ScheduleTask<T>> notInWorkTasks = newTasks.stream()
                    .filter(task -> !workScheduleTasks.contains(task))
                    .collect(Collectors.toSet());

            Set<ScheduleTask<T>> notInNewTasks = workScheduleTasks.stream()
                    .filter(task -> !newTasks.contains(task))
                    .collect(Collectors.toSet());

            if (!notInWorkTasks.isEmpty()) {
                workScheduleTasks.addAll(notInWorkTasks);
                suspendScheduleTasks.removeAll(notInWorkTasks);
            }

            if (!notInNewTasks.isEmpty()) {
                workScheduleTasks.removeAll(notInNewTasks);
                suspendScheduleTasks.addAll(notInNewTasks);
            }
        }
    }

    @Scheduled(initialDelay = 3 * 1000, fixedRate = 5 * 1000)
    public void scheduleTask() {
        synchronized (lock) {
            workScheduleTasks.forEach(this::schedule);
            suspendScheduleTasks.forEach(this::cancel);
        }
    }

    private void schedule(ScheduleTask<T> scheduleTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(scheduleTask, 1000);
        taskScheduledFutureMap.put(scheduleTask, scheduledFuture);
    }

    private void cancel(ScheduleTask<T> scheduleTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduledFutureMap.get(scheduleTask);
        Optional.ofNullable(scheduledFuture).ifPresent(future -> future.cancel(true));
    }

}
