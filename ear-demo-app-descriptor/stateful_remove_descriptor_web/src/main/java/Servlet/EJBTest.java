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

        Field[] declaredFields = this.getClass().getDeclaredFields();

        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                Object fieldValue = declaredField.get(this);
                System.out.println(String.format("field name [%s] with value [%s]", declaredField.getName(), fieldValue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String jndiName = "local-jndi-name-for-StatelessTestBean";
        try {
            InitialContext initialContext = new InitialContext();
            Object lookup = initialContext.lookup(jndiName);
            System.out.println(lookup);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


}
