package cn.jpanda.screenshot.oss.store.img.instances.jianshu;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class JianShuCookieUpdateAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {

    public static final String UPDATE_COOKIE_SHARD_PROPERTY_NAME= JianShuCookieUpdateAfterBootstrapLoaderProcess.class.getCanonicalName()+"-"+"UPDATE_COOKIE_SHARD_PROPERTY_NAME";

    /**
     * 刷新OSCHINA cookie的时间 间隔
     */
    public static final long TIME_DELAY=1000*60*60*2;

    private Configuration configuration;
    private SimpleBooleanProperty updateCookie=new SimpleBooleanProperty(false);
    private Log log;

    public JianShuCookieUpdateAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
        configuration.registryUniquePropertiesHolder(UPDATE_COOKIE_SHARD_PROPERTY_NAME,updateCookie);
        log=configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public void after() {
        log.info("JianShuCookieUpdateAfterBootstrapLoaderProcess loaded!");
        if (configuration.getViewLoaded().get()){
            updateCookie();
        }else {
            configuration.getViewLoaded().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    updateCookie();
                }
            });
        }
    }

    public void updateCookie(){
       TimerTask timerTask= new TimerTask() {
            @Override
            public void run() {
                if (updateCookie.get()){
                    doUpdateCookie();
                }
            }
        };
       Timer timer=new Timer("jian shu  cookie Refresh Task",true);
        updateCookie.addListener((observable, oldValue, newValue) -> {
            if (newValue){
                log.debug("refresh cookie  task is started ...");
                timer.schedule(timerTask,0,TIME_DELAY);
            }else {
                log.debug("refresh cookie  task is done ...");
                timer.cancel();
            }
        });

        doUpdateCookie();

    }

    public void  doUpdateCookie(){
        log.debug("will refresh cookie ...");
        JianShuPersistence jianShuPersistence =configuration.getPersistence(JianShuPersistence.class);
        String cookie= jianShuPersistence.getCookie();
        if (StringUtils.isEmpty(cookie)){
            log.debug("no cookie ...");
            // 没有Cookie
            if (updateCookie.get()){
                updateCookie.set(false);
            }
            return;
        }

        Long expire= jianShuPersistence.getExpire();
        if (new Date().getTime()>expire){
            // cookie已失效
            log.debug(" cookie is expire ...");
            if (updateCookie.get()){
                updateCookie.set(false);
            }
            if (configuration.getViewLoaded().get()){
                cookieExpireTips();
            }else {
                configuration.getViewLoaded().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                       if (newValue){
                           cookieExpireTips();
                           configuration.getViewLoaded().removeListener(this);
                       }
                    }
                });
            }
            return;
        }

        if (!updateCookie.get()){
            updateCookie.set(true);
        }
        requestForRefresh(jianShuPersistence);
    }
public void  cookieExpireTips(){
    Stage stage = configuration.getViewContext().newStage();
        stage.initStyle(StageStyle.TRANSPARENT);
    stage.setTitle("简书 登录信息失效");
    stage.setScene(new Scene(new AnchorPane(PopDialog.create()
            .setHeader("警告")
            .setContent("简书登录信息已失效，为了不影响使用，请重新进行登录操作。")
            .buttonTypes(new ButtonType("知道了"))
            .callback(new Callable<Boolean, ButtonType>() {
                @Override
                public Boolean apply(ButtonType buttonType) throws URISyntaxException, IOException {
                    stage.close();
                    return true;
                }
            })
            .bindParent(configuration.getViewContext().getStage())
            .getDialogPane())));
    stage.showAndWait();
}
    public void requestForRefresh(JianShuPersistence jianShuPersistence){
        try (
                CloseableHttpClient client= HttpClients.custom()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36")
                        .setDefaultHeaders(Collections.singletonList(
                                new BasicHeader("Cookie", jianShuPersistence.getCookie()))

                )
             .build()
        ){

            Date now=new Date();
            HttpGet get=new HttpGet(JianShuStoreBuilder.ACCESS_URL);
            HttpResponse response=client.execute(get);
            if (response.getStatusLine().getStatusCode()==200){
                log.debug("refresh cookie is done");
                jianShuPersistence.setExpire(now.getTime()+1000 * 60 * 60 * 24 * 30L);
                configuration.storePersistence(jianShuPersistence);
            }else {
                log.info("{}",response.getStatusLine().getStatusCode());
                log.info(EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
