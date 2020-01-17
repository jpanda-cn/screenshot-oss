package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 11:08
 */
public class OSChinaImageStoreTest {

    @Test
    @SneakyThrows
    public void upload() {
//        LogManager logManager = LogManager.getLogManager();
//        logManager.readConfiguration(getClass().getClassLoader().getResourceAsStream("logging.properties"));
        CloseableHttpClient client = HttpClients.createSystem();
        System.out.println(String.format("https://my.oschina.net/u/%s/space/ckeditor_dialog_img_upload", "3101282"));
        HttpPost post = new HttpPost("https://my.oschina.net/u/123/space/ckeditor_dialog_img_upload");

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            FileBody fileBody = new FileBody(new File("C:\\Users\\Suning\\Desktop\\chapter7_1_6.jpg"));
//            HttpEntity entity = MultipartEntityBuilder
//                    .create()
//                    .setCharset(StandardCharsets.UTF_8)
//                    .setContentType(ContentType.MULTIPART_FORM_DATA)
////                    .addPart("upload", new InputStreamBody(new FileInputStream("C:\\Users\\Suning\\Desktop\\empty.png"), "empty.png"))
////                    .addPart("upload",fileBody)
////                    .addBinaryBody("upload",new File("C:\\Users\\Suning\\Desktop\\empty.png"))
//                    .build();
//            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            post.addHeader("Accept", "*/*");
            post.addHeader("Accept-Encoding", "gzip, deflate, br");
            post.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,zh-TW;q=0.6");
            post.addHeader("Connection", "keep-alive");
            post.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundarySXBfv8XFRe5lvLjz");
//            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36");
            post.addHeader("cookie", "_user_behavior_=c240aa10-8196-44ee-be9c-97655632c66e; oscid=P978BV1nGdoUL3DrxEiEJpVBv3Dh%2BC%2FycZCzB3uRI9Ac2eVj1lXnV%2FWHKjfDb3rKmNl2fmDJaTiGaVbWD7%2B8IAOrlqKNfxGAJUmIvt81kPztEvy5b3EOtYKVFlZCLA5nUWJ3DfScLhwPWibyjy9FIFeb6xDMG5yiZsaeE3elXxINtb8QeK6VkA%3D%3D; Hm_lvt_a411c4d1664dd70048ee98afe7b28f0b=1578887000,1579173078,1579223546,1579225005; ckCsrfToken=jtECqD4wtt30p5dyCPhZaT2PqPkiCnE758lf16zA; Hm_lpvt_a411c4d1664dd70048ee98afe7b28f0b=1579232244");
            post.addHeader("origin", "chrome-extension://aejoelaoggembcahagimdiliamlcdmfm");
            post.addHeader("sec-fetch-mode", "cors");
            post.addHeader("sec-fetch-site", "cross-site");
            post.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36");


//            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            System.out.println(response.getStatusLine().getStatusCode());
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            OSChinaUploadResult oschinaRequstResult = new ObjectMapper().readValue(result, OSChinaUploadResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}