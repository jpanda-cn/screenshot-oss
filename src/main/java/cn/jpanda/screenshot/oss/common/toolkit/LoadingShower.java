package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/14 17:54
 */
public class LoadingShower {
    public static Stage createLoading(Window parent) {

        Stage loadingStage = new Stage(StageStyle.TRANSPARENT);

        loadingStage.initOwner(parent);
        loadingStage.initModality(Modality.APPLICATION_MODAL);


        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("  -fx-progress-color: RED ;");
        progressIndicator.setProgress(-1F);

        Label label = new Label("处理中, 请稍后...");
        label.setTextFill(Color.RED);
        label.setBackground(Background.EMPTY);


        VBox vBox = new VBox(progressIndicator, label);
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: transparent");
        AnchorPane pane = new AnchorPane(vBox);
        pane.setStyle("-fx-background-color:  rgba(0,0,0,0.3)");

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        loadingStage.setScene(scene);

        loadingStage.setX(parent.getX());
        loadingStage.setY(parent.getY());

        pane.layoutXProperty().set(0);
        pane.layoutYProperty().set(0);

        pane.prefWidthProperty().bind(parent.getScene().widthProperty());
        pane.prefHeightProperty().bind(pane.getScene().heightProperty());
        loadingStage.setWidth(parent.getWidth());
        loadingStage.setHeight(parent.getHeight());

        vBox.translateXProperty()
                .bind(pane.widthProperty().subtract(vBox.widthProperty())
                        .divide(2));

        vBox.translateYProperty()
                .bind(pane.heightProperty().subtract(vBox.heightProperty())
                        .divide(2));

        return loadingStage;

    }
    public static Stage createUploading(Window parent) {

        Stage loadingStage = new Stage(StageStyle.TRANSPARENT);

        loadingStage.initOwner(parent);
        loadingStage.initModality(Modality.APPLICATION_MODAL);


        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("  -fx-progress-color: WHITE ;");
        progressIndicator.setProgress(-1F);

        Label label = new Label("图片处理中, 请稍后...");
        label.setTextFill(Color.WHITE);
        label.setBackground(Background.EMPTY);


        VBox vBox = new VBox(progressIndicator, label);
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: transparent");
        AnchorPane pane = new AnchorPane(vBox);
        pane.setStyle("-fx-background-color:  rgba(0,0,0,0.6)");

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        loadingStage.setScene(scene);

        loadingStage.setX(parent.getX());
        loadingStage.setY(parent.getY());

        pane.layoutXProperty().set(0);
        pane.layoutYProperty().set(0);

        pane.prefWidthProperty().bind(parent.getScene().widthProperty());
        pane.prefHeightProperty().bind(pane.getScene().heightProperty());
        loadingStage.setWidth(parent.getWidth());
        loadingStage.setHeight(parent.getHeight());

        vBox.translateXProperty()
                .bind(pane.widthProperty().subtract(vBox.widthProperty())
                        .divide(2));

        vBox.translateYProperty()
                .bind(pane.heightProperty().subtract(vBox.heightProperty())
                        .divide(2));

        return loadingStage;

    }

}
