package spring.swagger.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import java.util.Map;

public class SwaggerInitializeCondition implements Condition {

    private static final String SWAGGER_CLASS_NAME = "springfox.documentation.swagger2.annotations.EnableSwagger2";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean match = isMatchPropertyValue(context, metadata);
        if (!match) {
            return false;
        }

        boolean present = isPresentCoreClass(SWAGGER_CLASS_NAME, context.getClassLoader());
        if (!present) {
            return false;
        }
        return true;
    }

    private static boolean isMatchPropertyValue(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String matchPropertyName = getMatchPropertyName(metadata);
        if (matchPropertyName == null || matchPropertyName.isEmpty()) {
            return false;
        }

        Environment environment = context.getEnvironment();
        String property = environment.getProperty(matchPropertyName);
        if (!Boolean.parseBoolean(property)) {
            return false;
        }

        return true;
    }

    private static String getMatchPropertyName(AnnotatedTypeMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(SwaggerInitialize.class.getName());
        if (attrs == null) {
            return null;
        }

        Object matchPropertyName = attrs.get("value");
        if (!(matchPropertyName instanceof String)) {
            return null;
        }

        return (String) matchPropertyName;
    }

    private static boolean isPresentCoreClass(String className, ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        try {
            ClassUtils.forName(className, classLoader);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

}
