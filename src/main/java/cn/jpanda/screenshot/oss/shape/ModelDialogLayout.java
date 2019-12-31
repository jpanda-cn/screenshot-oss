package cn.jpanda.screenshot.oss.shape;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/27 19:53
 */
public class ModelDialogLayout extends VBox {
    @Getter
    @Setter
    private StackPane heading = new StackPane();
    @Getter
    private StackPane body = new StackPane();
    @Getter
    @Setter
    private FlowPane actions = new FlowPane();

    public ModelDialogLayout() {
        initialize();
        heading.getStyleClass().addAll("jfx-layout-heading", "title");
        heading.setStyle("-fx-alignment: center-left;");
        heading.setStyle("-fx-font-weight: BOLD");
        heading.setStyle("-fx-padding: 24 24 20 24;");
        body.getStyleClass().add("jfx-layout-body");
        VBox.setVgrow(body, Priority.ALWAYS);
        actions.getStyleClass().add("jfx-layout-actions");
        getChildren().setAll(heading, body, actions);
    }

    private void initialize() {
        heading.setStyle(" -fx-alignment: center-left;");
        heading.setStyle(" -fx-font-weight: BOLD;;");
        heading.setStyle("   -fx-padding: 24 24 20 24;");

        actions.setStyle(" -fx-alignment: center-right;");
        actions.setStyle(" -fx-hgap: 8;");
        actions.setStyle("-fx-padding: 8 8 8 8;");

        body.setStyle("-fx-alignment: center-left;");
        body.setStyle("-fx-padding: 0 24 24 24;;");
        body.setStyle("-fx-pref-width: 400;;");

    }

    public void setBody(Node... body) {
        this.body.getChildren().setAll(body);
    }
}
