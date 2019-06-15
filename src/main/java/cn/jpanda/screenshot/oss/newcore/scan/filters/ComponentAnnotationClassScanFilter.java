package cn.jpanda.screenshot.oss.newcore.scan.filters;

import cn.jpanda.screenshot.oss.newcore.annotations.Component;

import java.lang.annotation.Annotation;

/**
 * Component注解过滤器
 */
public class ComponentAnnotationClassScanFilter extends AnnotationClassScanFilter {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Component.class;
    }
}
