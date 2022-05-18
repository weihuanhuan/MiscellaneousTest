package spring.json.validator.custom.clazz;

import spring.json.validator.RequestBodyValidateChildBean;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestBodyValidateChildBeanValidator implements ConstraintValidator<RequestBodyValidateChildBeanAnnotation, RequestBodyValidateChildBean> {

    @Override
    public void initialize(RequestBodyValidateChildBeanAnnotation constraintAnnotation) {
        //do nothing.
    }

    @Override
    public boolean isValid(RequestBodyValidateChildBean bodyValidateChildBean, ConstraintValidatorContext context) {
        if (bodyValidateChildBean == null) {
            return true;
        }

        Integer fieldChildIntegerBigger = bodyValidateChildBean.getFieldChildIntegerBigger();
        Integer fieldChildIntegerLess = bodyValidateChildBean.getFieldChildIntegerLess();

        //Compare the size relationship between two field.
        if (fieldChildIntegerBigger > fieldChildIntegerLess) {
            return true;
        }
        return false;
    }

}
