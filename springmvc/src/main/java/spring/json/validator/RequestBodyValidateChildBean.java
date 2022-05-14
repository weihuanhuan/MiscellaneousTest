package spring.json.validator;

import spring.json.validator.group.CreateGroup;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RequestBodyValidateChildBean {

    @NotNull(groups = {CreateGroup.class})
    private String fieldChildString;

    @Max(10)
    @Min(1)
    private Integer fieldChildInteger;

    public String getFieldChildString() {
        return fieldChildString;
    }

    public void setFieldChildString(String fieldChildString) {
        this.fieldChildString = fieldChildString;
    }

    public Integer getFieldChildInteger() {
        return fieldChildInteger;
    }

    public void setFieldChildInteger(Integer fieldChildInteger) {
        this.fieldChildInteger = fieldChildInteger;
    }

    @Override
    public String toString() {
        return "RequestBodyValidateChildBean{" +
                "fieldChildString='" + fieldChildString + '\'' +
                ", fieldChildInteger=" + fieldChildInteger +
                '}';
    }

}
