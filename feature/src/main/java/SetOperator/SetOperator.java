package SetOperator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JasonFitch on 2/2/2018.
 */
public class SetOperator
{
    public static void main(String[] args)
    {
        List<String> cli = new ArrayList<>();
        for (int i = 1; i <=3; ++i)
        {
            cli.add(String.valueOf(i));
        }

        List<String> ser =new ArrayList<>();
        for (int i = 2; i <= 4; ++i)
        {
            ser.add(String.valueOf(i));
        }

        Set<String> hs = new HashSet<>(cli);

        List<String> del =new ArrayList<>();
        for (String s : ser)
        {
            if(hs.add(s))
            {
                del.add(s);
            }

        }

        List<String>  add=new ArrayList<>();
        hs = new HashSet<>(ser);
        for (String s : cli)
        {
            if(hs.add(s))
            {
                add.add(s);
            }

        }

        System.out.println(del);
        System.out.println(add);

    }
}
