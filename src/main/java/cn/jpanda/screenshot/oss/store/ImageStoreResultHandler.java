package cn.jpanda.screenshot.oss.store;

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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class ImageStoreResultHandler {
    private Log log;
    private Configuration configuration;

    public ImageStoreResultHandler(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
    }

    @Getter
    private ObservableList<ImageStoreResult> imageStoreResults = FXCollections.observableArrayList();

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
                if (!file.exists() && file.mkdirs() && file.createNewFile()) {
                    ImageIO.write(imageStoreResult.getImage().get(), "PNG", file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 图片保存失败，需要提示，此处采用弹窗提示，并提供再次保存的能力
            log.err("{0}", imageStoreResult.getException().get());
            // 临时保存图片
            showAlert(imageStoreResult);
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
                    Throwable exception = imageStoreResult.getException().get();
                    // 展示五十行日志
                    StackTraceElement[] traceElements = exception.getStackTrace();
                    StringBuilder stackTrace = new StringBuilder();
                    for (StackTraceElement traceElement : traceElements) {
                        stackTrace.append(traceElement.toString()).append("\r\n");
                    }
                    Platform.runLater(() -> {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setHeaderText(exception.getMessage());
                        info.setContentText(stackTrace.toString());
                        info.initModality(Modality.APPLICATION_MODAL);
                        info.setOnCloseRequest(event -> info.close());
                        info.showAndWait();
                    });

                }
            }
        });
    }
}
