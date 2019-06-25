package cn.jpanda.screenshot.oss.core.shotkey;

import javafx.scene.Node;

/**
 * 截图元素
 */
public interface ScreenshotsElements {
    Node getTopNode();

    default boolean canActive() {
        return true;
    }

    default boolean canDestroy() {
        return true;
    }

    /**
     * 激活
     */
    void active();

    /**
     * 销毁
     */
    void destroy();


}
