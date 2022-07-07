package SystemTest.Runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by JasonFitch on 11/21/2019.
 */
public class RuntimeProcessRedirect {

    public static void main(String[] args) throws IOException, InterruptedException {

        Runtime runtime = Runtime.getRuntime();

//JF      runtime 执行命令时 不能使用重定向 操作，否则返回错误状态 1 ， 原因未知。
//JF      所以对于 process 所执行的命令输出，要通过 process 的输入流读到内存中，然后在写到目标文件，来持久化。

//JF      对于 Process 来说, 需要注意其 getInputStream 方法,  不是字面意义的 InputStream,
//JF      这里的 input 是相对于发起者而言的，process所代表的子进程的 STDOUT 向 父进程 输出数据，
//JF      即对于父进程来说，是有数据 输入到了我这里，故是  getInputStream 方法获取子进程的输出流。
//JF      jdk 对其有如下解释。

        /**
         * Returns the input stream connected to the normal output of the
         * subprocess.  The stream obtains data piped from the standard
         * output of the process represented by this {@code Process} object.
         *
         * <p>If the standard output of the subprocess has been redirected using
         * {@link ProcessBuilder#redirectOutput(Redirect)
         * ProcessBuilder.redirectOutput}
         * then this method will return a
         * <a href="ProcessBuilder.html#redirect-output">null input stream</a>.
         *
         * <p>Otherwise, if the standard error of the subprocess has been
         * redirected using
         * {@link ProcessBuilder#redirectErrorStream(boolean)
         * ProcessBuilder.redirectErrorStream}
         * then the input stream returned by this method will receive the
         * merged standard output and the standard error of the subprocess.
         *
         * <p>Implementation note: It is a good idea for the returned
         * input stream to be buffered.
         *
         * @return the input stream connected to the normal output of the
         *         subprocess
         */

        String[] option = new String[]{"netstat", "-ano", ">", "test.redirect.file"};
        String cmdLine = "netstat -ano > test.redirect.file";

        Process p = runtime.exec(option);

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        int waitFor = p.waitFor();
        System.out.println(waitFor);

        int exitValue = p.exitValue();
        System.out.println(exitValue);
    }

}
