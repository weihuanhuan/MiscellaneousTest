<%@ page import="java.security.Principal" %>

<html>
    <head>
        <title>protected page</title>
    </head>

    <body>
        <div>
            <p>protected page</p>

            <%
                String remoteUser = request.getRemoteUser();
                Principal userPrincipal = request.getUserPrincipal();
            %>
            <p>request.getRemoteUser():    <%= remoteUser %>.</p>
            <p>request.getUserPrincipal(): <%= userPrincipal %>.</p>

            <a href="requestLogout.jsp">requestLogout.jsp</a>
            <br/>
            <a href="sessionLogout.jsp">sessionLogout.jsp</a>
            <br/>
            <a href="protected.jsp">protected.jsp</a>
            <br/>
        </div>
    </body>
</html>
