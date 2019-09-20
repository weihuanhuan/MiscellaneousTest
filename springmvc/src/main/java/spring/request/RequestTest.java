package spring.request;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by JasonFitch on 9/20/2019.
 */

@Controller
public class RequestTest {

    @RequestMapping("/requestinfo")
    @ResponseBody
    public String printRequest(HttpServletRequest httpServletRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        stringBuilder.append("################session info####################");
        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        HttpSession session = httpServletRequest.getSession();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object attribute = session.getAttribute(name);
            stringBuilder.append("name=");
            stringBuilder.append(name);
            stringBuilder.append("###");
            stringBuilder.append("attribute=");
            stringBuilder.append(attribute);
            stringBuilder.append("\n");
            stringBuilder.append("<br/>");
        }

        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        stringBuilder.append("################parameterMap info####################");
        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            String key = stringEntry.getKey();
            String[] value = stringEntry.getValue();
            stringBuilder.append("key=");
            stringBuilder.append(key);
            stringBuilder.append("###");
            stringBuilder.append("value=");
            stringBuilder.append(Arrays.toString(value));
            stringBuilder.append("\n");
            stringBuilder.append("<br/>");
        }

        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        stringBuilder.append("################header info####################");
        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = httpServletRequest.getHeader(name);
            stringBuilder.append("name=");
            stringBuilder.append(name);
            stringBuilder.append("###");
            stringBuilder.append("value=");
            stringBuilder.append(value);
            stringBuilder.append("\n");
            stringBuilder.append("<br/>");
        }

        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        stringBuilder.append("################cookie info####################");
        stringBuilder.append("\n");
        stringBuilder.append("<br/>");
        for (Cookie cookie : httpServletRequest.getCookies()) {
            String name = cookie.getName();
            String value = cookie.getValue();
            stringBuilder.append("name=");
            stringBuilder.append(name);
            stringBuilder.append("###");
            stringBuilder.append("value=");
            stringBuilder.append(value);
            stringBuilder.append("\n");
            stringBuilder.append("<br/>");
        }

        return stringBuilder.toString();
    }


}
