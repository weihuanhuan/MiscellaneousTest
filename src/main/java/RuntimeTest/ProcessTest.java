package RuntimeTest;


import java.io.IOException;

public class ProcessTest
{

    public static void main(String[] args)
    {

        String parameter = "F:/JetBrains/IntelliJ IDEA/BEStest/ant/executor/start.bat";

        if (args.length == 0 || args[0].equals(""))
        {
            ExecUtils.exec(parameter);
        } else
        {
            ExecUtils.exec(args[0]);
        }

        try
        {
            System.in.read();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
