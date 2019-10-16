<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %>
<%@ page import="Servlet.RequestTest" %>
<%--
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

    //JF 注意使用如下方法 有时(不是绝对的) 无法区分出经过负载均衡后具体访问的tomcat实例时，现在有如下几种问题状况，原因暂时没有细查
    //   具体如下调用的输出，可以参考 Servlet.RequestTest 这个 servlet 类来查看。

    // 经过负载均衡后看见的URI是负载均衡器端收到的URL，场景是 apache mod_jk ajp13 连接方式
    // String requestURI = req.getRequestURI();

    // 无法获取到本机的端口信息。
    // String localName = req.getLocalName();
    // String localAddr = req.getLocalAddr();
    // int localPort = req.getLocalPort();

    // 无法获取到本机的端口信息
    // String serverName = req.getServerName();
    // int serverPort = req.getServerPort();

    // 可以通过看  getProtectionDomain().getCodeSource() 来确认。
    // 获取源文件的位置，得知一个类是从哪里加载到的，可以确定类加载的问题，通时也可以确定一台机器上多个 tomcat 实例的访问，无需端口信息。
    public String tomcatLocaltion = RequestTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
%>

<%
    //response 是 jsp 内置对象之一
    PrintWriter writer = response.getWriter();

    writer.write("" + counter.incrementAndGet());
    writer.write("<br/>");
    writer.write(tomcatLocaltion);
    writer.write("<br/>");
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

    //关于编译后， jsp 所对应的 java 文件，可以在tomcat的 work 目录中找见，这里可以看见 jsp执行时的细节。
    //"C:\apache-tomcat-8.5.37\work\Catalina\localhost\springmvc\org\apache\jsp\writerflushclose_jsp.java"

%>

</body>
java in jsp without close
<br/>
<br/>
jsp itself text data.
</html>
