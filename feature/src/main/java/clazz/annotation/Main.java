package clazz.annotation;

import clazz.annotation.anno.AnnoA;
import clazz.annotation.clazz.A;
import clazz.annotation.clazz.B;
import clazz.annotation.clazz.C;
import clazz.annotation.clazz.D;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

/**
 * Created by JasonFitch on 5/16/2019.
 */
public class Main {

    public static void main(String[] args) {

        //简单注解测试
        printAnnotationInfo(A.class);
        //子类继承注解测试  @Inherited
        printAnnotationInfo(B.class);
        //元注解测试      @注解的注解
        printAnnotationInfo(C.class);
        //注解保留域测试   @Retention
        printAnnotationInfo(D.class);

        System.out.println();

        //JF 关于 meta-annotation 的获取问题
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        Annotation[] annoOfC = C.class.getAnnotations();
        String strOfC;

        for (Annotation anno : annoOfC) {

            //输出处理的注解本身
            System.out.println(anno);

            //取得注解的注解(元注解)，但是结果却为空，为什么？
            Annotation[] annotations1 = anno.getClass().getAnnotations();
            strOfC = Arrays.deepToString(annotations1);
            System.out.println(strOfC);

            //查看注解的Class，原来是一个proxy Class 啊
            Class<? extends Annotation> aClass1 = anno.getClass();
            System.out.println(aClass1);

            //使用annotationType拿到真实的注解Class
            Class<? extends Annotation> aClass = anno.annotationType();
            System.out.println(aClass);

            //在真实的注解Class上便可以拿到元注解了
            Annotation[] annotations = aClass.getAnnotations();
            strOfC = Arrays.deepToString(annotations);
            System.out.println(strOfC);

            //JF 关于原注解的获取
            // 拿到的注解其实是个代理对象，所以getClass后那注解等于是在代理类上，当然拿不到那些元注解的
            // 而通过annotationType可以拿到实际的注解类，然后就可以拿到“类”上的注解了。
            // 这么一看就理解 Class<? extends Annotation> aClass1 = anno.getClass()，注解信息本身是基于继承的动态代理。

            // 同时注意到 Spring 使用如下工具类来查找注解
            // org.springframework.core.annotation.AnnotationUtils.findAnnotation(java.lang.Class<?>, java.lang.Class<A>)
        }
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        System.out.println();
        String strTmp;
        boolean annotation = A.class.isAnnotation();
        System.out.println(annotation);

        AnnoA annotation1 = A.class.getAnnotation(AnnoA.class);
        System.out.println(annotation1);

        AnnoA[] annotationsByType = A.class.getAnnotationsByType(AnnoA.class);
        strTmp = Arrays.deepToString(annotationsByType);
        System.out.println(strTmp);

        AnnotatedType annotatedSuperclass = A.class.getAnnotatedSuperclass();
        System.out.println(annotatedSuperclass);

        AnnotatedType[] annotatedInterfaces = A.class.getAnnotatedInterfaces();
        strTmp = Arrays.deepToString(annotatedInterfaces);
        System.out.println(strTmp);

    }

    public static void printAnnotationInfo(Class<?> clazz) {
        //不包括继承来的注解
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        String string = Arrays.deepToString(annotations);
        System.out.println("DeclaredAnnotations = " + string);

        //包括继承来的注解
        annotations = clazz.getAnnotations();
        string = Arrays.deepToString(annotations);
        System.out.println("Annotations=          " + string);

        System.out.println("------------------------" + clazz + "-------------------------------");

    }

}
