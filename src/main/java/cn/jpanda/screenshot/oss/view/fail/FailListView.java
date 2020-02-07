package cn.jpanda.screenshot.oss.view.fail;

import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.common.toolkit.ImageStoreResultExceptionShower;
import cn.jpanda.screenshot.oss.common.toolkit.LoadingShower;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.imageshower.ImageShowerManager;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Controller
public class FailListView implements Initializable {

    public static final String IS_SHOWING = FailListView.class.getCanonicalName() + "-IS_SHOWING";

    private Configuration configuration;

    private Map<String, Boolean> imageIsShow;

    public FailListView(Configuration configuration) {
        this.configuration = configuration;
        imageIsShow = configuration.getUniquePropertiesHolder(FailListView.class.getCanonicalName() + "-" + imageIsShow, new HashMap<>());
    }

    public TableView<ImageStoreResult> table;
    public TableColumn<ImageStoreResult, Button> image;
    public TableColumn<ImageStoreResult, String> store;
    public TableColumn<ImageStoreResult, TextArea> path;
    public TableColumn<ImageStoreResult, Label> exception;
    public TableColumn<ImageStoreResult, VBox> operation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        table.setFixedCellSize(130.0);

        table.editableProperty().set(false);
        image.setResizable(false);
        store.setResizable(false);
        path.setResizable(false);
        exception.setResizable(false);
        operation.setResizable(false);

        image.sortableProperty().set(false);
        store.sortableProperty().set(false);
        path.sortableProperty().set(false);
        exception.setSortable(false);
        operation.setSortable(false);

        image.setCellValueFactory((cell) -> {
            try {
                // TODO 缩略图点击时应该在图钉管理界面展示，且缩略图不可重复点击
                Image image = new Image(new FileInputStream(new File(cell.getValue().getPath().get())));
                Button button = loadImage(image);
                button.setMinWidth(100);
                button.setMinHeight(100);
                button.getStyleClass().clear();


                button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (!imageIsShow.getOrDefault(cell.getValue().getPath().get(), false)) {

                        ImageShower imageShower = ImageShower
                                .hidenTaskBar()
                                .setTopTitle(cell.getValue().getPath().get());

                        imageShower.showingProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                imageIsShow.put(cell.getValue().getPath().get(), newValue);
                            }
                        });

                        imageShower.showAndRegistry(image, configuration.getUniqueBean(ImageShowerManager.class));
                    }
                });
                return new SimpleObjectProperty<>(button);
            } catch (FileNotFoundException f) {
                return new SimpleObjectProperty<>(new Button());
            }

        });
        store.setCellValueFactory(cell -> cell.getValue().getImageStore());
        path.setCellValueFactory(cell -> {
            TextArea text = new TextArea(cell.getValue().getPath().get());
            text.editableProperty().set(false);
            text.wrapTextProperty().set(true);
            text.setPrefRowCount(5);
            return new SimpleObjectProperty<>(text);
        });
        exception.setCellValueFactory((c) -> {
            Label label = new Label(c.getValue().getException().get().getMessage());
            label.wrapTextProperty().set(true);
            return new SimpleObjectProperty<>(label);
        });
        // 添加查看异常和删除的操作
        operation.setCellValueFactory((c) -> {
            VBox box = new VBox();
            box.setSpacing(10);

            Button show = new Button("异常信息");
            show.getStyleClass().add("button-info");
            Button delete = new Button("忽略并删除");
            delete.getStyleClass().add("button-danger");
            Button retry = new Button("重试");
            retry.getStyleClass().add("button-primary");
            box.getChildren().addAll(show, delete, retry);


            show.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Platform.runLater(() -> {
                    ImageStoreResultExceptionShower.showExceptionTips(c.getValue(), configuration);
                });
            });
            // 移除，添加提示，二次确认
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                PopDialog.create().setHeader("确认删除").setContent("数据一旦删除，无法恢复！").buttonTypes(ButtonType.CANCEL, ButtonType.OK).callback(b -> {
                    if (b.equals(ButtonType.OK)) {
                        configuration.getUniqueBean(ImageStoreResultHandler.class).remove(c.getValue().getPath().get());
                    }
                    return true;
                }).showAndWait();
            });

            retry.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Stage loading = createLoading(table.getScene().getWindow());
                ImageStoreResultWrapper wrapper = new ImageStoreResultWrapper(c.getValue());
                String is = wrapper.getImageStore();
                ImageStoreRegisterManager imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
                ImageStore imageStore = imageStoreRegisterManager.getImageStore(is);
                if (imageStore == null) {
                    // nothing...
                    return;
                }
                // 移除
                ImageStoreResultHandler imageStoreResultHandler = configuration.getUniqueBean(ImageStoreResultHandler.class);
                imageStoreResultHandler.remove(c.getValue().getPath().get());
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() {
                        try {
                            return imageStore.retry(wrapper, retry.getScene().getWindow());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };

                task.setOnRunning(e -> {
                    loading.show();
                });
                task.setOnSucceeded(e -> {
                    try {
                        if (task.get()) {
                            Platform.runLater(() -> {
                                PopDialog.create().setHeader("提示").setContent("重试成功").buttonTypes(new ButtonType("知道了")).showAndWait();
                            });
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    } finally {
                        loading.close();
                    }
                });
                new Thread(task).start();
            });
            return new SimpleObjectProperty<>(box);
        });

        table.setRowFactory(tv -> new TableRow<ImageStoreResult>() {
            @Override
            public void updateIndex(int i) {
                super.updateIndex(i);
                if (i % 2 == 0) {
                    setStyle("-fx-background-color: white");
                } else {
                    setStyle("-fx-background-color: #e6e6e6");
                }
            }
        });

        table.setItems(configuration.getUniqueBean(ImageStoreResultHandler.class).getImageStoreResults());
    }

    protected Button loadImage(Image image) {
        double stroke = 2;
        Rectangle rect = new Rectangle();
        rect.getStyleClass().add("button-image");
        ImagePattern imagePattern = new ImagePattern(image);
        rect.widthProperty().set(100 + stroke * 2);
        rect.heightProperty().set(100 + stroke * 2);
        rect.setLayoutX(stroke);
        rect.setLayoutY(stroke);
        rect.setFill(imagePattern);
        rect.strokeWidthProperty().set(stroke);
        rect.strokeTypeProperty().set(StrokeType.OUTSIDE);
        Button button = new Button();
        button.setGraphic(rect);
        return button;
    }

    protected Stage createLoading(Window parent) {
        return LoadingShower.createLoading(parent);

    }
}
