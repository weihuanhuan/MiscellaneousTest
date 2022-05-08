package spring.json.validator;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.json.validator.response.ValidationErrorResponse;
import spring.json.validator.response.Violation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Controller
public class RequestBodyValidateBeanController {

    @RequestMapping("/request-body-validate-bean-manual")
    @ResponseBody
    public RequestBodyValidateBean requestBodyValidateBeanManualTest(@RequestBody RequestBodyValidateBean requestBodyValidateBean) {
        //手动通过 bean validation 的规范获取 Validator 对象
        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();

        //使用 Validator 对象进行检验 bean 对象，并输出响应的验证结果。
        Set<ConstraintViolation<RequestBodyValidateBean>> validate = validator.validate(requestBodyValidateBean);
        for (ConstraintViolation<RequestBodyValidateBean> constraintViolation : validate) {
            System.out.println(constraintViolation);
        }

        //检查是否存在验证问题，并抛出 javax.validation.ConstraintViolationException 来通知调用者处理验证错误
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }

        System.out.println(requestBodyValidateBean);

        return requestBodyValidateBean;
    }

    @RequestMapping("/request-body-validate-bean-auto")
    @ResponseBody
    //spring 自动的通过 bean validation 的规范获取 Validator 对象，并对 bean 对象进行检验
    //这里需要使用 bean validation 的注解 javax.validation.Valid 来触发 spring 的自动校验检测，
    //默认情况下，即使该 bean 本身存在 bean validation 相关的校验注解， spring 也不会自动对该 bean 的注解进行检测校验的。
    public ResponseEntity<RequestBodyValidateBean> requestBodyValidateBeanAutoTest(@Valid @RequestBody RequestBodyValidateBean requestBodyValidateBean) {

        System.out.println(requestBodyValidateBean);

        //使用统一的类型安全的结构化响应
        return ResponseEntity.ok(requestBodyValidateBean);
    }

    @RequestMapping("/request-body-validate-bean-auto-bind-result")
    @ResponseBody
    //spring 在 AbstractMessageConverterMethodArgumentResolver.isBindExceptionRequired 中判断是否在 valid 失败时直接抛出异常
    //比如这里，相比上面的 auto 方法，其存在方法参数 BindingResult ，那么即使前面的 @RequestBody 校验失败了，其也不会直接抛出异常，
    //这给予了我们在特定 接口上面检查错误信息 Errors.getFieldErrors()，并依据相关错误信息进行相应处理的机会。
    public ResponseEntity<?> requestBodyValidateBeanAutoBindResultTest(@Valid @RequestBody RequestBodyValidateBean requestBodyValidateBean, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ValidationErrorResponse error = new ValidationErrorResponse();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                Violation violation = new Violation();
                violation.setFieldName(fieldError.getField());
                violation.setMessage(fieldError.getDefaultMessage());
                error.getViolations().add(violation);
            }

            ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();
            return bodyBuilder.body(error);
        }

        System.out.println(requestBodyValidateBean);

        //使用统一的类型安全的结构化响应
        return ResponseEntity.ok(requestBodyValidateBean);
    }

}
