package spring.json.validator;

import spring.json.validator.custom.IpAddress;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RequestBodyValidateBean {

    @NotNull
    private String fieldString;

    //这里的 @Size 不是指验证 Integer 数据的大小范围，而是用来验证集合类数据的元素数量是否符合 size 的约定的
    //The annotated element size must be between the specified boundaries (included).
    //@Size(min = 10, max = 100)
    //@Max 和 @Min 才是验证数值大小的
    @Max(100)
    @Min(10)
    private Integer fieldInteger;

    @IpAddress
    private String ipAddress;

    //为了验证 child bean 中所定义的 bean validation 注解，我们需要在非基本类型属性上面增加 @Valid 来进行级联的校验
    //Marks a property, method parameter or method return type for validation cascading.
    @Valid
    @NotNull
    private RequestBodyValidateChildBean fieldChildObject;

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

    @Override
    public String toString() {
        return "RequestBodyValidateBean{" +
                "fieldString='" + fieldString + '\'' +
                ", fieldInteger=" + fieldInteger +
                ", ipAddress='" + ipAddress + '\'' +
                ", fieldChildObject=" + fieldChildObject +
                '}';
    }

}
