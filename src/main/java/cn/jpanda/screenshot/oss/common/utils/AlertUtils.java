package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;

public final class AlertUtils {
    public static Alert alert(Alert.AlertType type, String headMsg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(headMsg);
        alert.show();
        return alert;
    }

    public static Alert exception(ExceptionWrapper exceptionWrapper) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("异常信息");
        info.setHeaderText(exceptionWrapper.getMessage());
        DialogPane pane = info.getDialogPane();
        TextArea textArea = new TextArea();
        pane.contentProperty().setValue(textArea);
        textArea.textProperty().setValue(exceptionWrapper.getDetails());
        textArea.wrapTextProperty().set(true);
        textArea.editableProperty().set(false);
        info.initModality(Modality.APPLICATION_MODAL);
        return info;
    }

}
