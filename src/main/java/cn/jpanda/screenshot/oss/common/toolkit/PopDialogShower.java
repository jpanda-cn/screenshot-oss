package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/10 17:44
 */
public class PopDialogShower {
    public static void message(String message) {
        PopDialog.create().setHeader("参数错误").setContent(message).buttonTypes(new ButtonType("知道了")).showAndWait();
    }

    public static void message(String message, Window window) {
        PopDialog.create().setHeader("参数错误").setContent(message).buttonTypes(new ButtonType("知道了")).bindParent(window).showAndWait();
    }

    public static PopDialog exception(String header, String exception) {
        TextArea textArea = new TextArea();
        textArea.editableProperty().set(false);
        textArea.textProperty().setValue(exception);
        textArea.wrapTextProperty().set(true);
        return PopDialog.create().setHeader(header)
                .setContent(textArea)
                .buttonTypes(new ButtonType("知道了"))
                ;
    }
}
