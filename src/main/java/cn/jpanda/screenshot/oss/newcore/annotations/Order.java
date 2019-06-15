package cn.jpanda.screenshot.oss.newcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    /**
     * 排序优先级，数值越大，优先级越高
     */
    int value() default Integer.MIN_VALUE;

}
