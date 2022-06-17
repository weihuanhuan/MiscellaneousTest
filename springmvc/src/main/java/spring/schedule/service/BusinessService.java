package spring.schedule.service;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BusinessService {

    private final AtomicLong counter = new AtomicLong(0);

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

    public boolean isValid(BusinessBean businessBean) {
        boolean valid = true;
        long get = counter.get();
        if (get % 100 == 0) {
            String format = String.format("[%s] is [%s].", businessBean, valid);
            System.out.println(format);
        }
        return valid;
    }

    public void doBusiness(BusinessBean businessBean) {
        long get = counter.get();
        if (get % 100 != 0) {
            return;
        }

        String host = businessBean.getHost();
        int port = businessBean.getPort();
        String format = String.format("[%s]:[%s] connect to [%s]:[%s].", get, businessBean, host, port);
        System.out.println(format);
    }

}
