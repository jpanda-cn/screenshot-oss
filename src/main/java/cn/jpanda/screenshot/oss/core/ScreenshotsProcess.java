package cn.jpanda.screenshot.oss.core;

import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

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
    void done(BufferedImage image);


}