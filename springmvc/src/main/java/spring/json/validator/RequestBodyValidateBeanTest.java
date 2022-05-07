package spring.json.validator;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Controller
public class RequestBodyValidateBeanTest {

    @RequestMapping("/request-body-validate-bean-manual")
    @ResponseBody
    public RequestBodyValidateBean requestBodyValidateBeanManualTest(@RequestBody RequestBodyValidateBean requestBodyValidateBean) {
        //手动通过 bean validation 的规范获取 Validator 对象
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        //使用 Validator 对象进行检验 bean 对象，并输出响应的验证结果。
        Set<ConstraintViolation<RequestBodyValidateBean>> validate = validator.validate(requestBodyValidateBean);
        for (ConstraintViolation<RequestBodyValidateBean> constraintViolation : validate) {
            System.out.println(constraintViolation);
        }

        System.out.println(requestBodyValidateBean);

        return requestBodyValidateBean;
    }

    @RequestMapping("/request-body-validate-bean-auto")
    @ResponseBody
    //spring 自动的通过 bean validation 的规范获取 Validator 对象，并对 bean 对象进行检验
    //这里需要使用 bean validation 的注解 javax.validation.Valid 来触发 spring 的自动校验检测，
    //默认情况下，即使该 bean 本身存在 bean validation 相关的校验注解， spring 也不会自动对该 bean 的注解进行检测校验的。
    public RequestBodyValidateBean requestBodyValidateBeanAutoTest(@Valid @RequestBody RequestBodyValidateBean requestBodyValidateBean) {

        System.out.println(requestBodyValidateBean);

        return requestBodyValidateBean;
    }

}
