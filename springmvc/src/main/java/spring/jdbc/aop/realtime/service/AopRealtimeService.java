package spring.jdbc.aop.realtime.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.jdbc.aop.service.AopTransactionService;

@Service
public class AopRealtimeService extends AopTransactionService {

    @Transactional(rollbackFor = RuntimeException.class)
    public String updateSuccessWithTransactional() {
        // 使用自己的引用来直接调用父类中的实现，可以防止父类 AopTransactionService 实现方法所注解的 @Transactional 生效
        String result = super.updateSuccessWithTransactional();
        // 连续调用俩次，来模拟在子类 AopRealtimeService 实现方法中所注解的 @Transactional 处理的一次事务中，进行了多次的修改的测试。
        result = super.updateSuccessWithTransactional();
        return result;
    }

}
