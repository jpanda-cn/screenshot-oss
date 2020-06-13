package cn.jpanda.screenshot.oss.store.img.instances.alioss;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.AliOssFileImageStoreConfig;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@ImgStore(name = AliOssImageStore.NAME, config = AliOssFileImageStoreConfig.class, icon = "/images/stores/icons/alioss.png")
public class AliOssImageStore extends AbstractConfigImageStore {
    public static final String NAME = "阿里OSS";

    public AliOssImageStore(Configuration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return NAME;
    }



    @Override
    public String store(BufferedImage image, String extensionName) {
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);
        String name = fileNameGenerator(extensionName);
        if (aliOssPersistence.isAsync()) {
            new Thread(() -> {
                upload(image, aliOssPersistence, name, extensionName);
            }).start();
        } else {
            upload(image, aliOssPersistence, name, extensionName);
        }

        return String.format("%s/%s"
                , aliOssPersistence.getAccessUrl()
                , name
        );
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(Paths.get(imageStoreResultWrapper.getPath()).toFile());
        } catch (IOException e) {
            return false;
        }
        String path = imageStoreResultWrapper.getPath();
        String name = path.substring((int) MathUtils.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1);
        return upload(bufferedImage, configuration.getPersistence(AliOssPersistence.class), name, getFileSuffix(path));
    }

    public boolean upload(BufferedImage image, AliOssPersistence aliOssPersistence, String name, String extensionName) {

        OSSClient ossClient = new OSSClient(aliOssPersistence.getEndpoint(), aliOssPersistence.getAccessKeyId(), aliOssPersistence.getAccessKeySecret());

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, extensionName, os);
            PutObjectResult result = ossClient.putObject(aliOssPersistence.getBucket(), name, new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception e) {
            configuration.getUniqueBean(ImageStoreResultHandler.class).add(ImageStoreResult
                    .builder()
                    .image(new SimpleObjectProperty<>(image))
                    .imageStore(new SimpleStringProperty(NAME))
                    .path(new SimpleStringProperty(name))
                    .success(new SimpleBooleanProperty(false))
                    .exception(new SimpleObjectProperty<>(new ExceptionWrapper(e)))
                    .exceptionType(AliOSSExceptionType.CANT_SAVE)
                    .build());
            return false;
        } finally {
            ossClient.shutdown();
        }
        return true;
    }

    @Override
    public boolean canUse() {
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);
        return StringUtils.isNotEmpty(aliOssPersistence.getEndpoint())
                && StringUtils.isNotEmpty(aliOssPersistence.getBucket())
                && StringUtils.isNotEmpty(aliOssPersistence.getAccessKeyId())
                && StringUtils.isNotEmpty(aliOssPersistence.getAccessKeySecret())
                && StringUtils.isNotEmpty(aliOssPersistence.getAccessUrl());
    }


}
