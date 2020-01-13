package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.view.fail.FailListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.Getter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageStoreResultHandler {
    private Log log;
    private Configuration configuration;

    @Getter
    private ObservableList<ImageStoreResult> imageStoreResults = FXCollections.observableArrayList();

    public ImageStoreResultHandler(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
        init();
    }


    public void init() {
        ImageStoreResultPersistence imageStoreResultPersistence = configuration.getPersistence(ImageStoreResultPersistence.class);
        String json = imageStoreResultPersistence.getJson();
        if (StringUtils.isNotEmpty(json)) {
            JSONArray jsonArray = JSONArray.fromObject(json);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JsonConfig jsonConfig = new JsonConfig();
                ImageStoreResultWrapper imageStoreResultWrapper = (ImageStoreResultWrapper) JSONObject.toBean(jsonObject, ImageStoreResultWrapper.class);
                imageStoreResults.add(imageStoreResultWrapper.toImageStoreResult());
            }
        }
    }

    private String toJson() {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[]{"image"});
        return JSONArray.fromObject(imageStoreResults.stream().map(ImageStoreResultWrapper::new).collect(Collectors.toList()), jsonConfig).toString();
    }

    public void add(ImageStoreResult imageStoreResult) {
        if (imageStoreResult.getSuccess().get()) {
            // 如果保存成功，则移除对于图片的缓存，降低内存使用率
            imageStoreResult.setImage(null);
        }
        imageStoreResults.add(imageStoreResult);
        if (!imageStoreResult.getSuccess().get()) {
            // 保存图片
            String path = imageStoreResult.getPath().get();
            try {

                File file = Paths.get(path).toFile();
                if (!file.exists()) {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                }
                ImageIO.write(imageStoreResult.getImage().get(), "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 图片保存失败，需要提示，此处采用弹窗提示，并提供再次保存的能力
            log.err("{0}", imageStoreResult.getException().get());

            // 将其记录到失败列表
            ImageStoreResultPersistence configurationPersistence = configuration.getPersistence(ImageStoreResultPersistence.class);
            configurationPersistence.setJson(toJson());
            configuration.storePersistence(configurationPersistence);
            // 临时保存图片
            showAlert(imageStoreResult);
        }
    }

    public void remove(String path) {
        Optional<ImageStoreResult> imageStoreResult = imageStoreResults.stream().filter((i) -> i.getPath().get().equals(path)).findFirst();
        if (imageStoreResult.isPresent()) {
            imageStoreResults.remove(imageStoreResult.get());
            ImageStoreResultPersistence configurationPersistence = configuration.getPersistence(ImageStoreResultPersistence.class);
            configurationPersistence.setJson(toJson());
            configuration.storePersistence(configurationPersistence);
        }
    }

    public void showAlert(ImageStoreResult imageStoreResult) {
        Platform.runLater(() -> {
            if (configuration.getCutting().get() != null) {
                // 当前处于截图状态中
                // 当截图窗口关闭后，展示弹窗
                configuration.getCutting().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        showExceptionTips(imageStoreResult);
                    }
                });
            } else {
                showExceptionTips(imageStoreResult);
            }
        });
    }

    public void showExceptionTips(ImageStoreResult result) {


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
