package spring.schedule.config;

import org.springframework.util.ErrorHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleErrorHandler implements ErrorHandler {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void handleError(Throwable t) {
        String format = String.format("Unexpected exception [%s]:[%s] occurred in scheduled task", t.getClass(), t.getMessage());
        logger.log(Level.FINE, format);
    }

}
