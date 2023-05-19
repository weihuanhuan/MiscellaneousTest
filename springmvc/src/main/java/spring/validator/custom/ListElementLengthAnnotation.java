package spring.validator.custom;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//parameter-level bean validator annotation
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListElementLengthValidator.class)
@Documented
public @interface ListElementLengthAnnotation {

    int max() default 100;

    int min() default 1;

    String message() default "{spring.json.validator.custom.ListElementLengthAnnotation.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
