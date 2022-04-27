package spring.json.requestbody;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RequestBodyObjectBeanTest {

    @RequestMapping("/request-body-object-bean")
    @ResponseBody
    public RequestBodyObjectBean requestBodyObjectBeanTest(@RequestBody RequestBodyObjectBean requestBodyObjectBean) {

        System.out.println(requestBodyObjectBean);

        //以让 spring 自动的将 java object 转换为 json string 的形式作为 response .
        return requestBodyObjectBean;
    }

}
