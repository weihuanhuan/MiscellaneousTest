<%@ page import="java.util.logging.Logger" %><%--
  Created by IntelliJ IDEA.
  User: JasonFitch
  Date: 10/18/2019
  Time: 16:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>externaljsp.jsp</title>
</head>
<body>

<%-- jsp 页面可以使用 this 来指代当前 jsp 文件生成 java 文件后，编译为 class 文件的类，这样子我们便可以使用 Class 相关的操作了.--%>
<%
    Class<?> aClass = this.getClass();
    Logger logger = Logger.getLogger(aClass.getName());

    String file = aClass.getProtectionDomain().getCodeSource().getLocation().getFile();
    logger.info(file);

    ClassLoader classLoader = aClass.getClassLoader();
    while (classLoader != null) {
        logger.info(classLoader.toString());
        classLoader = classLoader.getParent();
    }
%>

<%-- 这里由于IDE的问题，导致语法提示信息不准确，实际上这些jsp内置变量的使用都是没有问题的，比如这里的this,out对象等--%>
<%
    out.write(file);
%>

</br>
externaljsp.jsp

</body>
</html>
