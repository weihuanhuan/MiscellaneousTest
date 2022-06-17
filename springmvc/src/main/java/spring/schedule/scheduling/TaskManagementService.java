package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.FixedDelayTask;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class TaskManagementService {

    @Autowired
    private TaskScheduleService taskScheduleService;

    private final Set<ScheduleTask> workScheduleTasks = new HashSet<>();
    private final Set<ScheduleTask> suspendScheduleTasks = new HashSet<>();

    @Value("${schedule.default.initial.update.delay:15000}")
    protected long initialUpdateDelay;
    @Value("${schedule.default.fixed.update.delay:15000}")
    protected long fixedUpdateDelay;

    @Value("${schedule.default.initial.schedule.delay:15000}")
    protected long initialScheduleDelay;
    @Value("${schedule.default.fixed.schedule.delay:15000}")
    protected long fixedScheduleDelay;

    protected void updateTask() {
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

    protected void scheduleTask() {
        synchronized (this) {
            workScheduleTasks.forEach(taskScheduleService::schedule);
            suspendScheduleTasks.forEach(taskScheduleService::cancel);
        }
    }

    abstract protected List<ScheduleTask> findScheduleTasks();

    public FixedDelayTask createUpdateTask() {
        return new FixedDelayTask(this::updateTask, initialUpdateDelay, fixedUpdateDelay);
    }

    public FixedDelayTask createScheduleTask() {
        return new FixedDelayTask(this::scheduleTask, initialScheduleDelay, fixedScheduleDelay);
    }

    public boolean suspendTask(ScheduleTask scheduleTask) {
        synchronized (this) {
            workScheduleTasks.remove(scheduleTask);
            return suspendScheduleTasks.add(scheduleTask);
        }
    }

    public boolean addTask(ScheduleTask scheduleTask) {
        synchronized (this) {
            workScheduleTasks.add(scheduleTask);
            return suspendScheduleTasks.remove(scheduleTask);
        }
    }

    public boolean cancelTask(ScheduleTask scheduleTask) {
        return taskScheduleService.cancel(scheduleTask);
    }

}
