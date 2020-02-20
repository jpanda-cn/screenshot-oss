package cn.jpanda.screenshot.oss.store.img.instances.qiniu;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.ExceptionWrapper;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.QiNiuCloudFileImageStoreConfig;
import com.aliyun.oss.OSSClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
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

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/10 11:35
 */
@ImgStore(name = QiNiuOssCloudStore.NAME, config = QiNiuCloudFileImageStoreConfig.class, icon = "/images/stores/icons/qiniu.png")
public class QiNiuOssCloudStore extends AbstractConfigImageStore {

    public final static String NAME = "七牛";

    public QiNiuOssCloudStore(Configuration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    @SneakyThrows
    public String store(BufferedImage image) {
        QiNiuOssPersistence qiNiuOssPersistence = configuration.getPersistence(QiNiuOssPersistence.class);
        String name = fileNameGenerator();
        if (qiNiuOssPersistence.isAsync()) {
            new Thread(() -> {
                upload(image, qiNiuOssPersistence, name);
            }).start();
        } else {
            upload(image, qiNiuOssPersistence, name);
        }

        return String.format("%s/%s"
                , qiNiuOssPersistence.getAccessUrl()
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
        return upload(bufferedImage, configuration.getPersistence(QiNiuOssPersistence.class), name);
    }

    public boolean upload(BufferedImage image, QiNiuOssPersistence qiNiuOssPersistence, String name) {

        OSSClient ossClient = new OSSClient(qiNiuOssPersistence.getEndpoint(), qiNiuOssPersistence.getAccessKeyId(), qiNiuOssPersistence.getAccessKeySecret());

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(qiNiuOssPersistence.getEndpoint(), "<REGION>");
        AWSCredentials awsCredentials = new BasicAWSCredentials(qiNiuOssPersistence.getAccessKeyId(), qiNiuOssPersistence.getAccessKeySecret());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonS3 s3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(true)
                .build();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpg");
            PutObjectResult result = s3.putObject(qiNiuOssPersistence.getBucket(), name, new ByteArrayInputStream(os.toByteArray()), objectMetadata);
        } catch (Exception e) {
            e.printStackTrace();
            configuration.getUniqueBean(ImageStoreResultHandler.class).add(ImageStoreResult
                    .builder()
                    .image(new SimpleObjectProperty<>(image))
                    .imageStore(new SimpleStringProperty(NAME))
                    .path(new SimpleStringProperty(name))
                    .success(new SimpleBooleanProperty(false))
                    .exception(new SimpleObjectProperty<>(new ExceptionWrapper(e)))
                    .exceptionType(QiNiuExceptionType.CANT_SAVE)
                    .build());
            return false;
        } finally {
            ossClient.shutdown();
        }
        return true;
    }

    protected String fileNameGenerator() {
        return UUID.randomUUID().toString() + ".png";
    }

    @Override
    public boolean canUse() {
        QiNiuOssPersistence qiNiuOssPersistence = configuration.getPersistence(QiNiuOssPersistence.class);
        return StringUtils.isNotEmpty(qiNiuOssPersistence.getEndpoint())
                && StringUtils.isNotEmpty(qiNiuOssPersistence.getBucket())
                && StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessKeyId())
                && StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessKeySecret())
                && StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessUrl());
    }

}
