package reflect.other;

//JF 不是公有的不能import
//import reflect.parent.ModifiedPackage;

/**
 * Created by JasonFitch on 12/24/2018.
 */
public class ModifiedPackageTest {


    public static void main(String[] args) throws ClassNotFoundException {

        //JF因为不能导入所以无法显示的使用,但是forName可以加载类路径的任何类。
//        Class<ModifiedPackage> modifiedPackageClassConcrete = null;
        Class<?> modifiedPackageClass = Class.forName("reflect.parent.ModifiedPackage");
        String canonicalName = modifiedPackageClass.getCanonicalName();
        System.out.println(canonicalName);


    }
}
