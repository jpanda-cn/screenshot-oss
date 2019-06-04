package cn.jpanda.screenshot.oss.core.scan;

import cn.jpanda.screenshot.oss.core.annotations.View;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * 判断类上是否包含{@link View}注解的类过滤器
 */
public class ViewClassScanFilter implements ClassScanFilter {

    /**
     * 需要跳过处理的注解类型
     */
    private List<Class<? extends Annotation>> shouldContinueAnnotationType = Arrays.asList(Documented.class, Retention.class, Target.class);
    @Override
    public boolean doFilter(Class clazz) {
        if (Annotation.class.isAssignableFrom(clazz)) {
            return false;
        }
        return recursionHandlerViewAnnotation(clazz);
    }

    protected boolean recursionHandlerViewAnnotation(Class clazz) {
        // 判断是否有View注解
        if (hasViewAnnotation(clazz)) {
            return true;
        }

        // 判断注解上是否有View注解
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (shouldContinueAnnotationType.contains(annotation.annotationType())) {
                continue;
            }
            if (hasViewAnnotation(annotation.annotationType())) {
                return true;
            }
            if (recursionHandlerViewAnnotation(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasViewAnnotation(Class clazz) {
        return null != clazz.getDeclaredAnnotation(View.class);
    }
}
