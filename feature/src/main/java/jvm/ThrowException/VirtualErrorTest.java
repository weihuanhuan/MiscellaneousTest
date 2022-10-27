package jvm.ThrowException;

/**
 * Created by JasonFitch on 4/3/2019.
 */
public class VirtualErrorTest {


    public static void main(String[] args) {

        try {
            //
            throw  new OutOfMemoryError();
        }catch (Error err){
            err.printStackTrace();
        }

        System.out.println("end");

    }
}
