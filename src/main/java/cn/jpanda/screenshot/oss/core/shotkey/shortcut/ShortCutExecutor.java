package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.scene.input.KeyEvent;

/**
 * 快捷键执行器
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:17
 */
@FunctionalInterface
public interface ShortCutExecutor {


    void exec(KeyEvent event);

}
