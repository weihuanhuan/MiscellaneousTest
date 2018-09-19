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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



