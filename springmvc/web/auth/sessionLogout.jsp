<%@ page import="java.security.Principal" %>
<html>

    <head>
    <title>session logout page</title>
    </head>

    <body>
        <div>
            <p>session logout page</p>

            <%--
                执行 javax.servlet.http.HttpSession.invalidate 销毁，使得应用登出,
                这个方法在 servlet sepc 3.0 之前就有了可以用于老旧的系统
            --%>
            <p>session logout executing...</p>
            <%
                if(session!=null){
                    boolean aNew = session.isNew();
                    session.invalidate();
                    System.out.println("session.invalidate() with session.isNew(): "+aNew);
                } else {
                    System.out.println("there is no exist a session for request");
                }
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
