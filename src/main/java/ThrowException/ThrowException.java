package ThrowException;

import java.io.IOException;
import java.util.EmptyStackException;

/**
 * Created by JasonFitch on 2/6/2018.
 */
public class ThrowException
{
    public static void main(String[] args)
    {
        System.out.println(ClassLoader.getSystemClassLoader());

        ThrowException te = new ThrowException();

        try
        {
            te.throwException();

        } catch (Exception ex)
        {
    //  捕获异常后可以不处理
        }


        System.out.println("");
    }


    public void throwException() throws IOException
    {
        Exception esex = new IOException();
//        throw esex;
//        不抛出异常，函数也可以有 throws 子句
    }
}
