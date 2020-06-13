package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.common.toolkit.ImageStoreResultExceptionShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
                ImageIO.write(imageStoreResult.getImage().get(), getFileSuffix(path), file);
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
            if (configuration.getCutting().get()) {
                // 当前处于截图状态中
                // 当截图窗口关闭后，展示弹窗
                configuration.getCutting().addListener(new ShowMessageChangeListener(imageStoreResult));
            } else {
                showExceptionTips(imageStoreResult);
            }
        });
    }

    public void showExceptionTips(ImageStoreResult result) {

        ImageStoreResultExceptionShower.showExceptionTips(result, configuration);

    }

    public class ShowMessageChangeListener implements ChangeListener<Boolean> {
        private ImageStoreResult imageStoreResult;

        public ShowMessageChangeListener(ImageStoreResult imageStoreResult) {
            this.imageStoreResult = imageStoreResult;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue == null) {
                showExceptionTips(imageStoreResult);
                configuration.getCutting().removeListener(this);
            }
        }
    }

    public String getFileSuffix(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

}
