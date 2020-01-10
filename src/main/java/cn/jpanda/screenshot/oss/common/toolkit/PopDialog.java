package cn.jpanda.screenshot.oss.common.toolkit;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.stage.Window;

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
    }


    protected void initDialogPane() {
        DialogPane dialogPane = new DialogPane() {
            @Override
            protected Node createButton(ButtonType buttonType) {
                if (null==callableSimpleObjectProperty.get()){
                    return super.createButton(buttonType);
                }

                final Button button = new Button(buttonType.getText());
                final ButtonBar.ButtonData buttonData = buttonType.getButtonData();
                ButtonBar.setButtonData(button, buttonData);
                button.setDefaultButton(buttonData.isDefaultButton());
                button.setCancelButton(buttonData.isCancelButton());
                button.addEventHandler(ActionEvent.ACTION, ae -> {
                    if (callableSimpleObjectProperty.get().apply(buttonType)) {
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
