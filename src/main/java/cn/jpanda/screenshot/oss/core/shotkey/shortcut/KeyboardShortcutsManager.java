package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * 快键键管理器
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 9:56
 */
public class KeyboardShortcutsManager {

    /**
     * 注册快捷键
     *
     * @return
     */
    public boolean registryShortCut(EventTarget eventTarget, ShortCutExecutorHolder holder) {
        System.out.println(String.format("will registry the short key with %s  and %s", eventTarget.toString(), holder.getShortcut().toString()));

        if (eventTarget instanceof Window) {
            if (((Window) eventTarget).getScene() != null) {
                registryShortCut(((Window) eventTarget).getScene(), holder);
            } else {
                ((Window) eventTarget).sceneProperty().addListener((observable, oldValue, newValue) -> registryShortCut(newValue, holder));
            }
            return true;
        }
        if (eventTarget instanceof Scene) {
            ((Scene) eventTarget).addEventHandler(holder.getShortcut().getKeyEvent(), holder::exec);
            return true;
        }
        if (eventTarget instanceof Node) {
            ((Node) eventTarget).addEventHandler(holder.getShortcut().getKeyEvent(), holder::exec);
            return true;
        }
        return false;
    }
}
