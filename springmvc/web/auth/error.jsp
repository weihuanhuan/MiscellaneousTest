<%@ page import="java.security.Principal" %>

<html>

    <head>
        <title>error page</title>
    </head>

    <body>
        <div>
            <p>error page</p>
            
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
            <a href="error.jsp">error.jsp</a>
            <br/>
        </div>
    </body>

</html>
