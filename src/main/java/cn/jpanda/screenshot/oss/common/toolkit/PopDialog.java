package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 弹窗
 */
public class PopDialog extends Dialog<ButtonType> {
    /**
     * 配置
     */
    public static ButtonType CONFIG = new ButtonType("去配置", ButtonBar.ButtonData.APPLY);
    /**
     * 保存
     */
    public static ButtonType SAVE = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);


    public static ButtonType PLACE_HOLDER = new ButtonType("占位");

    private SimpleObjectProperty<Parent> header = new SimpleObjectProperty<>(new HBox());

    private SimpleObjectProperty<Parent> content = new SimpleObjectProperty<>(new AnchorPane());

    private SimpleObjectProperty<Animation> animation = new SimpleObjectProperty<>();

    private SimpleObjectProperty<Callable<Boolean, ButtonType>> callableSimpleObjectProperty = new SimpleObjectProperty<>(buttonType -> true);

    /**
     * 按钮样式
     */
    private ObjectProperty<Map<ButtonType, String>> buttonStyleClassProperty = new SimpleObjectProperty<>(new HashMap<>());

    {
        buttonStyleClassProperty.get().put(ButtonType.APPLY, "button-apply");
        buttonStyleClassProperty.get().put(ButtonType.CANCEL, "button-cancel");
        buttonStyleClassProperty.get().put(ButtonType.CLOSE, "button-close");
        buttonStyleClassProperty.get().put(ButtonType.OK, "button-ok");
        buttonStyleClassProperty.get().put(ButtonType.FINISH, "button-finish");
        buttonStyleClassProperty.get().put(ButtonType.NEXT, "button-next");
        buttonStyleClassProperty.get().put(ButtonType.NO, "button-no");
        buttonStyleClassProperty.get().put(ButtonType.PREVIOUS, "button-previous");
        buttonStyleClassProperty.get().put(ButtonType.YES, "button-yes");
    }

    public PopDialog addButtonClass(ButtonType buttonType, String styleClass) {
        buttonStyleClassProperty.get().put(buttonType, styleClass);
        return this;
    }

    public static PopDialog create() {
        return new PopDialog();
    }

    private PopDialog() {
        init();
    }

    protected void init() {
        initDialogPane();
        header.addListener((observable, oldValue, newValue) -> {
            newValue.getStyleClass().addAll("header-panel");
            getDialogPane().setHeader(EventHelper.addDrag(newValue));
        });
        content.addListener((observable, oldValue, newValue) -> {
            newValue.getStyleClass().addAll("content");
            getDialogPane().setContent(newValue);
        });
        callableSimpleObjectProperty.addListener((observable, oldValue, newValue) -> buttonTypes(getDialogPane().getButtonTypes().toArray(new ButtonType[0])));
        buttonStyleClassProperty.addListener((observable, oldValue, newValue) -> buttonTypes(getDialogPane().getButtonTypes().toArray(new ButtonType[0])));
        initStyle(StageStyle.TRANSPARENT);
        loadStylesheets();
        initButtonTypes();
        initAnimation();
        getDialogPane().sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (Optional.ofNullable(newValue).isPresent()) {
                newValue.setFill(Color.TRANSPARENT);
            }
        });
    }

    public void initAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(getDialogPane().scaleXProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(getDialogPane().scaleYProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(getDialogPane().visibleProperty(), false, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(10),
                        new KeyValue(getDialogPane().visibleProperty(), true, Interpolator.EASE_BOTH),
                        new KeyValue(getDialogPane().opacityProperty(), 0, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(getDialogPane().scaleXProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(getDialogPane().scaleYProperty(), 1, Interpolator.EASE_BOTH),
                        new KeyValue(getDialogPane().opacityProperty(), 1, Interpolator.EASE_BOTH)
                ));

        timeline.setDelay(Duration.seconds(0));
        this.animation.set(timeline);
        showingProperty().addListener((observable, oldValue, newValue) -> {
            getDialogPane().getScene().setFill(Color.TRANSPARENT);
            timeline.play();
        });

    }

    protected void initDialogPane() {
        DialogPane dialogPane = new DialogPane() {
            @Override
            protected Node createButton(ButtonType buttonType) {
                if (null == callableSimpleObjectProperty.get()) {
                    return super.createButton(buttonType);
                }

                final Button button = new Button(buttonType.getText());
                button.getStyleClass().add(buttonStyleClassProperty.get().getOrDefault(buttonType, "button"));
                final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
                ButtonBar.setButtonData(button, buttonData);
                button.setDefaultButton(buttonData.isDefaultButton());
                button.setCancelButton(buttonData.isCancelButton());
                button.addEventHandler(ActionEvent.ACTION, ae -> {
                    try {
                        if (callableSimpleObjectProperty.get().apply(buttonType)) {
                            if (Optional.ofNullable(animation.get()).isPresent()) {
                                animation.get().setAutoReverse(true);
                                animation.get().setRate(-1);
                                animation.get().play();
                                animation.get().setOnFinished(e -> {
                                    setResult(null==buttonType?PLACE_HOLDER:buttonType);
                                    close();
                                });
                            } else {
                                setResult(null==buttonType?PLACE_HOLDER:buttonType);
                                close();
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return button;
            }

            @Override
            protected Node createButtonBar() {
                Node node=super.createButtonBar();
                return EventHelper.addDrag(node);
            }
        };
        setDialogPane(dialogPane);
    }

    protected void loadStylesheets() {
        getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dialog.css").toExternalForm());
    }

    protected void initButtonTypes() {
        buttonTypes(ButtonType.CANCEL, CONFIG);
    }


    public PopDialog setHeader(String text) {
        return setHeader(new HBox(new Label(text)));
    }

    public PopDialog setHeader(Parent h) {
        header.set(h);
        return this;
    }

    public PopDialog setContent(String content) {
        return setContent(new HBox(new Label(content)));
    }

    public PopDialog setContent(Parent h) {
        content.set(h);
        return this;
    }

    public PopDialog buttonTypes(ButtonType... types) {
        getDialogPane().getButtonTypes().clear();

        getDialogPane().getButtonTypes().addAll(types);
        return this;
    }

    public PopDialog bindParent(Window parent) {
        return bindParent(parent, true);
    }


    public PopDialog bindParent(Window parent, boolean disableParent) {
       if (parent!=null&&parent.getScene()!=null){
           initOwner(parent);
           // 显示在中间
           if (disableParent) {
               showingProperty().addListener((observable, oldValue, newValue) -> {
                   parent.getScene().getRoot().disableProperty().set(newValue);
               });
           }
       }

        return this;
    }

    public PopDialog centerOnNode(Node node) {

        node.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                setX(newValue.getMinX() + ((newValue.getWidth() - getWidth()) / 2));
                setY(newValue.getMinY() + ((newValue.getHeight() - getHeight()) / 2));
            }
        });

        showingProperty().addListener((observable, oldValue, newValue) -> {
            Bounds bounds = node.getBoundsInLocal();
            setX(bounds.getMinX() + ((bounds.getWidth() - getWidth()) / 2));
            setY(bounds.getMinY() + ((bounds.getHeight() - getHeight()) / 2));
        });
        return this;
    }

    public PopDialog callback(Callable<Boolean, ButtonType> callable) {
        callableSimpleObjectProperty.set(callable);
        return this;
    }

}
