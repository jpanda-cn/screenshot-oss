package cn.jpanda.screenshot.oss.service.handlers;

import cn.jpanda.screenshot.oss.core.Configuration;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class KeyExitStageEventHandler implements EventHandler<KeyEvent> {
    private KeyCode code;
    private Stage stage;
    private Configuration configuration;

    public KeyExitStageEventHandler(KeyCode code, Stage stage, Configuration configuration) {
        this.code = code;
        this.stage = stage;
        this.configuration = configuration;
    }


    @Override
    public void handle(KeyEvent event) {
        if (code.equals(event.getCode())) {
            configuration.setCutting(false);
            stage.close();
        }
    }
}
