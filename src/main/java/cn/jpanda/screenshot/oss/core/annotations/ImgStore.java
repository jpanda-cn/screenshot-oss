package cn.jpanda.screenshot.oss.core.annotations;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.store.ImageStoreConfigBuilder;
import cn.jpanda.screenshot.oss.store.NoImageStoreConfigBuilder;
import cn.jpanda.screenshot.oss.store.img.NoImageStoreConfig;
import javafx.fxml.Initializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImgStore {
    /**
     * 渠道名称
     */
    String name();

    /**
     * 图标
     */
    String icon() default "/images/stores/icons/default.png";

    /**
     * 处理图片的类型
     */
    ImageType type() default ImageType.HAS_PATH;

    /**
     * 对应的配置界面
     */
    Class<? extends Initializable> config() default NoImageStoreConfig.class;


    /**
     * 对应的配置界面构建器
     */
    Class<? extends ImageStoreConfigBuilder> builder() default NoImageStoreConfigBuilder.class;
}
