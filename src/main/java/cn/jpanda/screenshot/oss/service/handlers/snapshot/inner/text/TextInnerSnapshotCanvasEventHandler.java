package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.text;

import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
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

    public TextInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.cursorProperty().set(Cursor.DEFAULT);
    }

    @Override
    protected void press(MouseEvent event) {
        if (onShowing) {
            clear();
            return;
        }
        initText(event);
        onShowing = true;
    }


    private void initText(MouseEvent event) {
        // 配置类
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.TEXT);
        text = new TextRectangle(rectangle);
        // 需要手动初始化一下颜色
        text.getTextArea().setStyle(String.format("-fx-text-fill: %s", color2RGBA(config.getStrokeColor().get())));
        text.getTextArea().fontProperty().set(config.getFont().getValue());

        // 绑定
        config.getStrokeColor().addListener((ChangeListener<Paint>) (observable, oldValue, newValue) -> {
            text.getTextArea().setStyle(String.format("-fx-text-fill: %s", color2RGBA((Color) newValue)));
        });
        text.getTextArea().fontProperty().bind(config.getFont());


        // 放置文本框
        text.getExtBorder().xProperty().set(event.getSceneX());
        text.getExtBorder().yProperty().set(event.getSceneY());
        // 调整高度
        group = new Group(text);
        canvasProperties.getCutPane().getChildren().addAll(group);
        text.getTextArea().requestFocus();
        text.getTextArea().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                clear();
            }
        });
    }

    private void clear() {
        canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
            // 鼠标按下时，清理之前生成的矩形组的事件
            if (group != null) {
                group.setMouseTransparent(true);
                canvasProperties.putGroup(group);
            }
            // 移除绑定关系
            if (text != null) {
                text.getTextArea().editableProperty().setValue(false);
                text.getTextArea().fontProperty().unbind();
                text.getExtBorder().fillProperty().set(Color.TRANSPARENT);
                text.getExtBorder().visibleProperty().setValue(false);
            }
            onShowing = false;
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
