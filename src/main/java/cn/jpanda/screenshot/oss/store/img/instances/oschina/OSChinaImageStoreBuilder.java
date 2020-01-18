package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.ImageStoreConfigBuilder;
import com.sun.webkit.dom.HTMLDocumentImpl;
import com.sun.webkit.network.CookieManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;

import java.net.CookieHandler;
import java.net.URI;
import java.util.*;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/18 11:51
 */
public class OSChinaImageStoreBuilder implements ImageStoreConfigBuilder {
    public static final String ACCESS_URL = "https://www.oschina.net/";


    private Configuration configuration;
    CustomCookieManager cookieManager;

    public OSChinaImageStoreBuilder(Configuration configuration) {
        this.configuration = configuration;
        cookieManager = new CustomCookieManager(new CookieManager(), new java.net.CookieManager());
    }

    @Override
    public Parent load() {
        return createContent();
    }

    @SneakyThrows
    public Parent createContent() {
        CookieHandler.setDefault(cookieManager);
        loadCookie();

        WebView webView = new WebView();

        final WebEngine webEngine = webView.getEngine();
        final String DEFAULT_URL = "https://www.oschina.net/home/login";
        webEngine.load(DEFAULT_URL);
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36");
        webEngine.setJavaScriptEnabled(true);

        final TextField locationField = new TextField(DEFAULT_URL);
        final ChangeListener<String> changeListener =
                (ObservableValue<? extends String> observable,
                 String oldValue, String newValue) -> {
                    locationField.setText(newValue);
                };

        webEngine.locationProperty().addListener(changeListener);

        EventHandler<ActionEvent> goAction = (ActionEvent event) -> {
            String url = locationField.getText();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://".concat(url);
            }
            webEngine.load(url);
        };

        locationField.setOnAction(goAction);
        Callable<Boolean, ButtonType> callable = buttonType -> {
            if (ButtonType.CANCEL.equals(buttonType)) {
                return true;
            }
            String uid = getUserId(webEngine.getDocument());
            String cookie = getCookie();
            if (StringUtils.isEmpty(uid)) {
                // 无法获取用户ID
                PopDialogShower.message("无法获取用户登录信息，请确认是否登录，如确定已登录，该问题可能是因为版本变更导致，请联系开发人员", webView.getScene().getWindow());
                return false;
            }
            if (StringUtils.isEmpty(cookie)) {
                PopDialogShower.message("无法获取Cookie，请联系开发人员", webView.getScene().getWindow());
                return false;
            }

            OSChainPersistence persistence = configuration.getPersistence(OSChainPersistence.class);
            persistence.setUid(uid);
            persistence.setCookie(cookie);
            persistence.setExpire(new Date().getTime() + 1000 * 60 * 60 * 24 * 365L);
            configuration.storePersistence(persistence);
            SimpleBooleanProperty updateCookie=configuration.getUniquePropertiesHolder(OSChinaCookieUpdateAfterBootstrapLoaderProcess.UPDATE_COOKIE_SHARD_PROPERTY_NAME);
            updateCookie.set(true);
            return true;
        };

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + OSChinaImageStore.NAME, callable);

        Button goButton = new Button("Go");
        goButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        goButton.setDefaultButton(true);
        goButton.setOnAction(goAction);

        // Layout logic
        HBox hBox = new HBox(5);
        hBox.getChildren().setAll(locationField, goButton);
        HBox.setHgrow(locationField, Priority.ALWAYS);

        final VBox vBox = new VBox(5);
        vBox.getChildren().setAll(hBox, webView);
        vBox.setPrefSize(1366, 768);
        VBox.setVgrow(webView, Priority.ALWAYS);

        return vBox;
    }

    @SneakyThrows
    private String getCookie() {
        return String.join(";", Optional.ofNullable(CookieHandler.getDefault().get(new URI(ACCESS_URL), new HashMap<>()).get("Cookie")).orElse(Collections.emptyList()));
    }

    private String getUserId(Document document) {
        HTMLCollection divs = ((HTMLDocumentImpl) document).getElementsByClassName("current-user-avatar");
        if (divs.getLength() < 1) {
            return null;
        }
        Node dataUserId = divs.item(0).getAttributes().getNamedItem("data-user-id");
        if (dataUserId == null) {
            return null;
        }
        return dataUserId.getTextContent();
    }

    @SneakyThrows
    private void loadCookie(){
        OSChainPersistence persistence = configuration.getPersistence(OSChainPersistence.class);
        String cookie=persistence.getCookie();
        if (StringUtils.isEmpty(cookie)){
            return;
        }
        String[]cookies=cookie.split(";");
        Map<String,List<String>> cookieMap=new HashMap<>();
        cookieMap.put("Set-Cookie",Arrays.asList(cookies));
        CookieHandler.getDefault().put(new URI(ACCESS_URL),cookieMap);
    }
}
