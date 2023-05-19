package spring.validator;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import spring.validator.custom.ListElementLengthAnnotation;

import javax.validation.constraints.Pattern;
import java.util.List;

//使用 hibernate validation 验证 spring 容器所管理组件 bean 的方法参数
//如果存在该注解，调用 controller 方法时，间接调用
//    首先使用 org.springframework.validation.beanvalidation.MethodValidationInterceptor.invoke 来处理方法参数的校验
//    内部使用 bean validation 规范的接口 javax.validation.executable.ExecutableValidator.validateParameters 来对执行的方法进行验证
//    然后使用 org.aopalliance.intercept.Joinpoint.proceed 来调用真实的 controller 方法
//如果不存该注解，调用 controller 方法时，直接调用
//    直接使用 真实的 controller 方法，此时由于没有添加 MethodValidationInterceptor 代理对象，所以可以直接调用
//决定是否使用 MethodValidationInterceptor ，由下面步骤来处理
// 1. 使用 org.springframework.validation.beanvalidation.MethodValidationPostProcessor.afterPropertiesSet 来创建切点
// 2. 使用 org.springframework.aop.support.annotation.AnnotationMatchingPointcut 来向被 @Validated 注解的类中插入切点
@Validated
@RestController
public class RequestParameterValidateController {

    @RequestMapping("/request-param-validate/{name}")
    @ResponseBody
    public String requestParameterValidateTest(@PathVariable("name") @Pattern(regexp = "[a-zA-Z-]+") String name,
                                               @RequestParam("desc") @Length(min = 5, max = 20) String description) {
        String format = String.format("name [%s] with desc [%s].", name, description);
        System.out.println(format);
        return format;
    }

    @RequestMapping("/request-body-validate/{name}")
    @ResponseBody
    public String requestBodyValidateTest(@PathVariable("name") @Pattern(regexp = "[a-zA-Z-]+") String name,
                                          // 当所需要验证的目标内部无法添加 bean validation 注解时，比如这里的 List<String> 是 jdk 的类，无法修改
                                          // 此时就需要在 controller 的 class-level 中添加 @Validated 才行，而在该方法参数上添加 @Validated 无效，
                                          @Validated //无效示例注解，如果该参数是我们自定义的类型，就可以向其中添加 bean validation 注解，就能生效了。
                                          // 这里验证由 org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.resolveArgument 处理
                                          // 其校验调用要早于 MethodValidationInterceptor ，其是在 spring 解析所要执行的 controller 方法参数时就会执行了
                                          // spring 会在 AbstractMessageConverterMethodArgumentResolver.validateIfApplicable 中检测一个方法参数的对象是否存在 @Validated 注解
                                          // 如果存在，就调用 org.springframework.validation.DataBinder.validate(java.lang.Object...) 执行校验
                                          // 内部调用的验证方法是 bean validation 规范的接口 javax.validation.Validator.validate ，其所验证的是一个 java 对象
                                          // 即该方法中被 @Validated 的注解所标记的参数对象 List<String> descriptions ，其是 jdk 的类，没有任何校验注解
                                          // 另外我们注意到此时的 @ListElementLengthAnnotation 并不是注解到该对象上的，而是该方法参数的，所以对对象本身校验时他是不会生效的
                                          @RequestBody @ListElementLengthAnnotation(min = 5, max = 20) List<String> descriptions) {
        String format = String.format("name [%s] with descriptions [%s].", name, descriptions);
        System.out.println(format);
        return format;
    }

}
