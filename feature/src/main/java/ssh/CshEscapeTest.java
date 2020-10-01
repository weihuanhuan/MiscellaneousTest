package ssh;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;

import java.io.IOException;
import java.io.OutputStream;

public class CshEscapeTest {

    public static void main(String[] args) throws IOException {
        String hostname = "192.168.88.150";
        int port = 22;

        //whh  账户使用 csh，root 账户使用 bash
        String username = "whh";
        String password = "123456";

        Connection connection = new Connection(hostname, port);
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        Session session = connection.openSession();
        //不是用交互终端
        //session.requestDumbPTY();
        session.startShell();

        //对于 csh 来说，下面的命令【echo B#2008_2108#es $0】在 trilead 中执行只输出了 【B】，最后的 【$0】部分甚至没有输出
        //同时我们直接使用 ssh 执行这个命令也是只得到了同样的结果，
        //为了防止发起 ssh 的 shell 执行前对 $0 进行扩展我们使用了单引号包裹。
        //$ ssh whh@192.168.88.150 'echo B#2008#2108es $0'
        //whh@192.168.88.150's password:
        //B

        //最后在 csh 的 man csh 内容的 Lexical structure 章节中我们发现了如下描述
        //当 csh 的 stdin 不是一个终端时（非交互），其会将【#】字符作为注释起始的口令，直到该行的结尾结束注释。
        //所以上面的命令在 csh 执行时，抛去注释部分就变成了【echo B】的形式，所以结果中的就只有【B】了。

        //因此当使用 csh 的【非交互模式时】，对于命令中的【#】我们要，避免使用，或者进行转义处理。
        //转移的方法可以参考 man csh 的 Lexical structure 章节，可以使用转义字符，双引号或单引号的方法来处理
        //这里对于 trilead 来说我们将命令使用【'】单引号包裹即可，即使用【echo 'B#2008_2108#es $0'】的形式
        //不过使用单引号时，由于单引号禁止了变量扩展，所以【$0】就不会被解释了，单引号中的内容按照原样作为 echo 的参数来输出。

        //对于 ssh 也可以如法炮制，这次我们使用双引号来处理参数，如下例子所示，可以看见 $0 的结果也是正确的。
        //但是若我们使用双引号的话，则对于 server 的 csh 来说，其变量扩展依旧时会执行的，此时我们可以看见正常的 csh
        //同时由于 ssh 命令 command 部分外部的单引号禁止了 local 的 bash 扩展，此时我们的命令才会返回来自 server 端的数据。
        //$ ssh whh@192.168.88.150  'echo "B#2008#2108es $0"'
        //whh@192.168.88.150's password:
        //B#2008#2108es csh

        //而当【不使用】 csh 的非交互模式时，我们可以使用 pseudo-terminal 来防止命令中【#】被解释为注释的开始标记
        //对于 trilead 来说我们使用其 com.trilead.ssh2.Session.requestDumbPTY 方法来获取 terminal 来使用
        //对于 ssh 则可以使用等效的下面命令形式，这里我们没有转义，但是使用【-tt】参数来强制分配一个 terminal。
        //$ ssh -tt whh@192.168.88.150 'echo B#2008#2108es $0'
        //whh@192.168.88.150's password:
        //B#2008#2108es csh
        //Connection to 192.168.88.150 closed.

        //另外：
        //1.对于 trilead 的 stdin 中写【command】等价于 ssh 命令的 【command】 部分，例如
        //  stdin.write("echo B#2008_2108#es $0".getBytes());
        //  stdin.write("\n".getBytes());
        //  stdin.flush();
        //  等于【键入】下面命令并按下【回车】
        //  ssh  whh@192.168.88.150  'echo B#2008#2108es $0'

        //2.对于 bash 而言就没有【#】这个问题，其可以直接正常的使用【#】作为命令的内容。
        OutputStream stdin = session.getStdin();
        stdin.write("echo B#2008_2108#es $0".getBytes());
        stdin.write("\n".getBytes());
        stdin.flush();

        stdin.write("exit $?".getBytes());
        stdin.write("\n".getBytes());
        stdin.flush();
        stdin.close();

        SCPLoginTest.processSessionStream(session);
        SCPLoginTest.printSessionStatus(session);

        session.close();
        connection.close();
        System.out.println("---------------- connection close -----------------------");
    }

}
