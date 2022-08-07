package spring.schedule.service;

import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BusinessService {

    private final AtomicLong counter = new AtomicLong(0);

    private final Random random = new Random(0);

    private final Map<String, BusinessBean> businessBeans = new ConcurrentHashMap<>();

    public BusinessBean addBusinessBean() {
        long incrementAndGet = counter.incrementAndGet();
        String name = "n" + incrementAndGet;

        BusinessBean businessBean = new BusinessBean();
        businessBean.setGroup("g1");
        businessBean.setName(name);
        businessBean.setHost("192.168.88.110");
        businessBean.setPort(6379);
        return businessBeans.put(name, businessBean);
    }

    public BusinessBean removeBusinessBean(String name) {
        return businessBeans.remove(name);
    }

    public BusinessBean updateBusinessBean(String name) {
        BusinessBean businessBean = businessBeans.get(name);
        businessBean.setHost("192.168.88.110");
        businessBean.setPort(63791);
        return businessBeans.put(name, businessBean);
    }

    public List<BusinessBean> getBusinessBeans() {
        return new ArrayList<>(businessBeans.values());
    }

    public boolean isValid(BusinessBean businessBean) throws Exception {
        int i = random.nextInt(4);
        if (i == 1) {
            return false;
        }

        String group = businessBean.getGroup();
        String name = businessBean.getName();

        if (i == 2) {
            String format = String.format("IOException judge valid for [%s]-[%s]!", group, name);
            throw new IOException(format);
        }

        if (i == 3) {
            String format = String.format("RuntimeException judge valid for [%s]-[%s]!", group, name);
            throw new RuntimeException(format);
        }

        return true;
    }

    public void doBusiness(BusinessBean businessBean) throws Exception {
        int i = random.nextInt(3);

        String group = businessBean.getGroup();
        String name = businessBean.getName();

        //模拟业务的执行时间,注意和调度周期的关系
        //注意这里可能会发生 java.lang.InterruptedException，存在俩种情况
        // 1. 正常情况，spring scheduler shutdown 时会中断所有的 executor 线程
        // 2. 异常情况，当 BusinessService.isValid 验证任务不应该执行时，会将任务添加到 ScheduleTaskManager.suspendScheduleTasks 队列中，
        //    而后者会周期性的对挂起的任务执行 java.util.concurrent.Future.cancel, 在 cancel 的过程中 jdk 会对线程执行中断
        //    而此时该任务可能还在这里 sleep 中，一旦他的线程被 Future.cancel，就使得这里出现 InterruptedException 异常。
        //    发生这种情况的主要原因是我们进行 suspendScheduleTasks 处理时，没有考虑到正在执行的任务，就直接对其线程中断导致的，
        //    但是这种情况的发生概率比较小的，只有任务执行很耗时，且其执行时正好自身被检测为 isValid 状态且正好被 manager 执行了 suspend 操作时才会发生
        //    我们可以通过将 isValid 永远不会返回 false ，来保证任务执行时永远不会执行 suspend 来验证这种情况的 InterruptedException 就不会出现了
        //TODO 如何优化意外的将正在执行的任务 InterruptedException 的情况
        TimeUnit.MILLISECONDS.sleep(800);

        if (i == 1) {
            String format = String.format("IOException do business for [%s]-[%s]!", group, name);
            throw new IOException(format);
        }

        if (i == 2) {
            String format = String.format("RuntimeException do business for [%s]-[%s]!", group, name);
            throw new RuntimeException(format);
        }
    }

    public Map<String, BusinessBean> getBusinessBeansMap() {
        return businessBeans;
    }

}
