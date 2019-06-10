package cn.jpanda.screenshot.oss.view.tray.handlers.text;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;


/**
 * 文本矩形
 * 核心由一个矩形，一个标签以及一个文本域构成。
 * <p>
 * 矩形用来拖动文本框，标签用来展示内容，文本域用来输入文字。
 * <p>
 * 矩形以5的间距包裹着文本域，拖动矩形可以调整文本框的位置，即，修改整体的xy坐标，调整大小则同时涉及着整体的宽高xy。
 * <p>
 * 文本域仅仅是用来输入文字。
 * <p>
 * 标签用来展示文字。
 *
 * <p>
 * .text-area, .text-area .viewport, .text-area .content {
 * -fx-background-color: transparent ;
 * }
 * </p>
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/10 9:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TextRectangle extends StackPane {
    /**
     * 换行符
     */
    public static Character ENTER_CHAR = '\n';
    /**
     * 用于调整高度和宽度使用的标签
     */
    private Label label;
    /**
     * 用于输入文字的文本框
     */
    private TextArea textArea;

    private Rectangle limitRectangle;

    public TextRectangle(Rectangle rectangle) {
        super();
        this.limitRectangle = rectangle;
        init();
    }

    protected void init() {
        // 配置文字对齐方式
        setAlignment(Pos.TOP_LEFT);
        initTextArea();
//        initLabel();
    }

    protected void initTextArea() {
        this.textArea = new TextArea();
        // 启用自动换行
        this.textArea.setWrapText(true);
        // 透明背景
        this.textArea.getStylesheets().add("/css/text-area-transparent.css");

        // 默认大小
        textArea.setPrefColumnCount(1);
        textArea.setPrefRowCount(1);
        // 最大高度和宽度
        getChildren().addAll(textArea);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            // 根据内容长度动态确定高度和宽度
            if (StringUtils.isEmpty(newValue)) {
                return;
            }
            double width = limitRectangle.xProperty().add(limitRectangle.widthProperty()).subtract(textArea.getParent().getLayoutX()).get();
            double height = limitRectangle.yProperty().add(limitRectangle.heightProperty()).subtract(textArea.getParent().getLayoutY()).get();
            Insets insets = textArea.paddingProperty().get();
            // 获取当前每一行能够展示的最大字符数
            int maxCharCount = (int) ((width) / (textArea.fontProperty().get().getSize()));
            // 最终生成的新文本
            StringBuilder newText = new StringBuilder();
            // 获取字符数组
            char[] characters = newValue.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                newText.append(characters[i]);
                if (i % maxCharCount == 0) {
                    // 每行最后一个字母
                    if (i + 1 < characters.length) {
                        // 判断是否还需要添加换行符
                        if (characters[i + 1] != ENTER_CHAR) {
                            // 添加换行符
                            newText.append(ENTER_CHAR);
                        }
                    }else {
                        newText.append(ENTER_CHAR);
                    }
                }
            }
            // 重写流

            // 处理每一行的内容，如果长度大于等于maxCharCount，则执行换行操作，其中包括替换字符串，手动添加换行标志
            String[] rows = newValue.split(String.valueOf(ENTER_CHAR));

            // 最大宽度
            int maxWidth = 0;
            for (String row : rows) {
                // 在满足最大长度之后，手动添加一个换行符号
                int start = 0;
                int end = 0;
                while (end <= row.length() - 1) {
                    // 当前行能够持有的最大长度
                    int maxIndex = start + maxCharCount;
                    // 如果数量足够添加文本的同时添加换行符
                    if (row.length() < maxIndex) {
                        // 字符数不满一行
                        end = row.length();
                        newText.append(row, start, end);
                    } else {
                        // 字数满一行
                        end = maxIndex;
                        newText.append(row, start, end).append(ENTER_CHAR);
                    }
                    start = end;
                }
            }
            String finalText = newText.toString();
            // 以换行符分隔当前文本内容，每一个换行符都会产生一个新行
            int startIndex = -1;
            int rowCount = 0;
            do {
                startIndex = finalText.indexOf(ENTER_CHAR, ++startIndex);
                rowCount++;
            } while (startIndex != -1);
            if (finalText.length() % maxCharCount != 0) {
                rowCount++;
            }
            if (rowCount > 1) {
                maxWidth = maxCharCount;
            }
            // 更新文本内容
            textArea.textProperty().setValue(finalText);
            textArea.prefRowCountProperty().set(rowCount);
            textArea.prefColumnCountProperty().set(maxWidth);
        });
        textArea.toFront();
    }


    protected void initLabel() {

        // 初始化标签
        this.label = new Label();
        // 启用自动换行
        this.label.setWrapText(true);
        // 透明
        this.label.textFillProperty().set(Color.RED);
        // 绑定文字内容
        label.textProperty().bind(textArea.textProperty());
        label.fontProperty().bind(textArea.fontProperty());
//        label.prefWidthProperty().set(label.fontProperty().get().getSize()*2);
//        label.prefHeightProperty().set(label.fontProperty().get().getSize()*2);
        StackPane stackPane = new StackPane(label);
        // 文字间距
        stackPane.paddingProperty().bind(textArea.paddingProperty());
        stackPane.alignmentProperty().set(Pos.TOP_LEFT);
        textArea.layoutXProperty().addListener((observable, oldValue, newValue) -> stackPane.layoutXProperty().set(newValue.doubleValue()));
        textArea.layoutYProperty().addListener((observable, oldValue, newValue) -> stackPane.layoutYProperty().set(newValue.doubleValue()));
        getChildren().addAll(stackPane);
        stackPane.styleProperty().bind(textArea.styleProperty());
        // 绑定模拟容器和TextArea的位置关系
        textArea.prefWidthProperty().bind(stackPane.prefWidthProperty());
        textArea.prefHeightProperty().bind(stackPane.prefHeightProperty());
        textArea.prefColumnCountProperty().setValue(3);
        textArea.prefRowCountProperty().setValue(1);
        label.widthProperty().addListener((observable, oldValue, newValue) -> textArea.minWidth(newValue.doubleValue() + textArea.getFont().getSize()));
        label.heightProperty().addListener((observable, oldValue, newValue) -> textArea.minHeight(newValue.doubleValue() + 10));
        label.textProperty().addListener((observable, oldValue, newValue) -> {
            textArea.minWidth(label.widthProperty().get());
            textArea.minHeight(label.heightProperty().get());
        });

    }
}
