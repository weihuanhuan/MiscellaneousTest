<%--
  Created by IntelliJ IDEA.
  User: JasonFitch
  Date: 2/26/2019
  Time: 17:32
  To change this template use File | Settings | File Templates.
--%>
<html>
  <head>
    <title>springmvc welcome page</title>
  </head>

  <body>
    <p>springmvc welcome page</p>

    <a href="auth/protected.jsp">protected.jsp</a>
    <br/>

    <%--
      虽然由于同源策略 cookie 不会跨域发送，但是他可以跨子域发送。
      比如我们设置 cookie 的 domain 为 【ltpa.com】，那么属于该域的子域请求均可以共享设置为该域的 cookie
      比如下面这些域都是 【xxx.ltpa.com】的形式，所以他们都是【ltpa.com】的子域，均可以使用其 cookie

      注意1:下面的那个 【ltpa.com】的域，他不是【ltpa.com】的子域，其少了第二段【.】所以是【.com】的子域
      注意2：对于 tomcat 来说，其 org.apache.tomcat.util.http.Rfc6265CookieProcessor.validateDomain 实现验证了 cookie domian
            这个验证不允许 cookie domain 的以【.】(dot)开头，但是很多其他的服务器是允许的，
            对于这些允许的服务器来说，此时【.ltpa.com】的【ltpa.com】是等效的，比如 open liberty 就是这么处理的
    --%>
    <a href="http://www.ltpa.com:8080/springmvc/auth/protected.jsp">sub-domain www.ltpa.com protected.jsp</a>
    <br/>
    <a href="http://a.ltpa.com:9081/springmvc1/auth/protected.jsp">sub-domain a.ltpa.com protected.jsp</a>
    <br/>
    <a href="http://b.ltpa.com:9082/springmvc2/auth/protected.jsp">sub-domain b.ltpa.com protected.jsp</a>
    <br/>

    <a href="http://ltpa.com:8080/springmvc/auth/protected.jsp">cross-domain ltpa.com protected.jsp</a>
    <br/>
  </body>
</html>
