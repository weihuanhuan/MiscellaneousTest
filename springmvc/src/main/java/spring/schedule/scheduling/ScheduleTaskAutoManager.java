package spring.schedule.scheduling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.FixedDelayTask;

abstract public class ScheduleTaskAutoManager extends ScheduleTaskManager {

    @Value("${schedule.default.initial.update.delay:30000}")
    protected long initialUpdateDelay;
    @Value("${schedule.default.fixed.update.delay:30000}")
    protected long fixedUpdateDelay;

    @Value("${schedule.default.initial.schedule.delay:30000}")
    protected long initialScheduleDelay;
    @Value("${schedule.default.fixed.schedule.delay:30000}")
    protected long fixedScheduleDelay;

    public FixedDelayTask getAutoUpdateTask() {
        return createFixedDelayTask(this::updateTask, initialUpdateDelay, fixedUpdateDelay);
    }

    public FixedDelayTask getAutoScheduleTask() {
        return createFixedDelayTask(this::scheduleTask, initialScheduleDelay, fixedScheduleDelay);
    }

    private FixedDelayTask createFixedDelayTask(Runnable runnable, long initialDelay, long fixedDelay) {
        return new FixedDelayTask(runnable, initialDelay, fixedDelay);
    }

    public long getInitialUpdateDelay() {
        return initialUpdateDelay;
    }

    public void setInitialUpdateDelay(long initialUpdateDelay) {
        this.initialUpdateDelay = initialUpdateDelay;
    }

    public long getFixedUpdateDelay() {
        return fixedUpdateDelay;
    }

    public void setFixedUpdateDelay(long fixedUpdateDelay) {
        this.fixedUpdateDelay = fixedUpdateDelay;
    }

    public long getInitialScheduleDelay() {
        return initialScheduleDelay;
    }

    public void setInitialScheduleDelay(long initialScheduleDelay) {
        this.initialScheduleDelay = initialScheduleDelay;
    }

    public long getFixedScheduleDelay() {
        return fixedScheduleDelay;
    }

    public void setFixedScheduleDelay(long fixedScheduleDelay) {
        this.fixedScheduleDelay = fixedScheduleDelay;
    }

    @Override
    public String toString() {
        return "ScheduleTaskAutoManager{"
                + "initialUpdateDelay=" + initialUpdateDelay
                + ", fixedUpdateDelay=" + fixedUpdateDelay
                + ", initialScheduleDelay=" + initialScheduleDelay
                + ", fixedScheduleDelay=" + fixedScheduleDelay + '}';
    }

}
