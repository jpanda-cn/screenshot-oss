package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/6 19:18
 */
public class EventHelper {
    public static <T extends Node> T addDrag(T node) {
        node.addEventHandler(MouseEvent.ANY, crateDrag(node));
        return node;
    }


    public static EventHandler<MouseEvent> crateDrag(Node node) {

        return new EventHandler<MouseEvent>() {
            private double xOffset = 0;
            private double yOffset = 0;
            Stage stage;

            @Override
            public void handle(MouseEvent event) {
                node.setCursor(Cursor.MOVE);
                if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    stage = (Stage) node.getScene().getWindow();
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            }
        };
    }
}
