package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import sun.nio.cs.StandardCharsets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 11:08
 */
public class JianShuImageStoreTest {

    @Test
    @SneakyThrows
    public void upload() {
//        String boundary = "--------------" + UUID.randomUUID();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                .setDefaultHeaders(Arrays.asList(
                        new BasicHeader("Cookie", "_user_behavior_=c240aa10-8196-44ee-be9c-97655632c66e; _reg_key_=DNtrsFDKMRIcE5EZcgaD; _openid_key_=7123b5f2-bee2-4aba-8ef3-82e752336490; oscid=P978BV1nGdoUL3DrxEiEJpVBv3Dh%2BC%2FycZCzB3uRI9Ac2eVj1lXnV%2FWHKjfDb3rKmNl2fmDJaTiGaVbWD7%2B8IAOrlqKNfxGAJUmIvt81kPztEvy5b3EOtYKVFlZCLA5nUWJ3DfScLhwPWibyjy9FIFeb6xDMG5yiZsaeE3elXxINtb8QeK6VkA%3D%3D; Hm_lvt_a411c4d1664dd70048ee98afe7b28f0b=1578887000,1579173078,1579223546,1579225005; Hm_lpvt_a411c4d1664dd70048ee98afe7b28f0b=1579225016; ckCsrfToken=jtECqD4wtt30p5dyCPhZaT2PqPkiCnE758lf16zA")
//                ,new BasicHeader("Content-Type","multipart/form-data; boundary="+boundary)
                )).build();

        HttpPost post = new HttpPost("https://my.oschina.net/u/3101282/space/ckeditor_dialog_img_upload");

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            HttpEntity entity = MultipartEntityBuilder
                    .create()
                    .addPart(FormBodyPartBuilder.create("upload", new ByteArrayBody(new byte[]{1, 2, 3, 4, 5}, "empty.png")).build())
                    .build();

            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            System.out.println(response.getStatusLine().getStatusCode());
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
            OSChinaUploadResult oschinaRequstResult = new ObjectMapper().readValue(result, OSChinaUploadResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}