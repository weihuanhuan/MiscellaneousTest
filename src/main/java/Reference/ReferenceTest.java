package Reference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JasonFitch on 2/9/2018.
 */
public class ReferenceTest {
    public static void main(String[] args) {
        Reference<Integer> reference = new Reference<Integer>();
        List<Integer> referenced = reference.getList();
        if (referenced == null) {
            referenced = new ArrayList<Integer>();
            reference.setList(referenced);
            referenced.add(new Integer(1107));
        }
//        类持有的非基本数据类型，实际上是一个指向真实对象的引用。对真实对象的修改，会使引用他的所有对象获取的值变化。
        System.out.println(reference.getList().toString());


        LinkedHashMap<String, String> map = null;
//        for (Map.Entry<String, String> entry : map.entrySet()) {//null point异常
//            System.out.println("1:"+entry);
//        }

        LinkedHashMap<String, String> mapNew = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : mapNew.entrySet()) {
            System.out.println("#:"+entry);
        }


        StringBuilder sb =new StringBuilder();
        System.out.println("#"+sb.toString());
    }

}

class Reference<T> {

    private List<T> list = null;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
