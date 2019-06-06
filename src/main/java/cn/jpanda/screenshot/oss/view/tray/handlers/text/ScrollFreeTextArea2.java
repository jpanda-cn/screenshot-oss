package cn.jpanda.screenshot.oss.view.tray.handlers.text;

import cn.jpanda.screenshot.oss.view.tray.handlers.ShapeCovertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ScrollFreeTextArea2 extends StackPane {
    private Label label;
    private Rectangle out;
    private TextArea textArea;
    private Region content;
    private SimpleDoubleProperty contentHeight = new SimpleDoubleProperty();


    public ScrollFreeTextArea2() {
        super();
        configure();
    }

    private void configure() {
        setAlignment(Pos.TOP_LEFT);

        this.textArea = new TextArea() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (content == null) {
                    content = (Region) lookup(".content");
                    contentHeight.bind(content.heightProperty());
                }
            }
        };
        // 启用自动换行
        this.textArea.setWrapText(true);
        // 初始化标签
        this.label = new Label();
        // 启用自动换行
        this.label.setWrapText(true);
        // 绑定两者的宽度。
        this.label.prefWidthProperty().bind(this.textArea.widthProperty());
        label.textProperty().bind(textArea.textProperty());
        getChildren().addAll(textArea);
        out = ShapeCovertHelper.toRectangle(this);
        // 绑定外部边框
        out.fillProperty().set(Color.TRANSPARENT);
        out.strokeProperty().set(Color.RED);

    }

    public TextArea getTextArea() {
        return textArea;
    }

    public Label getLabel() {
        return label;
    }

    public Rectangle getOut() {
        return out;
    }
}
