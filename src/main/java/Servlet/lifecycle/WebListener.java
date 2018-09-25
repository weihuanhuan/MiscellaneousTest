package Servlet.lifecycle;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.commons.collections.bag.SynchronizedSortedBag;

/**
 * Created by JasonFitch on 9/25/2018.
 */
public class WebListener implements LifecycleListener {
    @Override
    public void lifecycleEvent(LifecycleEvent lifecycleEvent) {


        System.out.println("--------------WebListener-------------");
//        System.out.println("Data     :"+lifecycleEvent.getData().toString());
        //会有状态违规异常
        System.out.println("Source   :"+lifecycleEvent.getSource().toString());
        System.out.println("Type     :"+lifecycleEvent.getType());
        System.out.println("Lifecycle:"+lifecycleEvent.getLifecycle().toString());
        System.out.println("--------------WebListener-------------");
    }
}
