package SystemTest.Redirect.system;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by JasonFitch on 9/19/2018.
 */
public class SystemSetOutTest {

    public static void main(String[] args) throws IOException {

        System.out.println("std console");

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
//            dummy，使输出无效化
            }
        }));

        String info = "after redirect";
        //JF 此句不会输出，因为标准输出的写动作，已经在重写时哑掉了。
        System.out.println(info + " from out println");
        //println底层调用的wirte
        System.out.write((info + " from out write").getBytes());

        //err流独立于out流
        System.err.println(info + " frlnom err println");
        System.err.write((info + " from err write").getBytes());

        //JF 注意 err 与 out 是俩个不同的输出地，其操作是独立的，所以他们之间不能保证输出的顺序问题，
        //   但是 同一个流内部的顺序是可以确定的，在任务顺序确定时。
//        std console
//        after redirect frlnom err println
//        after redirect from err write

//        after redirect frlnom err println
//        std console
//        after redirect from err write


    }
}
