<%@ page import="java.security.Principal" %>

<html>
    <head>
        <title>login protected page</title>
    </head>

    <body>
        <div>
            <p>login page protected</p>

            <%
                String remoteUser = request.getRemoteUser();
                Principal userPrincipal = request.getUserPrincipal();
            %>
            <p>request.getRemoteUser():    <%= remoteUser %>.</p>
            <p>request.getUserPrincipal(): <%= userPrincipal %>.</p>

            <a href="logout.jsp">logout.jsp</a>
        </div>
    </body>
</html>
