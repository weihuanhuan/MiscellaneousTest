package RuntimeTest;


import java.io.IOException;

public class ExecUtils
{

    private static Runtime rt = Runtime.getRuntime();

    public static String exec(String command, boolean redirect) throws IOException, InterruptedException
    {

        Process exec = rt.exec(command);

        exec.waitFor();

        if (redirect)
        {
            exec.getOutputStream();
            exec.getErrorStream();
        }

        int exitCode = exec.exitValue();

        return String.valueOf(exitCode);
    }

    public static void exec(String command)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    System.out.println("process is executing with parameter: \"" + command + "\"");
                    exec(command, false);

                } catch (IOException e)
                {
                    e.printStackTrace();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }
}
