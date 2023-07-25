package spring.jdbc.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Aspect
class AopTransactionAspect extends TransactionSynchronizationAdapter {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void registerSynchronization() {
        System.out.println("registerSynchronization");
        TransactionSynchronizationManager.registerSynchronization(this);
    }

    @Override
    public void afterCommit() {
        System.out.println("afterCommit");
    }

    @Override
    public void afterCompletion(int status) {
        System.out.println("afterCompletion, status=" + status);
    }

}