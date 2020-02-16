package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortCutExecutorHolder;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class ScreenshotToolbarBoxButtonHolder {
    private ScreenshotToolbarBoxButtonHolder() {
    }

    public static ScreenshotToolbarBoxButtonHolder of() {
        return new ScreenshotToolbarBoxButtonHolder();
    }

    /**
     * 按钮
     */
    private Button button;
    /**
     * 提示
     */
    private Tooltip tooltip;
    /**
     * 类型
     */
    private CutInnerType type;

    /**
     * 选项组
     */
    private List<Node> options;
    /**
     * 默认
     */
    private boolean isDefault=false;

    /**
     * 活动处理
     */
    private EventHandler<ActionEvent> actionEventEventHandler;
    /**
     * 快捷键处理
     */
    private ShortCutExecutorHolder shortCutExecutorHolder;

    public void registry(ScreenshotToolbarBox screenshotToolbarBox) {
        screenshotToolbarBox.registryButton(this);
    }

    public ScreenshotToolbarBoxButtonHolder addOption(Node node) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(node);
        return this;
    }

}
