package cn.jpanda.screenshot.oss.core.annotations;

import cn.jpanda.screenshot.oss.core.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置文件注解,是否在需要持久化的类上标注该注解，并不重要，就算不标注，数据依然可以持久化。
 * 但是标注该注解，可以选择持久化时使用的配置文件,以及使用的持久化策略。
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Profile {
    /**
     * 使用的配置文件名称
     */
    String value() default Configuration.DEFAULT_COMMON_CONFIG_FILE;

    /**
     * 当前类是否为引导配置文件类
     * 对于引导配置文件类的操作将会由引导持久策略来完成
     */
    boolean bootstrap() default false;

}
