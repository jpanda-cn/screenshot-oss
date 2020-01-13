package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.animation.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

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

    private SimpleObjectProperty<Parent> header = new SimpleObjectProperty<>(new HBox());

    private SimpleObjectProperty<Parent> content = new SimpleObjectProperty<>(new AnchorPane());

    private SimpleObjectProperty<Animation> animation = new SimpleObjectProperty<>();

    private SimpleObjectProperty<Callable<Boolean, ButtonType>> callableSimpleObjectProperty = new SimpleObjectProperty<>(null);

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
        initStyle(StageStyle.TRANSPARENT);
        loadStylesheets();
        initButtonTypes();
        initAnimation();
        getDialogPane().parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                System.out.println(321);
            }
        });
        getDialogPane().sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                System.out.println("123");
                if (Optional.ofNullable(newValue).isPresent()) {
                    newValue.setFill(Color.TRANSPARENT);
                }
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
                final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
                ButtonBar.setButtonData(button, buttonData);
                button.setDefaultButton(buttonData.isDefaultButton());
                button.setCancelButton(buttonData.isCancelButton());
                button.addEventHandler(ActionEvent.ACTION, ae -> {
                    if (callableSimpleObjectProperty.get().apply(buttonType)) {
                        if (Optional.ofNullable(animation.get()).isPresent()) {
                            animation.get().setAutoReverse(true);
                            animation.get().setRate(-1);
                            animation.get().play();
                            animation.get().setOnFinished(e -> {
                                close();
                            });
                        }
                    } else {
                        close();
                    }
                });
                return button;
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
        initOwner(parent);

        // 显示在中间
        if (disableParent && parent != null) {
            showingProperty().addListener((observable, oldValue, newValue) -> parent.getScene().getRoot().disableProperty().set(newValue));
        }
        return this;
    }

    public PopDialog callback(Callable<Boolean, ButtonType> callable) {
        callableSimpleObjectProperty.set(callable);
        return this;
    }

    public PopDialog getPopDiag() {
        return this;
    }
}
