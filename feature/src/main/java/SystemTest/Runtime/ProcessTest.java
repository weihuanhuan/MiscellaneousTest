package SystemTest.Runtime;


import java.io.IOException;

public class ProcessTest {

    public static void main(String[] args) {
        try {
            String execute = "F:/JetBrains/IntelliJ IDEA/MiscellaneousTest/script/maven/executor/start.bat";

            if (args.length == 0 || args[0].equals("")) {
                System.out.println("startFile from internal");
                ExecUtils.exec(execute, null, false);

            } else {
                System.out.println("startFile from external");
                String[] newArr = new String[args.length - 1];
                System.arraycopy(args, 1, newArr, 0, args.length - 1);
                ExecUtils.startServer(args[0], newArr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("please press enter to continue.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
