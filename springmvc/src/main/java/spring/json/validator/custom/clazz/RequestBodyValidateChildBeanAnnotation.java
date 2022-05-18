package spring.json.validator.custom.clazz;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//class-level bean validator annotation
@Target(ElementType.TYPE)
@Constraint(validatedBy = RequestBodyValidateChildBeanValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBodyValidateChildBeanAnnotation {

    String message() default "{spring.json.validator.custom.clazz.RequestBodyValidateChildBeanAnnotation.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
