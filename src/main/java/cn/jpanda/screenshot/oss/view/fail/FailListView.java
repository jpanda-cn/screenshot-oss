package cn.jpanda.screenshot.oss.view.fail;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class FailListView implements Initializable {

    private Configuration configuration;

    public FailListView(Configuration configuration) {
        this.configuration = configuration;
    }

    public TableView<ImageStoreResult> table;
    public TableColumn<ImageStoreResult, Button> image;
    public TableColumn<ImageStoreResult, String> store;
    public TableColumn<ImageStoreResult, TextArea> path;
    public TableColumn<ImageStoreResult, VBox> operation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        image.sortableProperty().set(false);
        image.setCellValueFactory((cell) -> {
            Image image = SwingFXUtils.toFXImage(cell.getValue().getImage().get(), null);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            Button button = new Button("", imageView);
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Stage stage = configuration.getViewContext().newStage();
                stage.initOwner(table.getScene().getWindow());
                stage.initModality(Modality.APPLICATION_MODAL);
                ImageView imageView1 = new ImageView(image);
                stage.setScene(new Scene(new AnchorPane(imageView1)));
                stage.showAndWait();
            });
            return new SimpleObjectProperty<>(button);
        });
        store.setCellValueFactory(cell -> cell.getValue().getImageStore());
        store.sortableProperty().set(false);
        path.setCellValueFactory(cell -> {
            TextArea text = new TextArea(cell.getValue().getPath().get());
            text.editableProperty().set(false);
            text.wrapTextProperty().set(true);
            text.setPrefRowCount(5);
            return new SimpleObjectProperty<>(text);
        });
        path.sortableProperty().set(false);
        path.editableProperty().set(true);

        // 添加查看异常和删除的操作
        operation.setCellValueFactory((c) -> {
            VBox box = new VBox();
            Button show = new Button("查看异常信息");
            Button delete = new Button("删除");
            box.getChildren().addAll(show, delete);
            show.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Exception e = c.getValue().getException().get();

                }
            });
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                }
            });
            return new SimpleObjectProperty<>(box);
        });

        table.setItems(configuration.getUniqueBean(ImageStoreResultHandler.class).getImageStoreResults());
        table.editableProperty().set(true);
    }
}
