package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/14 9:45
 */
public class LoadingDialogTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("  -fx-progress-color: RED ;");
        progressIndicator.setProgress(-1F);

        Label label = new Label("处理中, 请稍后...");
        label.setTextFill(Color.RED);
        label.setBackground(Background.EMPTY);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: transparent");
        vBox.getChildren().addAll(progressIndicator, label);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(vBox);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);


        primaryStage.show();
    }
}