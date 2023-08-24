package spring.validator.config;

import org.hibernate.validator.internal.engine.resolver.DefaultTraversableResolver;

import javax.validation.Path;
import java.lang.annotation.ElementType;

public class BypassJPADetectWithDefaultTraversableResolver extends DefaultTraversableResolver {

    /**
     * 内部调用 DefaultTraversableResolver.detectJPA 方法，来自动检测 javax.persistence.Persistence 接口的存在，
     * 并自动加载 jpa 的 javax.validation.TraversableResolver 实现类 JPATraversableResolver
     *
     * @see org.hibernate.validator.internal.engine.resolver.JPATraversableResolver
     */
    public BypassJPADetectWithDefaultTraversableResolver() {
        super();
    }

    /**
     * 这是非 jpa 环境下 hibernate 默认使用的 javax.validation.TraversableResolver 实现类
     * 其内部不会自动检测 JPA 来执行逻辑，所有接口方法，其固定返回 true
     * 但是他是 package 访问权限的，所有我们不能直接使用他，需要通过继承 DefaultTraversableResolver ，并覆盖相关的方法来使用
     *
     * @see org.hibernate.validator.internal.engine.resolver.TraverseAllTraversableResolver
     */
    @Override
    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    @Override
    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

}
