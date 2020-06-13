package cn.jpanda.screenshot.oss.core;

import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

import java.awt.image.BufferedImage;

/**
 * 截图流程
 */
public interface ScreenshotsProcess {

    /**
     * 执行截屏操作
     */
    BufferedImage snapshot(Scene scene, Rectangle rectangle);

    /**
     * 完成截屏操作
     */
    void done(Window window, BufferedImage image);
    void done(Window window, BufferedImage image,String extendsName);


}
