package spring.json.validator;

import spring.validator.RequestBodyValidateBean;
import spring.validator.RequestBodyValidateChildBean;

import java.util.ArrayList;
import java.util.List;

public final class WebTestBean {

    private WebTestBean() {
    }

    public static RequestBodyValidateBean createRequestBean() {
        RequestBodyValidateBean bean = new RequestBodyValidateBean();
        bean.setFieldString("StringValue");
        bean.setFieldInteger(10);
        bean.setIpAddress("IpAddressValue");

        RequestBodyValidateChildBean childBean = new RequestBodyValidateChildBean();
        childBean.setFieldChildString("ChileStringValue");
        childBean.setFieldChildIntegerBigger(80);
        childBean.setFieldChildIntegerLess(100);
        bean.setFieldChildObject(childBean);

        List<RequestBodyValidateChildBean> fieldChildObjectList = new ArrayList<>();

        RequestBodyValidateChildBean childBeanList1 = new RequestBodyValidateChildBean();
        childBeanList1.setFieldChildString("ChileStringValue1");
        childBeanList1.setFieldChildIntegerBigger(81);
        childBeanList1.setFieldChildIntegerLess(101);
        fieldChildObjectList.add(childBeanList1);

        RequestBodyValidateChildBean childBeanList2 = new RequestBodyValidateChildBean();
        childBeanList2.setFieldChildString("ChileStringValue2");
        childBeanList2.setFieldChildIntegerBigger(82);
        childBeanList2.setFieldChildIntegerLess(102);
        fieldChildObjectList.add(childBeanList2);

        bean.setFieldChildObjectList(fieldChildObjectList);
        return bean;
    }

}