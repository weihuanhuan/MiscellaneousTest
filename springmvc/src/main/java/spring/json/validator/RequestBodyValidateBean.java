package spring.json.validator;

import spring.json.validator.custom.IpAddressAnnotation;
import spring.json.validator.group.CreateGroup;
import spring.json.validator.group.UpdateGroup;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RequestBodyValidateBean {

    //创建时进行校验
    @NotNull(groups = {CreateGroup.class})
    private String fieldString;

    //这里的 @Size 不是指验证 Integer 数据的大小范围，而是用来验证集合类数据的元素数量是否符合 size 的约定的
    //The annotated element size must be between the specified boundaries (included).
    //@Size(min = 10, max = 100)
    //@Max 和 @Min 才是验证数值大小的
    @Max(100)
    @Min(10)
    private Integer fieldInteger;

    //创建和更新时均校验
    @IpAddressAnnotation(groups = {CreateGroup.class, UpdateGroup.class})
    private String ipAddress;

    //为了验证 child bean 中所定义的 bean validation 注解，我们需要在非基本类型属性上面增加 @Valid 来进行级联的校验
    //Marks a property, method parameter or method return type for validation cascading.
    @Valid
    @NotNull
    private RequestBodyValidateChildBean fieldChildObject;

    //测试验证 list 中的 RequestBodyValidateChildBean 对象，而不是 list 本身, list 本身属于 RequestBodyValidateBean 对象
    //对于 list 中的元素的校验，我们也必须指定 @Valid 来执行级联检验，否则即使 list 中元素本身被校验注解标记了也不会执行校验
    @Valid
    private List<RequestBodyValidateChildBean> fieldChildObjectList;

    public String getFieldString() {
        return fieldString;
    }

    public void setFieldString(String fieldString) {
        this.fieldString = fieldString;
    }

    public Integer getFieldInteger() {
        return fieldInteger;
    }

    public void setFieldInteger(Integer fieldInteger) {
        this.fieldInteger = fieldInteger;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public RequestBodyValidateChildBean getFieldChildObject() {
        return fieldChildObject;
    }

    public void setFieldChildObject(RequestBodyValidateChildBean fieldChildObject) {
        this.fieldChildObject = fieldChildObject;
    }

    public List<RequestBodyValidateChildBean> getFieldChildObjectList() {
        return fieldChildObjectList;
    }

    public void setFieldChildObjectList(List<RequestBodyValidateChildBean> fieldChildObjectList) {
        this.fieldChildObjectList = fieldChildObjectList;
    }

    @Override
    public String toString() {
        return "RequestBodyValidateBean{" +
                "fieldString='" + fieldString + '\'' +
                ", fieldInteger=" + fieldInteger +
                ", ipAddress='" + ipAddress + '\'' +
                ", fieldChildObject=" + fieldChildObject +
                ", fieldChildObjectList=" + fieldChildObjectList +
                '}';
    }

}
