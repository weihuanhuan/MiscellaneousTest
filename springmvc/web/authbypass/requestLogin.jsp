<%@ page import="java.security.Principal" %>
<html>

    <head>
    <title>request login page</title>
    </head>

    <body>
        <div>
            <p>request login page</p>

            <%
                String remoteUserBefore = request.getRemoteUser();
                Principal userPrincipalBefore = request.getUserPrincipal();
            %>
            <p>before login request.getRemoteUser():    <%= remoteUserBefore %>.</p>
            <p>before login request.getUserPrincipal(): <%= userPrincipalBefore %>.</p>

            <%--
                执行 javax.servlet.http.HttpServletRequest.login 登入
                这个方法直到 servet spec 3.0 时添加，其能用于 web 应用系统。
            --%>
            <p>request login executing...</p>
            <%
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                request.login(username, password);
            %>

            <%
                String remoteUserAfter = request.getRemoteUser();
                Principal userPrincipalAfter = request.getUserPrincipal();
            %>
            <p>after login request.getRemoteUser():    <%= remoteUserAfter %>.</p>
            <p>after login request.getUserPrincipal(): <%= userPrincipalAfter %>.</p>

            <a href="../auth/requestLogout.jsp">requestLogout.jsp</a>
            <br/>
            <a href="../auth/sessionLogout.jsp">sessionLogout.jsp</a>
            <br/>
            <a href="../auth/protected.jsp">protected.jsp</a>
            <br/>
        </div>
    </body>

</html>
