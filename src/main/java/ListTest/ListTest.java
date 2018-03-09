package ListTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonFitch on 3/5/2018.
 */
public class ListTest
{
    public static void main(String[] args)
    {
        List<Thread> threads = new ArrayList<>();
        System.out.println(threads.size());
        Thread thread = null;
        threads.add(thread);
        System.out.println(threads.size());
    }
}
