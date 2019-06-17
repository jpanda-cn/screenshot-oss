package cn.jpanda.screenshot.oss.newcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排除注解
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/17 9:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exclude {
    Class[] value() default {};
}
