package apachebeanutils;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by JasonFitch on 2/25/2018.
 */
public class ApacheBeanUtils
{
    public static void main(String[] args)
    {
        Bean bean = new Bean();
        InterBean interBean = new InterBean();
        try
        {
            Object copied = BeanUtils.cloneBean(bean);
            System.out.println("copied" + copied.toString());
//      ok!


            Object interCopied = BeanUtils.cloneBean(interBean);
            System.out.println("interCopied" + interCopied.toString());
            /*
    java.lang.IllegalAccessException: Class org.apache.commons.beanutils.BeanUtilsBean can not access a member of class apachebeanutils.InterBean with modifiers ""
	    at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:102)
	    at java.lang.Class.newInstance(Class.java:436)
	    at org.apache.commons.beanutils.BeanUtilsBean.cloneBean(BeanUtilsBean.java:180)
	    at org.apache.commons.beanutils.BeanUtils.cloneBean(BeanUtils.java:108)
	    at apachebeanutils.ApacheBeanUtils.main(ApacheBeanUtils.java:21)

            * */

        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }

    }
}

class InterBean
{
    private String property = "interpro";

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }
}
