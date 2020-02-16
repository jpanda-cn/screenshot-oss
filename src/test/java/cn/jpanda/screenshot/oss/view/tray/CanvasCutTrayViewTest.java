package cn.jpanda.screenshot.oss.view.tray;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CanvasCutTrayViewTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        final Color defaultColor = Color.WHITE;
        String c="-fx-background-color: rgba(255, 255, 255, 0);";
        // 工具栏
        VBox toolbar = new VBox();
        toolbar.setStyle(c);
        // 按钮组
        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.setPadding(new Insets(5,10,5,10));
        buttons.setStyle("-fx-background-color: BLACK;");
        // 选项组
        HBox options = new HBox();
        toolbar.getChildren().addAll(buttons, options);
        // 放置按钮
        List<Button> bts = Arrays.stream(SVGPathHolder.values()).map((s) -> {
            SVGPath path = s.to(defaultColor);
            Bounds bounds = path.getBoundsInParent();
            double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
            path.setScaleX(scale);
            path.setScaleY(scale);
            Button btn = new Button();
            btn.setGraphic(path);
            btn.setMaxSize(30, 30);
            btn.setMinSize(30, 30);
            btn.setLayoutX(0);
            btn.setLayoutY(0);
            btn.setStyle("-fx-fill: black;\n" +
                    "    -fx-content-display: graphic-only;\n" +
                    "    -fx-background-color: rgba(0, 0, 0);\n" +
                    "    -fx-cursor: hand;");
            btn.getStyleClass().add("close-button");
            return btn;
        }).collect(Collectors.toList());

        buttons.getChildren().addAll(bts);
        primaryStage.setScene(new Scene(toolbar));
        primaryStage.show();
    }


}