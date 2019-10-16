<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %><%--
  Created by IntelliJ IDEA.
  User: JasonFitch
  Date: 10/16/2019
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<%!
    // java in jsp 的代码编译之后存在于 servlet 的 service 方法中，是方法级别的，而 带有 ! 号的声明则存在于 类级别，
    // 注意使用时的区别,后者可以做一些全局数据的操作。
    public AtomicInteger counter = new AtomicInteger(0);
%>

<%
    //response 是 jsp 内置对象之一
    PrintWriter writer = response.getWriter();

    writer.write("" + counter.incrementAndGet());
    writer.write("<br/>");
    writer.write("java in jsp writer write");
    writer.write("<br/>");
    writer.write("<br/>");

    //JF 注意这里最好不要调用 flush， 只会提前触发输出流写出数据，
    // 导致 org.apache.coyote.Response.sendHeaders() 方法的调用，会直接发送头，
    // 同时 org.apache.coyote.Response.setCommitted() 方法也会被调用，
    // 这些可能导致某些需要返回响应之前的，修改的操作无法执行，
    // 比如在servlet处理完成之后，通过 valve 来修改 session 信息的，因为 sessionid 信息一般是由 cookie 来承载的，
    // 当使用带有 jvmRoute 的 session 时需要改写 session id，如果 header 已经发送了，那么就无法去在次修改了。
    writer.write("java in jsp writer flush before");
    writer.write("<br/>");
//    writer.flush();

    // flush 只是会将缓冲里面的数据立即发送，输出流依旧是可以使用的，
    // 不影响后续输出的，只不过先前的输出无法修改了，同时转发之类的处理也会失效，因为服务器已经返回这个请求的数据了。
    writer.write("java in jsp writer flush after");
    writer.write("<br/>");
    writer.write("<br/>");

    //JF 注意这里如果直接 close 会触发提前关闭输出流，导致 JSP 页面的内容无法输出。
    //   而tomcat 处理的时候是先输出 java in jsp 的内容，如果这里 close了，jsp的内容就丢了。
    writer.write("java in jsp writer close before");
    writer.write("<br/>");
//    writer.close();
    writer.write("java in jsp writer close after");
    writer.write("<br/>");
    writer.write("<br/>");

%>

</body>
java in jsp without close
<br/>
<br/>
jsp itself text data.
</html>
