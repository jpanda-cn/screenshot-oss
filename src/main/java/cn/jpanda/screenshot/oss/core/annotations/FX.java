package cn.jpanda.screenshot.oss.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FX 注解用于标注{@link javafx.fxml.Initializable}实现类fxml文件的位置
 */
@View
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FX {
    /**
     * 目录前缀，不填默认使用实现类的包路径
     */
    String dir() default "";

    /**
     * fxml文件类型
     */
    String suffix() default ".fxml";

    /**
     * fxml文件名称
     */
    String fxmlName() default "";
}
