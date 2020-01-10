package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/9 20:35
 */
public class PopDialogTest extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setScene(new Scene(new AnchorPane()));
        primaryStage.setWidth(10);
        primaryStage.show();

        HBox content = new HBox();
        Label main = new Label("GIT:");
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        Label description = new Label("需要进行相关参数配置才可使用");
        content.getChildren().addAll(main, description);
        PopDialog popDialog = PopDialog.create();

        popDialog.setHeader("提示").setContent(content).callback(a -> {
            if (a.equals(PopDialog.CONFIG)) {
                PopDialog.create().setHeader("提示").setContent("31312312").callback(b -> {
                    if (b.equals(PopDialog.CONFIG)) {
                        popDialog.close();
                        return true;
                    }
                    return false;
                }).showAndWait();
                return false;
            }
            return false;
        }).show();

    }
}