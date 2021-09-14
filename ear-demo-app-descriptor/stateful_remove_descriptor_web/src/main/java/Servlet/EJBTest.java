package Servlet;

import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.Remove2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveLocal2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveLocalIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestIF;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoLocalHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class EJBTest extends HttpServlet {

    @EJB(name = "removeBean")
    private RemoveLocalIF removeBean;

    @EJB(name = "removeBean2")
    private RemoveLocal2IF removeBean2;

    @EJB(name = "removeBeanRemote")
    private RemoveIF removeBeanRemote;

    @EJB(name = "removeBean2Remote")
    private Remove2IF removeBean2Remote;

    @EJB(name = "testBean")
    private TestIF testBean;

    @EJB(name = "twoLocalHome")
    private TwoLocalHome twoLocalHome;

    @EJB(name = "twoRemoteHome")
    private TwoRemoteHome twoRemoteHome;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("##################### @EJB #####################");

        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                Object fieldValue = declaredField.get(this);
                System.out.println(String.format("inject field name [%s] with value [%s]", declaredField.getName(), fieldValue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String existEJBAnnotatedJndiName = "java:comp/env/" + "removeBean";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(existEJBAnnotatedJndiName);
            System.out.println(String.format("lookup jndi name [%s] with value [%s]", existEJBAnnotatedJndiName, lookup));
        } catch (NamingException e) {
            e.printStackTrace();
        }

        System.out.println("##################### local-jndi-name #####################");

        String localJndiName = "local-jndi-name-for-removeBean";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(localJndiName);
            System.out.println(String.format("lookup jndi name [%s] with value [%s]", localJndiName, lookup));
        } catch (NamingException e) {
            e.printStackTrace();
        }

        String compLocalJndiName = "java:comp/env/" + "local-jndi-name-for-removeBean";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(compLocalJndiName);
            System.out.println(String.format("lookup jndi name [%s] with value [%s]", compLocalJndiName, lookup));
        } catch (NamingException e) {
            e.printStackTrace();
        }

        System.out.println("##################### ejb-local-ref #####################");

        String existEJBLocalRefLocalJndiName = "java:comp/env/" + "ejb-local-ref-for-removeBean-local";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(existEJBLocalRefLocalJndiName);
            System.out.println(String.format("lookup jndi name [%s] with value [%s]", existEJBLocalRefLocalJndiName, lookup));
        } catch (NamingException e) {
            e.printStackTrace();
        }

        String existEJBLocalRefLocalHomeJndiName = "java:comp/env/" + "ejb-local-ref-for-removeBean-local-home";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(existEJBLocalRefLocalHomeJndiName);
            System.out.println(String.format("lookup jndi name [%s] with value [%s]", existEJBLocalRefLocalHomeJndiName, lookup));
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }


}
