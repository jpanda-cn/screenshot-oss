package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.view.main.IconLabel;
import cn.jpanda.screenshot.oss.view.main.IconListCell;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;


public class ComboxTest  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ComboBox<IconLabel> comboBox=new ComboBox<>();
        comboBox.getStylesheets().add("/css/default.css");
        comboBox.setCellFactory(c -> new IconListCell());
        comboBox.getItems().addAll(IconLabel.builder()
                .icon("/images/stores/icons/oschina.png")
                .text("oschina")
                .build());
        comboBox.setButtonCell(new IconListCell());
        primaryStage.setScene(new Scene(comboBox));
        primaryStage.show();

    }
}
