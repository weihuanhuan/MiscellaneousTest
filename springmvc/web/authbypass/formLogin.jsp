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

            <%--
                对于 j_security_check 进行认证的虚拟请求页面来说，
                如果我们使用的是相对路径，那么其会依据发起请求的页面的 url 来构建一个相对于发起认证页面的完整 url ，
                这里如果我们的触发页面是 【/auth/protected.jsp】 那么构建之后就是 【/auth/j_security_check】
                而构建之后的页面依旧是符合 <security-constraint> 的，
                所以即使他不被 AuthenticatorBase.isContinuationRequired  出理也是需要执行认证逻辑。

                所以这里显式的拼接一个绝对的请求路径，并将其作为要跳转的认证页面，结果就是【/springmvc/authbypass/j_security_check】
                其不符合 <security-constraint> ，这样子我们可以用其用来检测 tomcat 的 AuthenticatorBase.isContinuationRequired 方法
                进而可以确定是应因为 isContinuationRequired 还是因为 <security-constraint> 触发的登录，

                实际上对于 FORM 来说，即使没有 <security-constraint> 来强制的触发认证逻辑的执行，
                当出现了 j_security_check 认证页面时，也要通过 FormAuthenticator.isContinuationRequired 来保证 FORM 的认证阶段可以正确的执行。
                否则此时不仅认证没有正确的处理，而且还导致了页面访问到了专为 FORM 认证而虚拟的地址，并产生错误的 404 响应。
            --%>
            <form action="<%= response.encodeURL(request.getContextPath() + "/authbypass/j_security_check") %>" method="post">
                <input type="text" name="j_username" />
                <input type="password" name="j_password"/>
                <input type="submit" value="javaeeAuthBypassLogin.jsp"/>
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
