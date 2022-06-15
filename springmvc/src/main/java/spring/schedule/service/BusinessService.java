package spring.schedule.service;

import org.springframework.stereotype.Service;
import spring.schedule.entity.BusinessBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BusinessService {

    private final AtomicLong counter = new AtomicLong(0);

    private final List<BusinessBean> businessBeans = new ArrayList<>();

    public List<BusinessBean> getBusinessBeans() {
        BusinessBean businessBean = new BusinessBean();
        businessBean.setGroup("g1");
        businessBean.setName("n1");
        businessBean.setHost("192.168.88.110");
        businessBean.setPort(6379);

        businessBeans.add(businessBean);
        return businessBeans;
    }

    public boolean isValid(BusinessBean businessBean) {
        String group = businessBean.getGroup();
        String name = businessBean.getName();
        String host = businessBean.getHost();
        int port = businessBean.getPort();

        boolean valid = true;
        String format = String.format("[%s]-[%s] connect to [%s]:[%s] is [%s].", group, name, host, port, valid);
        System.out.println(format);
        return valid;
    }

    public void doBusiness(BusinessBean businessBean) {
        long incrementAndGet = counter.incrementAndGet();
        String group = businessBean.getGroup();
        String name = businessBean.getName();
        String host = businessBean.getHost();
        int port = businessBean.getPort();
        String format = String.format("[%s]:[%s]-[%s] connect to [%s]:[%s].", incrementAndGet, group, name, host, port);
        System.out.println(format);
    }

}
