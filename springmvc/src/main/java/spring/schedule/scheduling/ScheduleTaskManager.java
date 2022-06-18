package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class ScheduleTaskManager {

    @Autowired
    protected TaskScheduleService taskScheduleService;

    protected final Set<ScheduleTask> workScheduleTasks = new HashSet<>();
    protected final Set<ScheduleTask> suspendScheduleTasks = new HashSet<>();

    abstract protected List<ScheduleTask> findScheduleTasks();

    public void updateTask() {
        List<ScheduleTask> scheduleTask = findScheduleTasks();

        //null safety for list itself and its element
        List<ScheduleTask> newTasks = Optional.ofNullable(scheduleTask)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        synchronized (this) {
            Set<ScheduleTask> notInWorkTasks = newTasks.stream()
                    .filter(task -> !workScheduleTasks.contains(task))
                    .collect(Collectors.toSet());

            Set<ScheduleTask> notInNewTasks = workScheduleTasks.stream()
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

    public void scheduleTask() {
        synchronized (this) {
            workScheduleTasks.forEach(taskScheduleService::schedule);
            suspendScheduleTasks.forEach(taskScheduleService::cancel);
        }
    }

    public boolean addSuspendTask(ScheduleTask scheduleTask) {
        synchronized (this) {
            workScheduleTasks.remove(scheduleTask);
            return suspendScheduleTasks.add(scheduleTask);
        }
    }

    public boolean addScheduleTask(ScheduleTask scheduleTask) {
        synchronized (this) {
            workScheduleTasks.add(scheduleTask);
            return suspendScheduleTasks.remove(scheduleTask);
        }
    }

}
