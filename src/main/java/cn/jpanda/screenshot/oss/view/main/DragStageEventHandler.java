package cn.jpanda.screenshot.oss.view.main;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 舞台拖动事件处理器
 */
public class DragStageEventHandler implements EventHandler<MouseEvent> {
    private Stage stage;
    private double oldStageX;
    private double oldStageY;
    private double oldX;
    private double oldY;

    public DragStageEventHandler(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            stage.getScene().setCursor(Cursor.MOVE);
            oldStageX = stage.getX();
            oldStageY = stage.getY();
            oldX = event.getScreenX();
            oldY = event.getScreenY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            stage.setX(oldStageX + (event.getScreenX() - oldX));
            stage.setY(oldStageY + (event.getScreenY() - oldY));
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            stage.getScene().setCursor(Cursor.DEFAULT);
        }
    }
}
