package cn.jpanda.screenshot.oss.shape;

import com.sun.istack.internal.NotNull;
import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.List;

/**
 * 模态框
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/27 19:17
 */
public class ModelDialog<R> extends Dialog<R> {
    /**
     * 父窗口
     */
    final private Window parent;

    private InvalidationListener widthListener;
    private InvalidationListener heightListener;
    private InvalidationListener xListener;
    private InvalidationListener yListener;

    private final Pane container;
    private final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);

    public ModelDialog(@NotNull Window parent) {

        this.parent = parent;
        container = new AnchorPane();
        container.setStyle(" -fx-background-color: WHITE;");
        container.getStyleClass().add("depth-container");
        container.setPickOnBounds(false);
        final Node materialNode = JFXDepthManager.createMaterialNode(container, 2);
        container.setPickOnBounds(false);
        materialNode.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        initOwner(parent);
        // 初始化 DialogPane
        final DialogPane dialogPane = new DialogPane() {


            @Override
            protected double computePrefHeight(double width) {
                return parent.getHeight();
            }

            @Override
            protected double computePrefWidth(double height) {
                return parent.getWidth();
            }


            @Override
            protected void layoutChildren() {
                List<Node> managed = getManagedChildren();
                final double width = getWidth();
                double height = getHeight();
                double top = getInsets().getTop();
                double right = getInsets().getRight();
                double left = getInsets().getLeft();
                double bottom = getInsets().getBottom();
                double contentWidth = width - left - right;
                double contentHeight = height - top - bottom;
                for (Node child : managed) {
                    layoutInArea(child, left, top, contentWidth, contentHeight,
                            0, Insets.EMPTY, HPos.CENTER, VPos.CENTER);
                }
            }
        };
        // 透明
        dialogPane.setStyle("-fx-padding: 0;");
        dialogPane.setStyle("-fx-background-color: rgba(0,0,0,0.63);");
        dialogPane.setContent(materialNode);
        setDialogPane(dialogPane);
        dialogPane.getScene().setFill(Color.TRANSPARENT);

        if (parent != null) {
            // set the window to transparent
            initStyle(StageStyle.TRANSPARENT);
            initOwner(parent);
            // 绑定
            widthListener = observable -> updateWidth();
            heightListener = observable -> updateHeight();
            xListener = observable -> updateX();
            yListener = observable -> updateY();
        }

        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWING, event -> {
            addLayoutListeners();
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_SHOWN, event -> {
            if (getOwner() != null) {
                updateLayout();
            }
        });
        eventHandlerManager.addEventHandler(DialogEvent.DIALOG_HIDDEN, event -> removeLayoutListeners());


        getDialogPane().getScene().getWindow().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                close();
                keyEvent.consume();
            }
        });
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail).prepend(eventHandlerManager);
    }


    private void removeLayoutListeners() {
        Window stage = getOwner();
        if (stage != null) {
            stage.getScene().widthProperty().removeListener(widthListener);
            stage.getScene().heightProperty().removeListener(heightListener);
            stage.xProperty().removeListener(xListener);
            stage.yProperty().removeListener(yListener);
        }
    }

    private void addLayoutListeners() {
        Window stage = getOwner();
        if (stage != null) {
            if (widthListener == null) {
                throw new RuntimeException("Owner can only be set using the constructor");
            }
            stage.getScene().widthProperty().addListener(widthListener);
            stage.getScene().heightProperty().addListener(heightListener);
            stage.xProperty().addListener(xListener);
            stage.yProperty().addListener(yListener);
        }
    }

    private void updateLayout() {
        updateX();
        updateY();
        updateWidth();
        updateHeight();
    }

    private void updateHeight() {
        Window stage = getOwner();
        setHeight(stage.getScene().getHeight());
    }

    private void updateWidth() {
        Window stage = getOwner();
        setWidth(stage.getScene().getWidth());
    }

    private void updateY() {
        Window stage = getOwner();
        setY(stage.getY() + stage.getScene().getY());
    }

    private void updateX() {
        Window stage = getOwner();
        setX(stage.getX() + stage.getScene().getX());
    }

    public void setContent(Node... content) {
        for (Node node : content) {
            node.getProperties().put(ModelDialog.class, this);
        }
        container.getChildren().setAll(content);
    }
}
