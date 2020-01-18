package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import com.sun.webkit.network.CookieManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie2;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/18 12:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomCookieManager extends CookieHandler {
    private CookieManager webCookieManger;
    private java.net.CookieManager netCookieManger;

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        return webCookieManger.get(uri, requestHeaders);
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        webCookieManger.put(uri, responseHeaders);
        netCookieManger.put(uri, responseHeaders);
    }

    public BasicCookieStore get(URI uri) {
        List<HttpCookie> cookies = netCookieManger.getCookieStore().get(uri);
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        basicCookieStore.addCookies(cookies.stream().map(h -> new BasicClientCookie2(h.getName(), h.getValue()) {

            @Override
            public String getComment() {
                return h.getComment();
            }

            @Override
            public String getCommentURL() {
                return h.getCommentURL();
            }

            @Override
            public Date getExpiryDate() {
                return new Date(h.getMaxAge());
            }

            @Override
            public boolean isPersistent() {
                return false;
            }

            @Override
            public String getDomain() {
                return h.getDomain();
            }

            @Override
            public String getPath() {
                return h.getPath();
            }


            @Override
            public boolean isSecure() {
                return h.getSecure();
            }

            @Override
            public int getVersion() {
                return h.getVersion();
            }

        }).toArray(Cookie[]::new));

        return basicCookieStore;
    }

}
