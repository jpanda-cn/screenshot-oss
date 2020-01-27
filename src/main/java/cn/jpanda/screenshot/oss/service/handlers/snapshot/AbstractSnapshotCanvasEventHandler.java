package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortCutExecutorHolder;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortcutMatch;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.event.EventTarget;
import javafx.scene.shape.Rectangle;

public abstract class AbstractSnapshotCanvasEventHandler extends SnapshotCanvasEventHandler {
    protected double x;
    protected double y;
    protected double ox;
    protected double oy;
    protected CanvasProperties canvasProperties;
    protected Rectangle rectangle;
    protected CanvasDrawEventHandler canvasDrawEventHandler;
    protected CanvasShortcutManager canvasShortcutManager;

    public AbstractSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler, CanvasShortcutManager canvasShortcutManager) {
        this.canvasProperties = canvasProperties;
        this.canvasDrawEventHandler = canvasDrawEventHandler;
        this.canvasShortcutManager = canvasShortcutManager;
        rectangle = canvasProperties.getCutRectangle();
        ox = rectangle.xProperty().get();
        ox = rectangle.yProperty().get();
    }

    protected CanvasShortcutManager getCanvasShortcutManager() {
        return canvasShortcutManager;

    }

    protected ShortcutMatch getShortcutMatch() {
        return canvasShortcutManager.getShortcutMatch();
    }

    protected void addShortCut(EventTarget target, Object type, ShortCutExecutorHolder holder) {
        canvasShortcutManager.add(target, type, holder);
    }

    protected void addCurrent(EventTarget target, ShortCutExecutorHolder holder) {
        addShortCut(target, canvasProperties.getConfiguration().getUniquePropertiesHolder(CutInnerType.class, null), holder);
    }

    protected void addGlobal(EventTarget target, ShortCutExecutorHolder holder) {
        addShortCut(target, null, holder);
    }

}
