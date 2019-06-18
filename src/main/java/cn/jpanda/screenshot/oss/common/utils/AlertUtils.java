package cn.jpanda.screenshot.oss.common.utils;

import javafx.scene.control.Alert;

public final class AlertUtils {
    public static Alert alert(Alert.AlertType type, String headMsg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(headMsg);
        alert.show();
        return alert;
    }
}
