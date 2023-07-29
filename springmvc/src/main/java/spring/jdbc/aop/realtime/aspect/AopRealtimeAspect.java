package spring.jdbc.aop.realtime.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import spring.jdbc.aop.realtime.updated.UpdatedConfigPusher;
import spring.jdbc.aop.realtime.updated.UpdatedConfigRecorder;
import spring.jdbc.mybatis.bean.MybatisRedis;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Aspect
class AopRealtimeAspect extends TransactionSynchronizationAdapter {

    private static final Logger LOGGER = Logger.getLogger(AopRealtimeAspect.class.getName());

    private static final ThreadLocal<UpdatedConfigRecorder> RECORDER_THREAD_LOCAL = ThreadLocal.withInitial(UpdatedConfigRecorder::new);

    private static final UpdatedConfigPusher PUSHER = new UpdatedConfigPusher();

    @Before("execution(int spring.jdbc.mybatis.service.impl.RedisServiceImpl.updateRedisByName(..)) && args(updateMybatisRedis)")
    private void updateRedisByName(MybatisRedis updateMybatisRedis) {
        System.out.println(this.getClass().getSimpleName() + ": updateMybatisRedis=" + updateMybatisRedis);

        if (updateMybatisRedis == null) {
            return;
        }

        try {
            if (!isSynchronizationActive()) {
                return;
            }

            registerSynchronizationIfNeed();
        } catch (Throwable throwable) {
            String name = updateMybatisRedis.getName();
            String format = String.format("failed to register [%s] for [%s] named [%s]!",
                    AopRealtimeAspect.class.getSimpleName(), updateMybatisRedis.getClass().getSimpleName(), name);
            LOGGER.log(Level.WARNING, format, throwable);
            return;
        }

        try {
            UpdatedConfigRecorder updatedConfigRecorder = RECORDER_THREAD_LOCAL.get();
            updatedConfigRecorder.recordUpdatedConfig(updateMybatisRedis);
        } catch (Throwable throwable) {
            String name = updateMybatisRedis.getName();
            String format = String.format("failed to record updated config for [%s] named [%s]!", updateMybatisRedis.getClass()
                    .getSimpleName(), name);
            LOGGER.log(Level.WARNING, format, throwable);
        }
    }

    @Override
    public void afterCommit() {
        System.out.println(this.getClass().getSimpleName() + ": afterCommit");
        UpdatedConfigRecorder updatedConfigRecorder = RECORDER_THREAD_LOCAL.get();
        PUSHER.pushUpdatedConfig(updatedConfigRecorder);
    }

    @Override
    public void afterCompletion(int status) {
        System.out.println(this.getClass().getSimpleName() + ": afterCompletion, status=" + status);
        RECORDER_THREAD_LOCAL.remove();
    }

    private boolean isSynchronizationActive() {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        return synchronizationActive;
    }

    private void registerSynchronizationIfNeed() {
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        if (!synchronizations.contains(this)) {
            TransactionSynchronizationManager.registerSynchronization(this);
            System.out.println(this.getClass().getSimpleName() + ": registerSynchronization=" + this);
        }
    }

}