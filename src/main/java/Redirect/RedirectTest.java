package Redirect;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by JasonFitch on 9/19/2018.
 */
public class RedirectTest {

    public static void main(String[] args) {

        System.out.println("std console");

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
//            dummy，使输出无效化
            }
        }));

        System.out.println("after redirect");
        //此句不会输出，因为标准输出的写动作，已经在重写时哑掉了。
    }
}
