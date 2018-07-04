package JDBCMysql;

import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import javax.transaction.xa.XAException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JasonFitch on 3/8/2018.
 */
public class JDBCMysql
{
    public static void main(String[] args)
    {

        try
        {
            GeronimoTransactionManager gtm = new GeronimoTransactionManager();
        } catch (XAException e)
        {
            e.printStackTrace();
        }

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        HashMap<String,String> hm = new HashMap<>();

        hm.put("1","1");
        List<String> l = Collections.emptyList();
        boolean b = hm.keySet().containsAll(l);  //true
        System.out.println(b);
        hm.keySet().containsAll(null);   //NullPointerException

        // asm3 is Interface

        new org.objectweb.asm.ClassVisitor()
        {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
            {

            }

            @Override
            public void visitSource(String source, String debug)
            {

            }

            @Override
            public void visitOuterClass(String owner, String name, String desc)
            {

            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible)
            {
                return null;
            }

            @Override
            public void visitAttribute(Attribute attr)
            {

            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access)
            {

            }

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
            {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
            {
                return null;
            }

            @Override
            public void visitEnd()
            {

            }
        };


    }
}

interface IFa
{
    void func();
}

//abstract class A extends IFa {}   //Interface not allowed to extends
