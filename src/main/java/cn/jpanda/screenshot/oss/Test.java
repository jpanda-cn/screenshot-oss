package cn.jpanda.screenshot.oss;

import cn.jpanda.screenshot.oss.common.toolkit.EventHelper;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.getDialogPane().getScene();
        alert.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("提示");
        EventHelper.addDrag(alert.getDialogPane());
        alert.setContentText("We override the style classes of the dialog");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.show();

        TextInputDialog textInputDialog=new TextInputDialog();
        textInputDialog.initStyle(StageStyle.TRANSPARENT);
        HBox h=new HBox();
        h.getStyleClass().addAll("header-panel");
        h.getChildren().addAll(new Label("提示"));
        textInputDialog.getDialogPane().setHeader(EventHelper.addDrag(h));
//        textInputDialog.getDialogPane().setHeaderText("提示");
//        EventHelper.addDrag(textInputDialog.getDialogPane().getHeader());
        textInputDialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dialog.css").toExternalForm());
        textInputDialog.show();

        PopDialog.create().setHeader("提示").setContent("测试").show();
    }
}
