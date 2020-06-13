package cn.jpanda.screenshot.oss.store.img.instances.sm;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.store.*;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.store.img.instances.oschina.OSChinaExceptionType;
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
import java.util.UUID;

/**
 * sm.ms 图床
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/20 15:33
 */
@ImgStore(name = SmMsCloudStore.NAME, icon = "/images/stores/icons/smms.png")
public class SmMsCloudStore extends AbstractConfigImageStore {
    public final static String NAME = "图床-SM";
    /**
     * sm.ms上传地址
     */
    private static final String url = "https://sm.ms/api/v2/upload?inajax=1";
    private Log log;

    public SmMsCloudStore(Configuration configuration) {
        super(configuration);
        log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public String store(BufferedImage image, String extensionName) {
        return upload(image, url, extensionName);
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        PopDialogShower.message("sm.ms的图片上传功能不支持指定图片名称，重新上传获取到的图片访问地址并不一致，因此sm.ms不支持重试功能", window);
        return false;
    }

    @SneakyThrows
    private String upload(BufferedImage image, java.lang.String url, String extendsName) {
        SimpleStringProperty path = new SimpleStringProperty(fileNameGenerator(extendsName));

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                .setDefaultHeaders(
                        Collections.singletonList(
                                new BasicHeader("x-requested-with", "XMLHttpRequest")
                        )
                )
                .build();
             ByteArrayOutputStream os = new ByteArrayOutputStream();
        ) {
            ImageIO.write(image, extendsName, os);
            HttpPost post = new HttpPost(url);
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addBinaryBody("smfile", os.toByteArray(), ContentType.DEFAULT_BINARY, path.get())
                    .addTextBody("file_id", "0")
                    .build();
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code != 200) {
                throw new UploadException(OSChinaExceptionType.UPLOAD_FAILED);
            }
            log.debug(result);
            SmMsUploadResult smMsUploadResult = new ObjectMapper().readValue(result, SmMsUploadResult.class);
            return smMsUploadResult.getData().getUrl();
        } catch (Exception e) {
            ExceptionType exceptionType = SmMsExceptionType.CANT_UPLOAD;
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
}
