package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 13:32
 */
public class ShortcutHelperShower {

    private Map<KeyCode, String> codeNameMap = new HashMap<>();

    public static PopDialog show(List<ShortCutExecutorHolder> holders, Window parent) {


        ScrollPane body = new ScrollPane();
        body.prefHeight(300);
        body.setPrefViewportHeight(300);

        VBox vBox = new VBox();
        vBox.setPrefHeight(300);
        vBox.paddingProperty().set(new Insets(10, 30, 10, 10));
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color:  rgba(0,0,0,0.6)");
        holders.forEach(h -> {
            HBox content = new HBox();

            content.setStyle("-fx-background-color: transparent");
            content.setSpacing(25);
            Text description = new Text(h.getShortcut().getDescription());
            description.setFill(Color.WHITE);
            Text shortCut = new Text(getShortKey(h.getShortcut()));
            shortCut.setFill(Color.WHITE);
            content.getChildren().addAll(description, shortCut);
            BorderPane borderPane = new BorderPane();
            borderPane.setLeft(description);
            borderPane.setRight(shortCut);
            borderPane.setCenter(new Button());
            borderPane.getCenter().prefWidth(50);
            borderPane.getCenter().setStyle("-fx-background-color: transparent");
            BorderPane.setAlignment(description, Pos.CENTER_LEFT);
            BorderPane.setAlignment(shortCut,Pos.CENTER_RIGHT);
            shortCut.xProperty().bind(content.widthProperty().subtract(description.xProperty()).subtract(description.wrappingWidthProperty()).subtract(shortCut.wrappingWidthProperty()));
            HBox.setHgrow(description, Priority.ALWAYS);
            HBox.setHgrow(shortCut, Priority.ALWAYS);
            vBox.getChildren().add(borderPane);
        });
        body.setContent(vBox);
        Scene scene = new Scene(body);
        scene.setFill(Color.TRANSPARENT);

        body.layoutXProperty().set(0);
        body.layoutYProperty().set(0);
        body.fitToWidthProperty().set(false);
        body.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        body.pannableProperty().set(true);


        PopDialog popDialog = PopDialog.create().bindParent(parent).setHeader("快捷键列表");
        DialogPane dialogPane = popDialog.getDialogPane();
        dialogPane.setContent(body);

//        dialogPane.styleProperty().setValue("-fx-background-color: transparent");
        popDialog.buttonTypes(new ButtonType("知道了"));


        return popDialog;

    }

    private static String getShortKey(Shortcut shortcut) {
        StringBuilder sb = new StringBuilder();
        if (shortcut.getCtrl()) {
            sb.append("CTRL").append(" + ");
        }
        if (shortcut.getAlt()) {
            sb.append("ALT").append(" + ");
        }
        if (shortcut.getShift()) {
            sb.append("SHIFT").append(" + ");
        }
        Optional<KeyCode> keyCode = shortcut.getCodes().stream().findFirst();
        if (!keyCode.isPresent()) {
            throw new RuntimeException("快捷键必须包含普通按键");
        }
        sb.append(KeyCodeNameConvertor.get(keyCode.get(), keyCode.get().getName()));
        return sb.toString();
    }

}
