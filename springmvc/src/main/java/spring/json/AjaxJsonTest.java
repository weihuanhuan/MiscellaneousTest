package spring.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

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

    @RequestMapping(value = "/ajaxjson", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
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


    /**
     * 按照下面连接的说法，如果我们的 request body 是一个单纯的 json 字符串，那么需要使用 Object 类型才能正确的让 @RequestBody 解析为 java.lang.String。
     * 比如请求数据是 【"json"】 时，注意合法的 json string value 是必须要包含在双引号内的，而 【{}】 这是用于表示 json object ，对于独立的 json string 是不需要的
     * 那么，当 @RequestBody 的接收参数
     * 是 java.lang.String 时，接收到的参数是 java.lang.String 类型，值为 【"json"】，保留了请求数据的双引号，由于请求发来的 json 没有解析，直接使用原值导致的
     * 是 java.lang.Object 时，接收到的参数是 java.lang.String 类型，值为 【json】，移除了请求数据的双引号，由于真实解析了请求发来的 json 数据导致的。
     * https://stackoverflow.com/questions/64990357/parsing-a-request-body-containing-a-quoted-string-as-json-in-spring-boot-2
     * <p>
     * 随后简单的 debug 发现，造成这个现象的主要原因是 spring 内部解析请求数据为运行时参数时的 AbstractHttpMessageConverter 存在如下的尝试顺序
     * 我们可以看见 StringHttpMessageConverter 出现在了 MappingJackson2HttpMessageConverter 的前面，
     * 所以当  @RequestBody 的接收参数是 java.lang.String 时，就会优先被 StringHttpMessageConverter 处理了，因此没有了对请求 json 数据的解析，而是直接使用了 string 值。
     * 而当我们把 @RequestBody 的接收参数改成 java.lang.Object 时由于 StringHttpMessageConverter 无法匹配该类型跳过，才被 MappingJackson2HttpMessageConverter 处理
     * <p>
     * 这里我们也注意到，我们可以注册自定义的 AbstractHttpMessageConverter 来实现，对于自身特殊请求数据格式的解析。
     * {@link AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters(HttpInputMessage, MethodParameter, Type)}
     * {@link AbstractHttpMessageConverter#canRead(Class, MediaType)}
     * {@link StringHttpMessageConverter#supports(Class)}
     * {@link AbstractJackson2HttpMessageConverter#canRead(Type, Class, MediaType)}
     * this.messageConverters = {ArrayList@6810}  size = 8
     * 0 = {ByteArrayHttpMessageConverter@6812}
     * 1 = {StringHttpMessageConverter@6707}
     * 2 = {ResourceHttpMessageConverter@6813}
     * 3 = {ResourceRegionHttpMessageConverter@6814}
     * 4 = {SourceHttpMessageConverter@6815}
     * 5 = {AllEncompassingFormHttpMessageConverter@6816}
     * 6 = {Jaxb2RootElementHttpMessageConverter@6817}
     * 7 = {MappingJackson2HttpMessageConverter@6818}
     */
    @RequestMapping(value = "/ajaxjsonobject", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object ajaxJsonObjectTest(@RequestBody Object requestJson) {
        return requestJson;
    }

}
