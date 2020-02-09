package cn.jpanda.screenshot.oss.store.img.instances.oschina;

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
@ImgStore(name = OSChinaImageStore.NAME, type = ImageType.HAS_PATH, builder = OSChinaImageStoreBuilder.class,icon = "/images/stores/icons/oschina.png")
public class OSChinaImageStore extends AbstractConfigImageStore {

    public static final String NAME = "OS-CHINA";


    public OSChinaImageStore(Configuration configuration) {
        super(configuration);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String store(BufferedImage image) {
        OSChinaPersistence persistence = configuration.getPersistence(OSChinaPersistence.class);
        String url = loadUrl(persistence.getUid());
        return upload(image, url, persistence);
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        PopDialogShower.message("OSChina的图片上传功能不支持指定图片名称，重新上传获取到的图片访问地址并不一致，因此OSChina不支持重试功能", window);
        return false;
    }

    @SneakyThrows
    public String upload(BufferedImage image, String url, OSChinaPersistence persistence) {

        SimpleStringProperty path = new SimpleStringProperty(UUID.randomUUID().toString().concat(".png"));
        System.out.println(path.get());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                .setDefaultHeaders(Collections.singletonList(new BasicHeader("Cookie", persistence.getCookie()))).build();
             ByteArrayOutputStream os = new ByteArrayOutputStream();
        ) {
            ImageIO.write(image, "png", os);
            byte[] bytes = os.toByteArray();
            HttpPost post = new HttpPost(url);
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addBinaryBody("upload",os.toByteArray(),ContentType.DEFAULT_BINARY,path.get())
                    .build();
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code != 200) {
                throw new UploadException(OSChinaExceptionType.UPLOAD_FAILED);
            }
            OSChinaUploadResult oschinaRequstResult = new ObjectMapper().readValue(result, OSChinaUploadResult.class);
            return oschinaRequstResult.getUrl();
        } catch (Exception e) {
            ExceptionType exceptionType = OSChinaExceptionType.TIME_OUT;
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

    public String loadUrl(String id) {
        return String.format("https://my.oschina.net/u/%s/space/ckeditor_dialog_img_upload", id);
    }

    @Override
    public boolean canUse() {
        OSChinaPersistence persistence = configuration.getPersistence(OSChinaPersistence.class);
        return StringUtils.isNotEmpty(persistence.getUid());
    }

//    public void Test(){
//        Document document=new HTMLDocument()
//    }
}
