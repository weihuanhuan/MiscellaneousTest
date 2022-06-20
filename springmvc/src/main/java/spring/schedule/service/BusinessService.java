package spring.schedule.service;

import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.io.IOException;
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

        if (i == 1) {
            String format = String.format("IOException do business for [%s]-[%s]!", group, name);
            throw new IOException(format);
        }

        if (i == 2) {
            String format = String.format("RuntimeException do business for [%s]-[%s]!", group, name);
            throw new RuntimeException(format);
        }
    }

}
