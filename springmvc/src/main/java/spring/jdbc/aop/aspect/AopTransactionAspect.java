package spring.jdbc.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;

@Component
@Aspect
class AopTransactionAspect extends TransactionSynchronizationAdapter {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void registerSynchronization(JoinPoint joinPoint) {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        System.out.println(this.getClass().getSimpleName() + ": synchronizationActive=" + synchronizationActive);

        // 判断当前线程中是否支持注册 org.springframework.transaction.support.TransactionSynchronization
        if (synchronizationActive) {
            TransactionSynchronizationManager.registerSynchronization(this);
        }

        //我们也可以通过向 @Before advice 的方法中添加 org.aspectj.lang.JoinPoint 参数对象，并从中获取被 aspect 的方法参数
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        System.out.println(this.getClass().getSimpleName() + ": joinPoint=" + joinPoint);
        System.out.println(this.getClass().getSimpleName() + ": target=" + target);
        System.out.println(this.getClass().getSimpleName() + ": args=" + Arrays.deepToString(args));
    }

    @Override
    public void afterCommit() {
        System.out.println(this.getClass().getSimpleName() + ": afterCommit");
    }

    @Override
    public void afterCompletion(int status) {
        System.out.println(this.getClass().getSimpleName() + ": afterCompletion, status=" + status);
    }

}