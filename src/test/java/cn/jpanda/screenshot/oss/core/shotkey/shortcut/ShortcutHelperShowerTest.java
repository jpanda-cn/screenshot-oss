package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 13:36
 */
public class ShortcutHelperShowerTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(new Scene(new AnchorPane()));
        primaryStage.show();
        ShortcutHelperShower.show(Arrays.asList(
                ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).addCode(KeyCode.C).description("复制当前图片").build())
                        .match(new SimpleShortcutMatch())
                        .executor(event -> System.out.println(123))
                        .build()
                ,  ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(true).shift(true).addCode(KeyCode.C).description("复制当前窗口").build())
                        .match(new SimpleShortcutMatch())
                        .executor(event -> System.out.println(123))
                        .build()


        )
        ,primaryStage);
    }
}