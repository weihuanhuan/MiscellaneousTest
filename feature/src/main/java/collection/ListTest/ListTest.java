package collection.ListTest;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JasonFitch on 3/5/2018.
 */
public class ListTest {

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        System.out.println(threads.size());
        Thread thread = null;
        threads.add(thread);
        System.out.println(threads.size());


        Thread[]     arrThread  = new Thread[0];
        List<Thread> threadList = Arrays.<Thread>asList(arrThread);
        threads.toArray(arrThread);

        Collection collection = new ArrayList<>();
        //Collection 是接口，实现了 Iterable
        Collection collection2 = new AbstractCollection() {
            @Override
            public Iterator iterator() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        };
        //AbstractCollection 是抽象类,实现了 Collection

        System.out.println("-------------------------------------");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("k1", "v1");
        hashMap.put("k2", "v2");
        System.out.println(hashMap.size());
        Set<Map.Entry<String, String>> entrys = hashMap.entrySet();
        for (Iterator<Map.Entry<String, String>> it = entrys.iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            entry.getKey();
            entry.getValue();
            it.remove();
        }
        System.out.println(hashMap.size());
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            entry.getKey();
            entry.getValue();
        }
        //foreach 实际使用 Iterator 去遍历。

    }
}
