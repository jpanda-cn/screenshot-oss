package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import com.sun.javafx.webkit.WebConsoleListener;
import com.sun.webkit.dom.HTMLDocumentImpl;
import com.sun.webkit.network.CookieManager;
import javafx.application.Application;
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
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.CookieHandler;
import java.net.URI;
import java.util.*;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 14:27
 */
public class WebViewApp extends Application {
    Callable<Boolean, ButtonType> callable;
    CookieManager cookieManager = new CookieManager();

    @SneakyThrows
    public Parent createContent() {
        CookieHandler.setDefault(cookieManager);
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
        callable = new Callable<Boolean, ButtonType>() {
            @Override
            public Boolean apply(ButtonType buttonType) {
                Document document = webEngine.getDocument();
                Element element = document.getElementById("userSidebar");
                NodeList nodeList = element.getElementsByTagName("div");

                String userId = ((HTMLDocumentImpl) document).getElementsByClassName("current-user-avatar").item(0).getAttributes().getNamedItem("data-user-id").getTextContent();
                return true;
            }
        };

        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {

                System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });


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

    @Override
    public void start(Stage primaryStage) throws Exception {
        PopDialog
                .create()
                .setHeader("OSCHINA")
                .setContent(createContent())
                .buttonTypes(ButtonType.FINISH)
                .callback(callable)
                .showAndWait();


        Map<String, List<String>> cookies = CookieHandler.getDefault().get(new URI("https://www.oschina.net/"), new HashMap<>());
        String cookieValue = String.join(";", Optional.ofNullable(cookies.get("Cookie")).orElse(Collections.emptyList()));
        if (StringUtils.isEmpty(cookieValue)) {
            // 没有拿到Cookie,表示数据错误，弹窗提示
        }
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
