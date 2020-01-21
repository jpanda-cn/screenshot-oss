package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ConfigurationHolder;
import cn.jpanda.screenshot.oss.core.capture.JPandaScreenCapture;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
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
import java.util.Comparator;
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
    private  ScreenCapture screenCapture;
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
        loadScreenCapture();
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

    public PopDialog removeContentClass(String styleClass){
        if (content.get()!=null){
            content.get().getStyleClass().remove(styleClass);
        }
        content.addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                newValue.getStyleClass().remove(styleClass);
            }
        });
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
               ChangeListener<Number> bindParent=new ChangeListener<Number>() {
                   @Override
                   public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                       double x=parent.getX()+((parent.getWidth()-getWidth())/2);
                       double y=parent.getY()+((parent.getHeight()-getHeight())/2);
                       updateX(x,getWidth());
                       updateY(y,getHeight());
                   }
               };
               parent.widthProperty().addListener(bindParent);
               parent.heightProperty().addListener(bindParent);
               parent.xProperty().addListener(bindParent);
               parent.yProperty().addListener(bindParent);
               widthProperty().addListener(bindParent);
               heightProperty().addListener(bindParent);
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
                double x=newValue.getMinX() + ((newValue.getWidth() - getWidth()) / 2);
                double y=newValue.getMinY() + ((newValue.getHeight() - getHeight()) / 2);
                updateX(x,getWidth());
                updateY(y,getHeight());
            }
        });

        showingProperty().addListener((observable, oldValue, newValue) -> {
            Bounds bounds = node.getBoundsInLocal();
            double x=bounds.getMinX() + ((bounds.getWidth() - getWidth()) / 2);
            double y=bounds.getMinY() + ((bounds.getHeight() - getHeight()) / 2);
            updateX(x,getWidth());
            updateY(y,getHeight());
        });
        return this;
    }

    public PopDialog callback(Callable<Boolean, ButtonType> callable) {
        callableSimpleObjectProperty.set(callable);
        return this;
    }

    protected void loadScreenCapture(){
        Configuration configuration=ConfigurationHolder.getInstance().getConfiguration();
        if (configuration!=null){
            screenCapture=configuration.getUniqueBean(ScreenCapture.class);
        }
        if (screenCapture==null){
            screenCapture=new JPandaScreenCapture();
        }
    }
    public void  updateX(final double x,final double w){
        double[] xa=new double[]{x};
        if (xa[0]<screenCapture.minx()){
            xa[0]=screenCapture.minx();
        }
        screenCapture.screens().stream().max(Comparator.comparingDouble(s -> s.getBounds().getMaxX())).ifPresent(s->{
            if (xa[0]>s.getBounds().getMaxX()-w){
                xa[0]=s.getBounds().getMaxX()-w;
            }
        });
        setX(xa[0]);
    }
    public void updateY(final double y,final double h){
        double[] ya=new double[]{y};
        if (ya[0]<screenCapture.miny()){
            ya[0]=screenCapture.miny();
        }
        screenCapture.screens().stream().max(Comparator.comparingDouble(s -> s.getBounds().getMaxY())).ifPresent(s->{
            if (ya[0]>s.getBounds().getMaxY()-h){
                ya[0]=s.getBounds().getMaxY()-h;
            }
        });
        setY(ya[0]);
    }
}
