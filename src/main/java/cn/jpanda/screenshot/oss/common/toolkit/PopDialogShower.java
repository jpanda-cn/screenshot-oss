package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.scene.control.ButtonType;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/10 17:44
 */
public class PopDialogShower {
    public static void message(String message) {
        PopDialog.create().setHeader("参数错误").setContent(message).buttonTypes(new ButtonType("知道了")).showAndWait();
    }
}
