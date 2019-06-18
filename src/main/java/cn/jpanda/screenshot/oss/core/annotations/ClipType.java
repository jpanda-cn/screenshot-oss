package cn.jpanda.screenshot.oss.core.annotations;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClipType {
    String name();

    ClipboardType type() default cn.jpanda.screenshot.oss.common.enums.ClipboardType.NEED_PATH;
}
