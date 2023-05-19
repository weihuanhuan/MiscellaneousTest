package spring.validator.custom;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ListElementLengthValidator implements ConstraintValidator<ListElementLengthAnnotation, List<String>> {

    private int max;
    private int min;

    @Override
    public void initialize(ListElementLengthAnnotation constraintAnnotation) {
        max = constraintAnnotation.max();
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext context) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        for (String str : list) {
            boolean valid = isValidElement(str);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidElement(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();
        if (min > length || length > max) {
            return false;
        }
        return true;
    }

}