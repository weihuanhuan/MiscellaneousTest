package network.proxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ObjectHandler implements InvocationHandler {

    private final Object realObject;

    public ObjectHandler(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxyObject, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        //avoid nested recursion with java.lang.String.format(java.lang.String, java.lang.Object...)
        //这里由于 proxyObject 就是由 java.lang.reflect.Proxy.newProxyInstance 所产生的对象，所以对其再次调用 toString 就会导致嵌套递归调用了。
        //而且这个检测必须要在触发 toString 的调用之前执行才行，比如要在下面的 format 方法之前调用
        if (name.equals("toString") && method.getDeclaringClass() == Object.class) {
            return "proxy object avoid unlimited recursive invoke toString";
        }

        Object result = method.invoke(realObject, args);
        String format = String.format("proxy object [%s] invoke method [%s] with real object [%s]", proxyObject, name, realObject);
        System.out.println(format);
        return result;
    }
}