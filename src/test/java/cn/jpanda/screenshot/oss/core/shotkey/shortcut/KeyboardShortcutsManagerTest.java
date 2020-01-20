package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:47
 */
public class KeyboardShortcutsManagerTest extends Application {

    public void registryShortCut() {
        Shortcut shortcut = Shortcut.Builder.create()
                .ctrl(true)
                .addCode(KeyCode.A)
                .description("测试一下快键键的作用")
                .build();


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ShortCutExecutor shortCutExecutor = event -> {
            PopDialogShower.message(event.getText());
            event.consume();
        };

        ShortCutExecutorHolder holder = ShortCutExecutorHolder.builder()
                .shortcut(Shortcut.Builder.create()
                        .ctrl(true)
                        .keyEvent(KeyEvent.KEY_PRESSED)
                        .addCode(KeyCode.A)
                        .description("测试一下快键键的作用")
                        .build())
                .match(new SimpleShortcutMatch())
                .executor(shortCutExecutor)
                .build();
        KeyboardShortcutsManager manager = new KeyboardShortcutsManager();

        Group group=new Group();
        group.prefWidth(100);
        group.prefHeight(100);

        Scene scene=new Scene(group);
        manager.registryShortCut(scene, holder);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}