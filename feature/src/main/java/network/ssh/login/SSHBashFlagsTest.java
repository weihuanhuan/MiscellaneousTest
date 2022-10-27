package network.ssh.login;

import com.trilead.ssh2.Connection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JasonFitch on 7/11/2020.
 */
public class SSHBashFlagsTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        String hostname = "192.168.88.10";
        int port = 22;

        String username = "root";
        String password = "123456";

        Connection connection = new Connection(hostname, port);
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        SSHConnection SSHConnection = new SSHConnection(connection);

        //交互登录
        //标志是 himBH ，这个标志和 xshell 登录到 linux 上是一模一样的
        //而且 $0 有前导的 -(hyphen)，即 【-bash】，这暗示他是一个 login bash
        //另外也可以使用 shopt -q login_shell 的返回值来判断 bash 的登录状态，值 0 表示 login ，1 表示 non-login
        System.out.println("---------------- login connection login with pty -----------------------");
        String term = "vt100";
        String commandPTY = "echo ---- login with pty ---- && echo $- && echo $0 && shopt -q login_shell ; echo $? ";
        SSHConnection.loginWithPTYExec(term, commandPTY, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        //非交互登录
        //标志是 hB ，没有表示 interactive 的标志 【-i】，
        //不过他是一个 login bash，可从 $0 有前导的 -(hyphen) 来确认。
        //所以可以由 .bash_profile 来间接的调用 .bashrc 的执行
        System.out.println("---------------- login connection login -----------------------");
        String commandLogin = "echo ---- login ---- && echo $- && echo $0 && shopt -q login_shell ; echo $? ";
        SSHConnection.loginExec(commandLogin, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        //非交互非登录
        //标志是 hBc，依旧没有 -i 的标志，但是他是一个 non-login bash，其 $0 的结果是 【bash】
        //但是他也执行了 .bashrc ，那么在 non-login 且 non-interactive 的 bash 中为什么他执行了？
        //因为 bash 手册的  Invocation 章节中有如下描述：
        //Bash attempts to determine when it is being run with its standard input connected to a network connection,
        //as when executed by the remote shell daemon, usually rshd, or the secure shell daemon sshd.
        //If bash determines it is being run in this  fashion,
        //it reads  and executes commands from ~/.bashrc, if that file exists and is readable.

        //参考：https://unix.stackexchange.com/questions/257571/why-does-bashrc-check-whether-the-current-shell-is-interactive
        System.out.println("---------------- login connection non-login -----------------------");
        String commandNonLogin = "echo ---- non-login ---- && echo $- && echo $0 && shopt -q login_shell ; echo $? ";
        SSHConnection.nonLoginExec(commandNonLogin, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        System.out.println("---------------- login connection close -----------------------");
        SSHConnection.close();

        //对于 bash 而言其查找 login shell 的 登录脚本顺序如下
        //if [ -f ~/.bash_profile ]; then
        //    . ~/.bash_profile;
        //elif [ -f ~/.bash_login ]; then
        //    . ~/.bash_login;
        //elif [ -f ~/.profile ]; then
        //    . ~/.profile;
        //fi;

    }
}
