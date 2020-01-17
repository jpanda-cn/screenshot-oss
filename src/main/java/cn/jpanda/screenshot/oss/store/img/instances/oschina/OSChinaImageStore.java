package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.*;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.OSChinaFileImageStoreConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * OSChina图片上传支持
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 9:45
 */
@ImgStore(name = OSChinaImageStore.NAME, type = ImageType.HAS_PATH, config = OSChinaFileImageStoreConfig.class)
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
        OSChainPersistence persistence = configuration.getPersistence(OSChainPersistence.class);
        String url = loadUrl(persistence.getBlogId());
        return upload(image, url);
    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window) {
        PopDialogShower.message("OSChina的图片上传功能不支持指定图片名称，重新上传获取到的图片访问地址并不一致，因此OSChina不支持重试功能", window);
        return false;
    }

    public String upload(BufferedImage image, String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            FileBody fileBody=new FileBody(new File("C:\\Users\\Suning\\Desktop\\chapter7_1_6.jpg"));
            ByteArrayBody byteArrayBody = new ByteArrayBody(os.toByteArray(), ContentType.create("image/png", Consts.UTF_8), "foo.png");
            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .setCharset(StandardCharsets.UTF_8)
                    .addPart("upload", fileBody)
                    .build();

            post.setEntity(entity);

            post.addHeader("Content-Type", "multipart/form-data");

            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(response.getStatusLine().getStatusCode());
                throw new UploadException(OSChinaExceptionType.UPLOAD_FAILED);
            }
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            OSChinaUploadResult oschinaRequstResult = new ObjectMapper().readValue(result, OSChinaUploadResult.class);
            return oschinaRequstResult.getUrl();
        } catch (Exception e) {
            ExceptionType exceptionType = OSChinaExceptionType.TIME_OUT;
            if (e instanceof UploadException) {
                exceptionType = ((UploadException) e).getExceptionType();
            }
            SimpleStringProperty path = new SimpleStringProperty(UUID.randomUUID().toString().concat(".png"));
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
        OSChainPersistence persistence = configuration.getPersistence(OSChainPersistence.class);
        return StringUtils.isNotEmpty(persistence.getBlogId());
    }

}
