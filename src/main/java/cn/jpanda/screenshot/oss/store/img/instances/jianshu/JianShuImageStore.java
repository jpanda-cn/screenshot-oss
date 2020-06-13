package cn.jpanda.screenshot.oss.store.img.instances.jianshu;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.*;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
 * OSChina图片上传支持
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 9:45
 */
@ImgStore(name = JianShuImageStore.NAME, type = ImageType.HAS_PATH, builder = JianShuStoreBuilder.class, icon = "/images/stores/icons/jianshu.png")
public class JianShuImageStore extends AbstractConfigImageStore {

    public static final String NAME = "简书";


    public JianShuImageStore(Configuration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public String store(BufferedImage image, String extensionName) {
        JianShuPersistence persistence = configuration.getPersistence(JianShuPersistence.class);
        return upload(image, persistence, extensionName);
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        PopDialogShower.message("OSChina的图片上传功能不支持指定图片名称，重新上传获取到的图片访问地址并不一致，因此OSChina不支持重试功能", window);
        return false;
    }

    @SneakyThrows
    public String upload(BufferedImage image, JianShuPersistence persistence, String extendsName) {


        SimpleStringProperty path = new SimpleStringProperty(UUID.randomUUID().toString().concat(".png"));
        System.out.println(path.get());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                .setDefaultHeaders(Collections.singletonList(new BasicHeader("Cookie", persistence.getCookie()))).build();
             ByteArrayOutputStream os = new ByteArrayOutputStream();
        ) {

            // 获取token
            HttpGet get = new HttpGet(String.format("https://www.jianshu.com/upload_images/token.json?filename=%s", path.get()));
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new UploadException(JianShuExceptionType.TOKEN_FAIL);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JIanShuTokenResult jIanShuTokenResult = objectMapper.readValue(EntityUtils.toString(response.getEntity()), JIanShuTokenResult.class);
            String token = jIanShuTokenResult.getToken();
            String key = jIanShuTokenResult.getKey();

            // 上传图片
            ImageIO.write(image, extendsName, os);
            byte[] bytes = os.toByteArray();
            HttpPost post = new HttpPost("https://upload.qiniup.com/");
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addTextBody("token", token)
                    .addTextBody("key", key)
                    .addTextBody("x:protocol", "https")
                    .addBinaryBody("file", os.toByteArray(), ContentType.DEFAULT_BINARY, path.get())
                    .build();
            post.setEntity(entity);

            HttpResponse upRes = httpClient.execute(post);
            int code = upRes.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(upRes.getEntity());
            if (code != 200) {
                throw new UploadException(JianShuExceptionType.UPLOAD_FAILED);
            }
            JianShuUploadResult uploadResult = objectMapper.readValue(result, JianShuUploadResult.class);
            return uploadResult.getUrl();
        } catch (Exception e) {
            ExceptionType exceptionType = JianShuExceptionType.TIME_OUT;
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

    @Override
    public boolean canUse() {
        JianShuPersistence persistence = configuration.getPersistence(JianShuPersistence.class);
        return StringUtils.isNotEmpty(persistence.getCookie());
    }

}
