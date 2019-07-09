package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import lombok.Getter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
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
                JsonConfig jsonConfig=new JsonConfig();
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
                    if (parent.exists()) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("图片保存失败");
            alert.setHeaderText(String.format("在使用【%s】方式保存图片时发生异常", imageStoreResult.getImageStore().get()));
            alert.setContentText(String.format("异常信息的简要内容为:%s", imageStoreResult.getException().get().getMessage()));
            alert.getButtonTypes().removeAll(ButtonType.OK);
            alert.getButtonTypes().addAll(new ButtonType("忽略", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("查看详细日志", ButtonBar.ButtonData.OK_DONE));
            alert.initStyle(StageStyle.UNDECORATED);
            alert.initModality(Modality.APPLICATION_MODAL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    // 获取异常对象
                    Platform.runLater(() -> {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("异常信息");
                        info.setHeaderText(imageStoreResult.getException().get().getMessage());
                        info.setContentText(imageStoreResult.getException().get().getDetails());
                        info.initModality(Modality.APPLICATION_MODAL);
                        info.setOnCloseRequest(event -> info.close());
                        info.showAndWait();
                    });

                }
            }
        });
    }
}
