package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;

/**
 * 弹窗
 */
public class PopDialog extends Dialog<ButtonType> {

    private SimpleObjectProperty<Pane> header=new SimpleObjectProperty<>(new HBox());
    private SimpleObjectProperty<Pane> content=new SimpleObjectProperty<>(new AnchorPane());
    {
        header.addListener((observable, oldValue, newValue) -> {
            newValue.getStyleClass().addAll("header-panel");
            getDialogPane().setHeader(EventHelper.addDrag(newValue));
        });
        content.addListener((observable, oldValue, newValue) -> {
            newValue.getStyleClass().addAll("content");
            getDialogPane().setContent(newValue);
        });
        initStyle(StageStyle.TRANSPARENT);
        getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dialog.css").toExternalForm());
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.OK);
    }

    public static PopDialog create(){
        return new PopDialog();
    }

    private PopDialog() {

    }

    public PopDialog setHeader(String text){
       return setHeader(new HBox(new Label(text)));
    }
    public PopDialog setHeader(Pane h){
        header.set(h);
        return this;
    }

    public PopDialog setContent(String content){
        return setContent(new AnchorPane(new Label(content)));
    }
    public PopDialog setContent(Pane h){
        content.set(h);
        return this;
    }
}
