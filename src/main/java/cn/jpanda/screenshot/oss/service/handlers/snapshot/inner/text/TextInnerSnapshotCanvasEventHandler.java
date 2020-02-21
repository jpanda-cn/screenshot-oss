package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.text;

import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.shape.TextRectangle;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * 文字编辑处理
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/5 13:30
 */
public class TextInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private TextRectangle text;
    private boolean onShowing = false;
    // 当前配置对象
    private TrayConfig config;
    private ChangeListener<Paint> colorChangeListener;

    public TextInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler, CanvasShortcutManager canvasShortcutManager) {
        super(canvasProperties, canvasDrawEventHandler, canvasShortcutManager);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.cursorProperty().set(Cursor.DEFAULT);
    }

    @Override
    protected void press(MouseEvent event) {
        clear();
        if (onShowing) {
            onShowing = false;
            canvasProperties.getCutPane().requestFocus();
            return;
        }
        initText(event);
        onShowing = true;
    }


    private void initText(MouseEvent event) {
        // 获取当前配置类
        config = canvasProperties.getTrayConfig(CutInnerType.TEXT, false);

        text = new TextRectangle(rectangle);
        // 需要手动初始化一下颜色
        colorChangeListener = (observable, oldValue, newValue) -> {
            text.getTextArea().setStyle(String.format("-fx-text-fill: %s", color2RGBA((Color) newValue)));
        };
        config.getStrokeColor().addListener(
                (observable, oldValue, newValue) -> text.getTextArea().setStyle(String.format("-fx-text-fill: %s", newValue))
        );
        text.getTextArea().fontProperty().set(config.getFont().getValue());

        config.getStrokeColor().addListener(colorChangeListener);
        // 字体
        text.getTextArea().fontProperty().bind(config.getFont());

        text.getTextArea().setStyle(String.format("-fx-text-fill: %s", color2RGBA(config.getStrokeColor().get())));

        // 放置文本框
        text.getExtBorder().xProperty().set(event.getSceneX());
        text.getExtBorder().yProperty().set(event.getSceneY());
        // 调整高度
        group = new Group(text);
        canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        canvasProperties.getCutPane().getChildren().addAll(group);
        text.getTextArea().requestFocus();
    }

    private void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            // 鼠标按下时，清理之前生成的矩形组的事件
            if (group != null) {
                group.setMouseTransparent(true);
            }
            // 移除绑定关系
            if (text != null) {
                text.getTextArea().editableProperty().setValue(false);
                text.getTextArea().fontProperty().unbind();
                text.getExtBorder().fillProperty().set(Color.TRANSPARENT);
                text.getExtBorder().visibleProperty().setValue(false);
                text.getTextArea().deselect();
            }
            if (config!=null&&colorChangeListener!=null){
            config.getStrokeColor().removeListener(colorChangeListener);

            }
            config = null;
        });
    }

    private String color2RGBA(Color color) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();
        double a = color.getOpacity();
        return String.format("rgba(%f,%f,%f,%f)", r * 255, g * 255, b * 255, a);
    }

}
