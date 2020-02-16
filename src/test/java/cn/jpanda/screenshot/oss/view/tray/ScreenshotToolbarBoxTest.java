package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.core.Configuration;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.Test;

public class ScreenshotToolbarBoxTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(Color.RED);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setHeight(200);
//
//
        VBox toolbar = new ScreenshotToolbarBox(new Configuration());
        primaryStage.setScene(new Scene(toolbar));
        primaryStage.show();

//        sec(primaryStage);
//        thi(primaryStage);


//


    }
    @Test
    public void printColor(){
        System.out.println(coverColor(Color.rgb(255,222,123,0)));
        System.out.println(coverColor(Color.BLUE));
        System.out.println(coverColor(Color.web("#EFFE66")));
    }
    private String coverColor(Color color) {
        return String.format("#%s%s%s", toHexString(color.getRed()), toHexString(color.getGreen()), toHexString(color.getBlue()));
    }

    private String toHexString(double d) {
        return Integer.toHexString(Math.toIntExact(Math.round(d * 255)));
    }
    private void sec(Stage primaryStage) {

        Group svg = new Group(
                createPath("M0,0h100v100h-100z"),
                createPath("M20,20h60v60h-60z")
        );

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Group group = new Group();
        group.getChildren().add(new AnchorPane(btn));
        group.getStylesheets().add("/css/screenshotToolbarBox.css");

        primaryStage.setScene(new Scene(group));
        primaryStage.show();
    }

    private void thi(Stage primaryStage) {

        HBox group = new HBox();
        group.getStylesheets().add("/css/screenshotToolbarBox.css");
        group.getChildren().addAll(
                // 拖拽
                createButton(SVGPathHolder.DRAG),
                // 圆形
                createButton(SVGPathHolder.ROUND),
                // 矩形
                createButton(SVGPathHolder.RECTANGLE),
                // 箭头
                createButton(SVGPathHolder.ARROW),
                // 画笔
                createButton(SVGPathHolder.PEN),
                // 文字
                createButton(SVGPathHolder.TEXT),
                // 马赛克
                createButton(SVGPathHolder.MOSAIC),
                // 取色器
                createButton(SVGPathHolder.COLOR_PICKER),
                // 图钉
                createButton(SVGPathHolder.DRAWING_PIN),
                // 设置
                createButton(SVGPathHolder.SETTING),
                // 上传
                createButton(SVGPathHolder.UPLOAD),
                // 关闭
                createButton(SVGPathHolder.CLOSE),
                // 保存
                createButton(SVGPathHolder.SAVE)
        );

        primaryStage.setScene(new Scene(group));
        primaryStage.show();
    }

    private Button createButton(SVGPathHolder s) {

//        SVGPath path = createPath(s.getPath());
        SVGPath path =   s.to(Color.WHITE);;
        path.getStyleClass().add("svg");
        Group svg = new Group(path);
        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return btn;
    }

    private SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
//        path.setStyle("-fill:" + fill + ";-hover-fill:" + hoverFill + ';');
        return path;
    }
}