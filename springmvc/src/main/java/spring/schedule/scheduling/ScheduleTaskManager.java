package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract public class ScheduleTaskManager {

    @Autowired
    protected TaskScheduleService taskScheduleService;

    //avoid ConcurrentModificationException
    protected final Set<ScheduleTask> workScheduleTasks = new CopyOnWriteArraySet<>();

    protected final Set<ScheduleTask> newScheduleTasks = new CopyOnWriteArraySet<>();
    protected final Set<ScheduleTask> suspendScheduleTasks = new CopyOnWriteArraySet<>();

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
                //record to be schedule
                newScheduleTasks.addAll(notInWorkTasks);
                //reduce to be suspend
                suspendScheduleTasks.removeAll(notInWorkTasks);
            }

            if (!notInNewTasks.isEmpty()) {
                //record to be suspend
                suspendScheduleTasks.addAll(notInNewTasks);
                //reduce to be schedule
                newScheduleTasks.removeAll(notInNewTasks);
            }

            //to be schedule or suspend should has not same task
            boolean anyMatch = notInNewTasks.stream().anyMatch(notInWorkTasks::contains);
            if (anyMatch) {
                System.err.println("notInNewTasks.stream().anyMatch(notInWorkTasks::contains) should never match!");
                System.exit(-1);
            }
        }
    }

    synchronized public void scheduleTask() {
        if (newScheduleTasks.isEmpty() && suspendScheduleTasks.isEmpty()) {
            return;
        }

        newScheduleTasks.forEach(task -> this.addScheduleTask(task, true));
        suspendScheduleTasks.forEach(task -> this.addSuspendTask(task, true));

        //after schedule task any queue will be empty.
        long sum = Stream.of(newScheduleTasks, suspendScheduleTasks).mapToLong(Collection::size).sum();
        if (sum > 0) {
            System.err.println("Stream.of(newScheduleTasks, suspendScheduleTasks).mapToLong(Collection::size).sum() should be zero!");
            System.exit(-2);
        }
    }

    synchronized public boolean addScheduleTask(ScheduleTask scheduleTask, boolean immediateSchedule) {
        boolean result = false;
        if (immediateSchedule) {
            boolean schedule = taskScheduleService.schedule(scheduleTask);
            if (schedule) {
                //add work queue
                result = workScheduleTasks.add(scheduleTask);
                //reduce schedule queue of next round
                newScheduleTasks.remove(scheduleTask);
            }
        } else {
            result = newScheduleTasks.add(scheduleTask);
        }

        //reduce suspend queue of next round
        if (result) {
            suspendScheduleTasks.remove(scheduleTask);
        }

        if (!result) {
            System.err.println("addScheduleTask should always be true!");
            //可能该任务在 manual 添加调度时，被 auto schedule 的 newScheduleTasks 队列给预先添加了，此时就会导致 result 为 false 了。
            //System.exit(-3);
        }
        return result;
    }

    synchronized public boolean addSuspendTask(ScheduleTask scheduleTask, boolean immediateSchedule) {
        boolean result = false;
        if (immediateSchedule) {
            boolean cancel = taskScheduleService.cancel(scheduleTask);
            if (cancel) {
                //remove work queue
                result = workScheduleTasks.remove(scheduleTask);
                //reduce suspend queue of next round
                suspendScheduleTasks.remove(scheduleTask);
            }
        } else {
            result = suspendScheduleTasks.add(scheduleTask);
        }

        //reduce schedule queue of next round
        if (result) {
            newScheduleTasks.remove(scheduleTask);
        }

        if (!result) {
            System.err.println("addSuspendTask should always be true!");
            //可能该任务虽然先前被成功的调度了，但当他在等候到执行时，且在其执行的过程中恰巧被 auto schedule 的 suspendScheduleTasks 队列给移除了，
            //此时如果该任务在运行的过程中发生了异常，就会触发 addSuspendTask ，但是由于其先被 auto schedule 移除了，就会导致 result 为 false 了。
            //System.exit(-4);
        }
        return result;
    }

    public Set<ScheduleTask> getWorkScheduleTasks() {
        return workScheduleTasks;
    }

    public Set<ScheduleTask> getNewScheduleTasks() {
        return newScheduleTasks;
    }

    public Set<ScheduleTask> getSuspendScheduleTasks() {
        return suspendScheduleTasks;
    }

}
