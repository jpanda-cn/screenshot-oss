package cn.jpanda.screenshot.oss.core.capture;

import javafx.collections.ObservableList;
import javafx.stage.Screen;

import java.awt.image.BufferedImage;

public interface ScreenCapture {
    /**
     * 执行截图
     */
    BufferedImage screenshotImage();

    /*
     * 获取首屏
     */
    Screen first();

    /**
     * 获取主屏
     */
    Screen main();

    /**
     * 获取所有屏幕
     */
    ObservableList<Screen> screens();


}
