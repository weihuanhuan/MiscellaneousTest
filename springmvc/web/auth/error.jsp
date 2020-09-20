<%@ page import="java.security.Principal" %>

<html>

    <head>
        <title>login error page</title>
    </head>

    <body>
        <div>
            <p>login error page</p>
            
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
