package FileTest;

import java.io.File;
import java.io.IOException;

/**
 * Created by JasonFitch on 9/18/2018.
 */
public class FileTest {

    public static void main(String[] args) {

        File pathTest = new File("pathTest");
        System.out.println(pathTest.getAbsolutePath());
        try {
            if (pathTest.mkdir()) {
                System.out.println("mkdir");
            }
            if (pathTest.createNewFile()) {
                System.out.println("createFile");
            }
            if (pathTest.exists()) {
                String result = pathTest.isDirectory() ? "isDir" : "isFile";
                System.out.println(result);
            } else {
                System.out.println("not exist");
            }

            //JF 该值是文件的实时值，如果文件在创建该变量之后被删除或者改名了，那么其值为 0
            //   所以要在备份文件时利用该值确定文件版本，需要在文件改名前就暂存其该值。
            long lastModified = pathTest.lastModified();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



