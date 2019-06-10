package cn.jpanda.screenshot.oss.view.tray.handlers.text;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.handlers.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import cn.jpanda.screenshot.oss.view.tray.handlers.rectangle.DragRectangleEventHandler;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


/**
 * 文字编辑处理
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/5 13:30
 */
public class TextInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private TextRectangle text;
    private Rectangle dragRec;

    public TextInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.cursorProperty().set(Cursor.DEFAULT);
    }

    @Override
    protected void press(MouseEvent event) {
        clear();
        initText(event);
        //  在外部添加一个矩形框
        dragRec = ShapeCovertHelper.toRectangle(text);
        dragRec.fillProperty().set(Color.TRANSPARENT);
        dragRec.strokeProperty().set(Color.RED);
        // 宽度+10 ，前5后5用于移动
//        dragRec.xProperty().addListener((observable, oldValue, newValue) -> {

//            dragRec.maxWidth(rectangle.widthProperty().add(rectangle.xProperty()).subtract(newValue.doubleValue()).get());
//        });
//        dragRec.yProperty().addListener((observable, oldValue, newValue) -> dragRec.maxHeight(rectangle.heightProperty().add(rectangle.yProperty()).subtract(newValue.doubleValue()).get()));
        dragRec.widthProperty().bind(text.widthProperty().add(10));
        dragRec.heightProperty().bind(text.heightProperty().add(10));

        text.layoutXProperty().bind(dragRec.xProperty().add(5));
        text.layoutYProperty().bind(dragRec.yProperty().add(5));
//        text.maxWidthProperty().bind(rectangle.widthProperty().add(rectangle.xProperty()).subtract(text.layoutXProperty()).subtract(5));
//        text.maxHeightProperty().bind(rectangle.heightProperty().add(rectangle.yProperty()).subtract(text.layoutYProperty()).subtract(5));
        // 调整初始高度
        group.getChildren().addAll(dragRec);
        dragRec.toBack();
        // 添加一个拖动事件
        dragRec.addEventFilter(MouseEvent.ANY, new DragRectangleEventHandler(dragRec, rectangle, null));
    }


    private void initText(MouseEvent event) {
        // 配置类
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.TEXT);

        text = new TextRectangle(rectangle);
        text.getStylesheets().add("/css/text-area-transparent.css");

        // 放置文本框
        text.layoutXProperty().set(event.getScreenX());
        text.layoutYProperty().set(event.getScreenY());
        // 调整高度
        group = new Group(text);
        canvasProperties.getCutPane().getChildren().addAll(group);
        config.getStrokeColor().addListener((ChangeListener<Paint>) (observable, oldValue, newValue) -> {
            String rgb = color2RGBA((Color) newValue);
            text.getTextArea().setStyle(String.format("-fx-text-fill: %s", rgb));
        });
        text.getTextArea().fontProperty().bind(config.getFont());
        text.getTextArea().requestFocus();
    }

    private void clear() {
        if (group != null) {
            group.setMouseTransparent(true);
        }
        // 移除绑定关系
        if (text != null) {
            text.getTextArea().fontProperty().unbind();
//            text.getLabel().fontProperty().unbind();
        }
        if (dragRec != null) {
            dragRec.strokeProperty().set(Color.TRANSPARENT);
            dragRec.visibleProperty().setValue(false);
        }
    }

    private String color2RGBA(Color color) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();
        double a = color.getOpacity();
        return String.format("rgba(%f,%f,%f,%f)", r * 255, g * 255, b * 255, a);
    }

}
