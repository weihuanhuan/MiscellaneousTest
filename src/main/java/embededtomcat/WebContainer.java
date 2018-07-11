package embededtomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

/**
 * Created by JasonFitch on 2/3/2018.
 */
public class WebContainer extends Tomcat
{

    public static void main(String[] args)
    {
        WebContainer wc = new WebContainer();
        try
        {
            wc.init();
            wc.start();
            wc.stop();
            wc.destroy();
        } catch (LifecycleException e)
        {
            e.printStackTrace();
        }
    }
}
