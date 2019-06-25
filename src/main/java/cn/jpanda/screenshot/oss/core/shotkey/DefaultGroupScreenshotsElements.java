package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

public class DefaultGroupScreenshotsElements implements ScreenshotsElements {
    private Group group;
    private CanvasProperties canvasProperties;

    public DefaultGroupScreenshotsElements(Group group, CanvasProperties canvasProperties) {
        this.group = group;
        this.canvasProperties = canvasProperties;
    }

    @Override
    public Node getTopNode() {
        return group;
    }

    @Override
    public boolean canActive() {
        Group pane = canvasProperties.getCutPane();
        Rectangle rectangle = ShapeCovertHelper.toRectangle(group);
        return pane.contains(rectangle.getX(), rectangle.getY())
                && pane.contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight())
                && pane.contains(rectangle.getX(), rectangle.getY() + rectangle.getHeight())
                && pane.contains(rectangle.getX() + rectangle.getWidth(), rectangle.getY())
                ;
    }

    @Override
    public void active() {
        if (group != null) {
            canvasProperties.getCutPane().getChildren().add(group);
        }
    }

    @Override
    public void destroy() {
        if (group != null) {
            canvasProperties.getCutPane().getChildren().remove(group);
        }

    }
}
