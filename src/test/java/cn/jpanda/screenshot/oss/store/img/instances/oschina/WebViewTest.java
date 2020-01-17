package cn.jpanda.screenshot.oss.store.img.instances.oschina; /**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 13:49
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.CookieHandler;
import java.net.CookieManager;

public class WebViewTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webView.setPrefWidth(1024);
        webView.setPrefHeight(768);
        WebEngine webEngine = webView.getEngine();

        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36");
        webEngine.setJavaScriptEnabled(true);
        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                primaryStage.setTitle(newValue);
            }
        });

        webEngine.load("https://www.oschina.net/home/login");
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(webView);
        primaryStage.setScene(new Scene(webView));
        primaryStage.show();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

    }
}
