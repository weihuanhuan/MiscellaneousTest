<%@ page import="java.security.Principal" %>
<html>

    <head>
    <title>login logout page</title>
    </head>

    <body>
        <div>
            <p>login logout page</p>

            <%--执行登出--%>
            <%
                request.logout();
            %>

            <%
                String remoteUser = request.getRemoteUser();
                Principal userPrincipal = request.getUserPrincipal();
            %>
            <p>request.getRemoteUser():    <%= remoteUser %>.</p>
            <p>request.getUserPrincipal(): <%= userPrincipal %>.</p>

            <a href="login.jsp">login.jsp</a>
            <a href="protected.jsp">protected.jsp</a>
        </div>
    </body>

</html>
