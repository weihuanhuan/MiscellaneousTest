<%@ page import="java.security.Principal" %>
<html>

    <head>
    <title>request logout page</title>
    </head>

    <body>
        <div>
            <p>request logout page</p>

            <%--
                执行 javax.servlet.http.HttpServletRequest.logout 登出
                这个方法直到 servet spec 3.0 时添加，其能用于 web 应用系统。
            --%>
            <p>request logout executing...</p>
            <%
                request.logout();
            %>

            <%
                String remoteUser = request.getRemoteUser();
                Principal userPrincipal = request.getUserPrincipal();
            %>
            <p>request.getRemoteUser():    <%= remoteUser %>.</p>
            <p>request.getUserPrincipal(): <%= userPrincipal %>.</p>

            <a href="../authbypass/formLogin.jsp">formLogin.jsp</a>
            <br/>
            <a href="protected.jsp">protected.jsp</a>
            <br/>
        </div>
    </body>

</html>
