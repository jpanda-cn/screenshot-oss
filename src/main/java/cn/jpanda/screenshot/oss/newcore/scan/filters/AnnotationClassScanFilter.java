package cn.jpanda.screenshot.oss.newcore.scan.filters;

import cn.jpanda.screenshot.oss.common.utils.ReflectionUtils;
import cn.jpanda.screenshot.oss.newcore.scan.ClassScanFilter;

import java.lang.annotation.Annotation;

public abstract class AnnotationClassScanFilter implements ClassScanFilter {

    @Override
    public boolean doFilter(Class clazz) {
        if (Annotation.class.isAssignableFrom(clazz)) {
            return false;
        }
        return ReflectionUtils.hasAnnotation(clazz, getAnnotation(), true);
    }


    protected abstract Class<? extends Annotation> getAnnotation();
}
