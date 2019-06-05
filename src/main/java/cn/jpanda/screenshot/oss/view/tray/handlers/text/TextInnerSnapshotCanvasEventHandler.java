package cn.jpanda.screenshot.oss.view.tray.handlers.text;

import cn.jpanda.screenshot.oss.service.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


/**
 * 文字编辑处理
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/5 13:30
 */
public class TextInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private TextField text;
    private Rectangle rec;

    public TextInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler) {
        super(canvasProperties, canvasDrawEventHandler);
    }

    @Override
    protected void move(MouseEvent event) {
        rectangle.cursorProperty().set(Cursor.DEFAULT);
    }

    @Override
    protected void press(MouseEvent event) {
        if (group != null) {
            group.setMouseTransparent(true);
            if (rec != null) {
                rec.setStroke(Color.TRANSPARENT);
            }
        }   // 展示文字的矩形
        text = new TextField();
        TrayConfig config = canvasProperties.getTrayConfig(CutInnerType.TEXT);
        text.fontProperty().set(config.getFont());


        // 绘制矩形
        rec = new Rectangle();
        rec.fillProperty().set(Color.TRANSPARENT);
        rec.xProperty().set(event.getScreenX());
        rec.yProperty().set(event.getScreenY());
        rec.strokeProperty().set(Color.RED);
        rec.widthProperty().bind(text.widthProperty());
        rec.heightProperty().bind(text.heightProperty());

        // 放置文本框
        text.setStyle("-fx-background-color: transparent");
//        text.opacityProperty().set(0.3);
        text.layoutXProperty().bind(rec.xProperty());
        text.layoutYProperty().bind(rec.yProperty());
        Text t2 = new Text();
        t2.fillProperty().set(config.getStrokeColor());
        t2.layoutXProperty().bind(rec.xProperty());
        t2.layoutYProperty().bind(rec.yProperty());
        t2.wrappingWidthProperty().bind(text.widthProperty());
        t2.textProperty().bind(text.textProperty());
        group = new Group(text, rec, t2);
        t2.toBack();
        text.toFront();
        canvasProperties.getCutPane().getChildren().addAll(group);
        text.requestFocus();


//        text.setStyle("color: " + color2RGBA(config.getStrokeColor()));
    }

    private String color2RGBA(Color color) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();
        double a = color.getOpacity();
        return String.format("rgba(%f,%f,%f,%f)", r, g, b, a);
    }
}
