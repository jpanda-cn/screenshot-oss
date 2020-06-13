package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.store.*;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.store.img.instances.oschina.OSChinaExceptionType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

/**
 * sm.ms 图床
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/20 15:33
 */
@ImgStore(name = UmogCloudStore.NAME, builder = UmogStoreBuilder.class, icon = "/images/stores/icons/umog.png")
public class UmogCloudStore extends AbstractConfigImageStore {
    public final static String NAME = "图床-umog";
    /**
     * sm.ms上传地址
     */
    private Log log;

    public UmogCloudStore(Configuration configuration) {
        super(configuration);
        log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public String store(BufferedImage image, String extensionName) {
        UmogPersistence umogPersistence = configuration.getPersistence(UmogPersistence.class);

        return upload(image, getUrl(umogPersistence.getType()), extensionName);
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        PopDialogShower.message("sm.ms的图片上传功能不支持指定图片名称，重新上传获取到的图片访问地址并不一致，因此sm.ms不支持重试功能", window);
        return false;
    }

    @SneakyThrows
    private String upload(BufferedImage image, String url, String extendsName) {
        SimpleStringProperty path = new SimpleStringProperty(fileNameGenerator(extendsName));

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                .setDefaultHeaders(
                        Collections.singletonList(
                                new BasicHeader("Cookie", "PHPSESSID=raer98dqc4qfefloodffbf47fa;")
                        )
                )
                .build();
             ByteArrayOutputStream os = new ByteArrayOutputStream();
        ) {
            ImageIO.write(image, extendsName, os);
            HttpPost post = new HttpPost(url);
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addTextBody("file", "multipart")
                    .addBinaryBody("Filedata", os.toByteArray(), ContentType.DEFAULT_BINARY, path.get())
                    .build();
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            log.debug(result);
            if (code != 200) {
                throw new UploadException(OSChinaExceptionType.UPLOAD_FAILED);
            }
            UmogUploadResult umogUploadResult = getObjectMapper().readValue(result, UmogUploadResult.class);
            return umogUploadResult.getImgurl();
        } catch (Exception e) {
            ExceptionType exceptionType = UmogExceptionType.CANT_UPLOAD;
            if (e instanceof UploadException) {
                exceptionType = ((UploadException) e).getExceptionType();
            }

            configuration.getUniqueBean(ImageStoreResultHandler.class).add(ImageStoreResult
                    .builder()
                    .image(new SimpleObjectProperty<>(image))
                    .imageStore(new SimpleStringProperty(NAME))
                    .path(path)
                    .success(new SimpleBooleanProperty(false))
                    .exception(new SimpleObjectProperty<>(new ExceptionWrapper(e)))
                    .exceptionType(exceptionType)
                    .build());
            return path.get();
        }

    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    private String getUrl(String t) {
        return EFigureBed.valueOf(t).getUrl();
    }
}
