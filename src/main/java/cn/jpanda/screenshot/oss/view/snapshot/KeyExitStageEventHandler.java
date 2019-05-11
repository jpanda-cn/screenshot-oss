package cn.jpanda.screenshot.oss.view.snapshot;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class KeyExitStageEventHandler implements EventHandler<KeyEvent> {
    private KeyCode code;
    private Stage stage;

    public KeyExitStageEventHandler(KeyCode code, Stage stage) {
        this.code = code;
        this.stage = stage;
    }

    @Override
    public void handle(KeyEvent event) {
        if (code.equals(event.getCode())) {
            stage.close();
        }
    }
}
