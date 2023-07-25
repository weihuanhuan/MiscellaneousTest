package spring.jdbc.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import spring.jdbc.mybatis.bean.MybatisRedis;

@Component
@Aspect
class AopUpdateAspect extends TransactionSynchronizationAdapter {

    @Pointcut("execution(int spring.jdbc.mybatis.service.impl.RedisServiceImpl.updateRedisByName(..)) && args(updateMybatisRedis)")
    private void updateRedisByNamePointcut(MybatisRedis updateMybatisRedis) {
        // 第一, aspectj 运行时，其切点方法中可以提供一个参数，及参数名来处理对于 joint point 的参数处理，
        // 如果我们只在切点方法中提供了参数, 而没有在 @Pointcut 表达式中提供同名的该参数，那么会提示如下异常
        //Caused by: java.lang.IllegalArgumentException: error at ::0 formal unbound in pointcut
        // 第二, 同时也会依据 @Pointcut 表达式 args(updateMybatisRedis) 中的参数名，来匹配切点方法中参数的参数名
        // 如果他们的名字不匹配就会产生下面的异常，以提示和 args(updateMybatisRedis) 中的参数名没有匹配的参数名
        //Caused by: java.lang.IllegalArgumentException: warning no match for this type name: updateMybatisRedis [Xlint:invalidAbsoluteTypeName]
    }

    // 第三, 由于切点方法中提供了 MybatisRedis updateMybatisRedis 参数，所以 @Before advice 中也需要传递同名的该参数，否则会提示如下的异常
    //Caused by: java.lang.IllegalArgumentException: error at ::0 incompatible number of arguments to pointcut, expected 1 found 0
    // 第四, 进而也使得 @Before advice 中也需要传递同名的该参数，否则会提示如下的异常
    //Caused by: java.lang.IllegalArgumentException: warning no match for this type name: updateMybatisRedis [Xlint:invalidAbsoluteTypeName]
    @Before("spring.jdbc.aop.aspect.AopUpdateAspect.updateRedisByNamePointcut(updateMybatisRedis)")
    public void registerSynchronization(MybatisRedis updateMybatisRedis) {
        System.out.println(this.getClass().getSimpleName() + ": registerSynchronization");

        System.out.println("updateMybatisRedis=" + updateMybatisRedis);

        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (synchronizationActive) {
            TransactionSynchronizationManager.registerSynchronization(this);
        }
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