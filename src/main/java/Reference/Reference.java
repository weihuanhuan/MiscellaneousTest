package Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonFitch on 2/9/2018.
 */
public class Reference
{
    public static void main(String[] args)
    {
        Ref<Integer> ri = new Ref<Integer>();
        List<Integer> ril =  ri.getList();
        if(ril==null)
        {
            ril = new ArrayList<Integer>();
            ri.setList(ril);
            ril.add(new Integer(1107));
        }
//        引用的范围，实则是java栈中的存取策略
        System.out.println(ril.size());
    }

}

class Ref<T>{

    private List<T> list=null;

    public List<T> getList()
    {
           return list;
    }

    public void setList(List<T> list)
    {
        this.list = list;
    }
}
