package spring.loadclass;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.external.ExternalLoadTest;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class LoadClassTest {

    public Logger logger = Logger.getLogger(ExternalLoadTest.class.getName());

    /**
     * 这里注意我们请求的 【{class-name}】 部分值，期望应该是 【pring.loadclass.LoadClassTest】
     * 但是实际在 spring 的 @PathVariable 中却接收到了 【spring.loadclass】.
     * request url: 【http://127.0.0.1:8080/springmvc/loadclass/spring.loadclass.LoadClassTest】
     * <p>
     * 然后我们 debug 发现，spring 使用如下方法解析 {class-name} 部分值
     * spring resolve: org.springframework.util.AntPathMatcher$AntPathStringMatcher.matchStrings
     * <p>
     * 不过，其内部使用正则表达式来完成字符串解析，可以看见正则匹配的输入字符串是没有问题的
     * 问题是其使用正则 【(.*)\Q.\E.*】 ，并将其第一个捕获组作为匹配结果，
     * 但该正则还有【\Q.\E.*】部分，其匹配了字符【.LoadClassTest】部分，其中【\Q.\E】匹配了单字符【.(dot)】,【.*】匹配了【LoadClassTest】
     * 具体 【\Q】和【\E】在 java 正则中的作用可见 {@link java.util.regex.Pattern#quote(java.lang.String)}
     * 故我们这里最终的结果就是一个错误的【spring.loadclass】了
     * request input value: spring.loadclass.LoadClassTest
     * resolve expression: java.util.regex.Matcher[pattern=(.*)\Q.\E.* region=0,30 lastmatch=spring.loadclass.LoadClassTest]
     * path result value: class-name ---> spring.loadclass
     * <p>
     * 依据 spring 的实现，对于这种匹配路径占位符在最后一个路径字段，且没有任何类似【/】之类的后缀时，
     * 我们应该在 url 中添加【/】结尾，就可以避免其被上述的误匹配逻辑吞掉了。
     * 此时我们看见 spring 自动生成的正则表达式变成了【(.*)】，所以就没有问题了。
     * request url: 【http://127.0.0.1:8080/springmvc/loadclass/spring.loadclass.LoadClassTest/】
     * request input value: spring.loadclass.LoadClassTest
     * resolve expression: java.util.regex.Matcher[pattern=(.*) region=0,30 lastmatch=spring.loadclass.LoadClassTest]
     * path result value: class-name ---> spring.loadclass.LoadClassTest
     * <p>
     * 下面是 spring 执行该方法的调用栈
     * "http-nio-8080-exec-7@9282" daemon prio=5 tid=0x2b nid=NA runnable
     * java.lang.Thread.State: RUNNABLE
     * at org.springframework.util.AntPathMatcher$AntPathStringMatcher.matchStrings(AntPathMatcher.java:699)
     * at org.springframework.util.AntPathMatcher.matchStrings(AntPathMatcher.java:421)
     * at org.springframework.util.AntPathMatcher.doMatch(AntPathMatcher.java:218)
     * at org.springframework.util.AntPathMatcher.extractUriTemplateVariables(AntPathMatcher.java:498)
     * at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.handleMatch(RequestMappingInfoHandlerMapping.java:124)
     * at org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping.handleMatch(RequestMappingInfoHandlerMapping.java:57)
     * at org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.lookupHandlerMethod(AbstractHandlerMethodMapping.java:415)
     * at org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.getHandlerInternal(AbstractHandlerMethodMapping.java:365)
     * at org.springframework.web.servlet.handler.AbstractHandlerMethodMapping.getHandlerInternal(AbstractHandlerMethodMapping.java:65)
     * at org.springframework.web.servlet.handler.AbstractHandlerMapping.getHandler(AbstractHandlerMapping.java:401)
     * at org.springframework.web.servlet.DispatcherServlet.getHandler(DispatcherServlet.java:1232)
     * at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1015)
     * at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
     * at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1005)
     * at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:897)
     * at javax.servlet.http.HttpServlet.service(HttpServlet.java:635)
     */
    @RequestMapping("/loadclass/{class-name}")
    public String loadClass(@PathVariable("class-name") String clazzName) throws ClassNotFoundException {
        Objects.requireNonNull(clazzName, "clazzName cannot be null!");

        return printInfo(clazzName);
    }

    @RequestMapping("/loadclass/newJDK8Outer")
    public String newJDK8Outer() throws ClassNotFoundException {
        JDK8Outer jdk8Outer = new JDK8Outer();
        return printInfo(jdk8Outer.getClass().getName());
    }

    public String printInfo(String className) throws ClassNotFoundException {
        ClassLoader classLoader = LoadClassTest.class.getClassLoader();
        StringBuilder stringBuilder = new StringBuilder();

        Class<?> aClass = classLoader.loadClass(className);
        String file = aClass.getProtectionDomain().getCodeSource().getLocation().getFile();
        logger.info(file);
        stringBuilder.append(file);
        stringBuilder.append("</br>");

        while (classLoader != null) {
            logger.info(classLoader.toString());
            stringBuilder.append(classLoader);
            stringBuilder.append("</br>");
            classLoader = classLoader.getParent();
        }

        return stringBuilder.toString();
    }

}
