package spring.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by JasonFitch on 9/20/2019.
 */
@Controller
public class AjaxJsonTest {

    //这是 spring 内置的响应和请求消息转换器的类层次，如果我们要处理 json 的请求和响应报文，需要引入对应的实现才行。
    //AbstractHttpMessageConverter (org.springframework.http.converter)
    //    AbstractGenericHttpMessageConverter (org.springframework.http.converter)
    //        AbstractJsonHttpMessageConverter (org.springframework.http.converter.json)
    //            GsonHttpMessageConverter (org.springframework.http.converter.json)

    @RequestMapping("/ajaxjson")
    @ResponseBody
    public Map<String, String> ajaxJsonTest(@RequestBody String requestJson) {
        //使用 @RequestBody 接收 request 的 json string,
        //并按照自己的需求转为 java object .这里我们使用了 gson 来完成 json string 的解析.
        Gson gson = new Gson();
        TypeToken<?> mapTypeToken = TypeToken.getParameterized(HashMap.class, String.class, String.class);
        Map<String, String> requestMap = gson.fromJson(requestJson, mapTypeToken.getType());

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("springKey1", "springValue1");
        resultMap.put("springKey2", "springValue2");
        resultMap.put("springKeyEmpty", "");
        resultMap.put("springKeyNull", null);
        resultMap.putAll(requestMap);

        //使用 @ResponseBody 利用 GsonHttpMessageConverter 的实现，
        //以让 spring 自动的将 java map object 转换为 json string 的形式作为 response .
        return resultMap;
    }

}
