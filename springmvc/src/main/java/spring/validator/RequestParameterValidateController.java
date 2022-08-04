package spring.validator;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;

//使用 hibernate validation 验证 spring 容器所管理组件 bean 的方法参数
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

}
