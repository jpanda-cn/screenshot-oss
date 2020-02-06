package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StageTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(new AnchorPane(new Button("12"))));
//        primaryStage.show();
//        primaryStage.setIconified(true);
        PopDialog dialog=  PopDialog.create().bindParent(primaryStage);
        dialog.initModality(Modality.NONE);
        dialog.showAndWait();
    }
}
