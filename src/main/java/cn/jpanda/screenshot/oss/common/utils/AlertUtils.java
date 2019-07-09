package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import javafx.scene.control.Alert;

public final class AlertUtils {
    public static Alert alert(Alert.AlertType type, String headMsg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(headMsg);
        alert.show();
        return alert;
    }

    public static Alert exception(ExceptionWrapper exceptionWrapper) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("异常信息");
        alert.setHeaderText(exceptionWrapper.getMessage());
        alert.setContentText(exceptionWrapper.getDetails());
        return alert;
    }
}
