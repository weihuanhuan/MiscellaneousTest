package clazz.annotation.anno;

/**
 * Created by JasonFitch on 5/16/2019.
 */
//JF 注意注解的保留时间，如果不是运行时那么反射是那不到这个注解的。
//@Retention(RetentionPolicy.RUNTIME)
public @interface AnnoD {

    String value();
}
