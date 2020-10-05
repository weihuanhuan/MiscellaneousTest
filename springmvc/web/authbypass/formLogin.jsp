<%@ page import="java.security.Principal" %>
<html>

    <head>
    <title>form login page</title>
    </head>

    <body>
        <div>
            <p>form login page</p>

            <%
                String remoteUser = request.getRemoteUser();
                Principal userPrincipal = request.getUserPrincipal();
            %>
            <p>request.getRemoteUser():    <%= remoteUser %>.</p>
            <p>request.getUserPrincipal(): <%= userPrincipal %>.</p>

            <%--
                对于 web 应用的 form 登录模式来说，其提交地址和参数名都由 servlet 规范所规定
                提交地址：j_security_check
                提交参数：j_username, j_password
             --%>

            <%--使用 response.encodeURL 可以将 session id 编码到 url 中 --%>

            <form action="<%= response.encodeURL("j_security_check") %>" method="post">
                <input type="text" name="j_username" />
                <input type="password" name="j_password"/>
                <input type="submit" value="javaeeAuthLogin.jsp"/>
            </form>
            <br/>

            <form action="../authbypass/requestLogin.jsp" method="get">
                <input type="text" name="username"/>
                <input type="password" name="password"/>
                <input type="submit" value="requestLogin.jsp"/>
            </form>
            <br/>

            <%--
                注意，登录成功后，server 会跳转到登录前的页面，
                所以在未登录状态下，如果是由登录页面触发的登录活动，那么登录成功后依旧停留在登录见面，
                此时虽然看起来页面没有进行跳转，其实当前用户的身份已经变为了登录后的身份了，那些需要认证的页面也可以访问了。
            --%>

            <a href="../auth/requestLogout.jsp">requestLogout.jsp</a>
            <br/>
            <a href="../auth/sessionLogout.jsp">sessionLogout.jsp</a>
            <br/>
            <a href="../auth/protected.jsp">protected.jsp</a>
            <br/>
        </div>
    </body>

</html>
