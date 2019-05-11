package cn.jpanda.screenshot.oss.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@View
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueInterceptor {

    boolean isGet() default false;

    boolean isSet() default false;

}
