package clazz.bytecode.transformer;

import clazz.bytecode.javassist.JavassistTest;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class ThreadSleepTransformerModify implements ClassFileTransformer {

    private JavassistTest javassistTest = new JavassistTest();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            byte[] instrumentBytecode = javassistTest.doInstrumentSystemPath(className, classfileBuffer);
            System.out.println("use modify instrumented byte code");
            return instrumentBytecode;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("use original byte code");
        }
        return classfileBuffer;
    }

}
