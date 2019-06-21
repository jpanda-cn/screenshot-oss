package cn.jpanda.screenshot.oss.store.img.instances.alioss;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.AliOssFileImageStoreConfig;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@ImgStore(name = "阿里OSS", config = AliOssFileImageStoreConfig.class)
public class AliOssImageStore extends AbstractConfigImageStore {

    public AliOssImageStore(Configuration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return "阿里OSS";
    }

    @Override
    @SneakyThrows
    public String store(BufferedImage image) {
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);
        String name = fileNameGenerator();
        if (aliOssPersistence.isAsync()) {
            new Thread(() -> {
                upload(image, aliOssPersistence, name);
            }).start();
        } else {
            upload(image, aliOssPersistence, name);
        }

        return String.format("%s://%s.%s/%s"
                , aliOssPersistence.getSchema()
                , aliOssPersistence.getBucket()
                , aliOssPersistence.getEndpoint()
                , name
        );
    }

    @SneakyThrows
    public void upload(BufferedImage image, AliOssPersistence aliOssPersistence, String name) {

        OSSClient ossClient = new OSSClient(aliOssPersistence.getEndpoint(), aliOssPersistence.getAccessKeyId(), aliOssPersistence.getAccessKeySecret());
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            PutObjectResult result = ossClient.putObject(aliOssPersistence.getBucket(), name, new ByteArrayInputStream(os.toByteArray()));
        } finally {
            ossClient.shutdown();
        }
    }

    protected String fileNameGenerator() {
        return UUID.randomUUID().toString() + ".png";
    }
}
