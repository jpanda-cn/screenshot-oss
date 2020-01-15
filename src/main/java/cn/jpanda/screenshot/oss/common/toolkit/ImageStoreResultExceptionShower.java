package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.view.fail.FailListView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/14 17:56
 */
public class ImageStoreResultExceptionShower {

    public static void showExceptionTips(ImageStoreResult result, Configuration configuration) {


        ButtonType ignore = new ButtonType("忽略");
        ButtonType toHandler = new ButtonType("去处理");

        VBox body = new VBox();
        body.setSpacing(5);
        Label type = new Label(String.format("存储方式:【%s】", result.getImageStore().get()));
        Label reason = new Label(String.format("失败原因:【%s】", result.getException().get().getMessage()));
        Label description = new Label(String.format("失败描述:【%s】", result.getExceptionType().getDescription()));
        Label path = new Label(String.format("图片保存路径:【%s】", result.getPath().get()));

        TextArea textArea = new TextArea();
        textArea.editableProperty().set(false);
        textArea.textProperty().setValue(result.getException().get().getDetails());
        textArea.wrapTextProperty().set(true);
        body.getChildren().addAll(type, description, reason, path, textArea);
        PopDialog popDialog = PopDialog.create().setHeader("图片处理失败")
                .setContent(body)
                .addButtonClass(ignore,"button-light")
                .bindParent(configuration.getViewContext().getStage())
                .callback(buttonType -> {
                    if (toHandler.equals(buttonType)) {
                        // 跳转到失败任务列表
                        configuration.registryUniquePropertiesHolder(FailListView.IS_SHOWING, true);
                        PopDialog.create()
                                .setHeader("失败任务列表")
                                .setContent(configuration.getViewContext().getScene(FailListView.class, true, false).getRoot())
                                .bindParent(configuration.getViewContext().getStage())
                                .buttonTypes(ButtonType.CLOSE)
                                .callback(buttonType1 -> {
                                    configuration.registryUniquePropertiesHolder(FailListView.IS_SHOWING, false);
                                    return true;
                                })
                                .show();
                    }
                    return true;
                });
        if (configuration.getUniquePropertiesHolder(FailListView.IS_SHOWING, false)) {
            popDialog.buttonTypes(ignore);
        } else {
            popDialog.buttonTypes(ignore, toHandler);
        }
        popDialog.getDialogPane().getContent().setStyle("-fx-alignment: left;");
        popDialog.show();

    }
}
