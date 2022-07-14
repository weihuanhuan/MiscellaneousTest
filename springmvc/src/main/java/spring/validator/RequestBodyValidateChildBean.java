package spring.validator;

import spring.validator.custom.clazz.RequestBodyValidateChildBeanAnnotation;
import spring.validator.group.CreateGroup;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RequestBodyValidateChildBeanAnnotation(groups = {CreateGroup.class})
public class RequestBodyValidateChildBean {

    @NotNull(groups = {CreateGroup.class})
    private String fieldChildString;

    //当验证时如果指定了 groups 信息，那么这些没有 group 标记的域将不会执行验证了
    @Max(10)
    @Min(1)
    private Integer fieldChildIntegerBigger;

    @Max(value = 10, groups = {CreateGroup.class})
    @Min(value = 1, groups = {CreateGroup.class})
    private Integer fieldChildIntegerLess;

    public String getFieldChildString() {
        return fieldChildString;
    }

    public void setFieldChildString(String fieldChildString) {
        this.fieldChildString = fieldChildString;
    }

    public Integer getFieldChildIntegerBigger() {
        return fieldChildIntegerBigger;
    }

    public void setFieldChildIntegerBigger(Integer fieldChildIntegerBigger) {
        this.fieldChildIntegerBigger = fieldChildIntegerBigger;
    }


    public Integer getFieldChildIntegerLess() {
        return fieldChildIntegerLess;
    }

    public void setFieldChildIntegerLess(Integer fieldChildIntegerLess) {
        this.fieldChildIntegerLess = fieldChildIntegerLess;
    }

    @Override
    public String toString() {
        return "RequestBodyValidateChildBean{" +
                "fieldChildString='" + fieldChildString + '\'' +
                ", fieldChildIntegerBigger=" + fieldChildIntegerBigger +
                ", fieldChildIntegerLess=" + fieldChildIntegerLess +
                '}';
    }

}
