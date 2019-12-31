package cn.jpanda.screenshot.oss.core.controller;

import cn.jpanda.screenshot.oss.core.Configuration;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public interface ViewContext {

    /**
     * 获取默认的舞台
     */
    Stage getStage();

    /**
     * 创建一个新的舞台，主要作用是统一图标
     */
    Stage newStage();

    /**
     * 关闭舞台
     */
    void closeStage();

    /**
     * 获取场景
     *
     * @param clazz scene类
     */
    Scene getScene(Class<? extends Initializable> clazz);

    /**
     * 新增一个指定的场景
     */
    Scene getScene(Class<? extends Initializable> clazz, boolean isNew, boolean override);
    Scene getScene(Class<? extends Initializable> clazz,boolean useDefaultCss, boolean isNew, boolean override);

    /**
     * 获取指定场景是否使用默人CSS
     */
    Scene getScene(Class<? extends Initializable> clazz, boolean useDefaultCss);

    /**
     * 注册场景
     *
     * @param clazz Scene实现类
     */
    boolean registry(Class<? extends Initializable> clazz, boolean useDefaultCss);


    /**
     * 在默认的舞台上展示指定的场景
     *
     * @param scene 场景
     */
    default <T extends Scene> void showScene(T scene) {
        showScene(scene, getStage());
    }

    /**
     * 在指定舞台上展示指定的场景
     *
     * @param scene 场景
     * @param stage 舞台
     */
    <T extends Scene> void showScene(T scene, Stage stage);

    default void showScene(Class<? extends Initializable> clazz) {
        showScene(getScene(clazz));
    }

    Configuration getConfiguration();
}
