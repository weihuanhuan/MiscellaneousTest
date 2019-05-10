package spring.resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ClassLoaderResourceTest {

    private static final long serialVersionUID = 1L;

    private static final String resourceName = TestResourceEntry.getResourceName();
    private static final String clazzName = TestResourceEntry.getClazzName();

    @RequestMapping("resourceall")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        Enumeration<URL> urls = getClass().getClassLoader().getResources("");
        while (urls.hasMoreElements()) {
            response.getWriter().write(urls.nextElement().toString() + "<br/>");
        }
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    @RequestMapping("resourceget")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        Enumeration<URL> urls = getClass().getClassLoader().getResources(resourceName);
        while (urls.hasMoreElements()) {
            response.getWriter().write(urls.nextElement().toString() + "<br/>");
        }

        response.getWriter().append("Served at: ").append(request.getContextPath());

    }

    @RequestMapping("resourcesc")
    private void getResourceBySC(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        try {
            URL resource = httpServletRequest.getServletContext().getResource(resourceName);
            if (resource != null) {
                System.out.println(resource.toString());
                httpServletResponse.getWriter().write((resource.toString() + "<br/>"));
            } else {
                httpServletResponse.getWriter().write("no resource found" + "<br/>");
            }

            httpServletResponse.getWriter().append("Served at: ").append(httpServletRequest.getContextPath());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("resourceload")
    private void getResourceByLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        try {
            Class<?> loadClass = this.getClass().getClassLoader().loadClass(clazzName);
            Object instance = loadClass.newInstance();
            System.out.println(instance);
            httpServletResponse.getWriter().write((loadClass.toString() + "<br/>"));

            httpServletResponse.getWriter().append("Served at: ").append(httpServletRequest.getContextPath());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
