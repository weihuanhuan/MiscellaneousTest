package spring.schedule.service;

import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
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

    public boolean isValid(BusinessBean businessBean) {
        int i = random.nextInt(3);
        if (i == 0) {
            return true;
        } else if (i == 1) {
            return false;
        } else {
            String group = businessBean.getGroup();
            String name = businessBean.getName();
            String format = String.format("cannot judge valid for [%s]-[%s]!", group, name);
            throw new RuntimeException(format);
        }
    }

    public void doBusiness(BusinessBean businessBean) {
        int i = random.nextInt(3);
        if (i == 2) {
            String group = businessBean.getGroup();
            String name = businessBean.getName();
            String format = String.format("cannot do business for [%s]-[%s]!", group, name);
            throw new RuntimeException(format);
        }
    }

}
