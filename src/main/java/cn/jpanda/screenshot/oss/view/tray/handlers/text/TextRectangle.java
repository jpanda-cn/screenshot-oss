package cn.jpanda.screenshot.oss.view.tray.handlers.text;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import com.sun.javafx.font.PrismFontLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * 文本矩形
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
    }

    protected void initTextArea() {
        this.textArea = new TextArea() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                ScrollBar scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
                scrollBar.setOpacity(0);
                scrollBar.setDisable(true);
            }
        };
        // 启用自动换行
//        this.textArea.setWrapText(true);
        // 透明背景
        this.textArea.getStylesheets().add("/css/text-area-transparent.css");

        // 默认大小
        textArea.setPrefColumnCount(1);
        textArea.setPrefRowCount(1);
        // 最大高度和宽度
        getChildren().addAll(textArea);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            reRenderText(newValue);
        });
        textArea.fontProperty().addListener((observable, oldValue, newValue) -> reRenderText(textArea.textProperty().get()));
        textArea.toFront();
    }

    protected double computerStrWidth(String str) {
        return PrismFontLoader.getInstance().getFontMetrics(textArea.fontProperty().get()).computeStringWidth(str);
    }

    protected double computerStrHeight(Integer rowCount) {
        return PrismFontLoader.getInstance().getFontMetrics(textArea.fontProperty().get()).getLineHeight() * rowCount;
    }

    protected void reRenderText(String str) {
        String text = reCreateText(str);
        int rowCount = (int) text.chars().filter((c) -> c == ((int) ENTER_CHAR)).count() + 1;
        Optional<String> longContent = Stream.of(text.split(String.valueOf(ENTER_CHAR))).max(Comparator.comparingDouble(this::computerStrWidth));
        // 更新文本内容
        textArea.textProperty().setValue(text);
        // 更新高度
        textArea.prefRowCountProperty().set(rowCount);
        // 更新宽度
        textArea.prefWidthProperty().set(computerStrWidth(longContent.get()));
    }

    protected String reCreateText(String text) {
        // 根据内容长度动态确定高度和宽度
        if (StringUtils.isEmpty(text)) {
            return "";
        }

        // 最终生成的新文本
        StringBuilder newText = new StringBuilder();
        // 获取字符数组
        char[] characters = text.toCharArray();

        // 新的一行的起始位置
        int newLineStart = 0;
        int rowCount = 1;
        for (int i = 0; i < characters.length; i++) {
            // 处理手动换行
            if (characters[i] == ENTER_CHAR) {
                if (cantNewLine(computerStrHeight(++rowCount))) {
                    // 不许愿添加新行
                    return newText.toString();
                }
                // 换行
                newLineStart = i + 1;
                newText.append(ENTER_CHAR);
                continue;
            }

            // 处理自动换行,获取当前行的长度
            double currentWidth = computerStrWidth(text.substring(newLineStart, i + 1));
            // 如果当前行的长度超出了每行能够展示的最大长度，追加一个换行符，并记录当前字符为下一行的起始元素
            if (needNewLine(currentWidth)) {
                // 减掉一个大小生成新行
                if (cantNewLine(computerStrHeight(++rowCount))) {
                    // 判断下一行是否会超出高度
                    return newText.toString();
                }
                // 追加换行符
                newText.append(ENTER_CHAR);
                // 重置当前行的起始索引
                newLineStart = i;
            }
            // 追加当前元素
            newText.append(characters[i]);
        }
        return newText.toString();
    }

    protected boolean needNewLine(double w) {
        // 当前文本域允许的最大宽度
        // 外部限制区域宽度加上X减去文本域的起始X
        double boundW = limitRectangle.xProperty().add(limitRectangle.widthProperty()).subtract(textArea.getParent().getLayoutX()).get();
        textArea.setMaxWidth(boundW - 6);
        return w >= boundW - 6;
    }

    protected boolean cantNewLine(double h) {
        return !(h <= limitRectangle.yProperty().add(limitRectangle.heightProperty()).subtract(textArea.getParent().getLayoutY()).get() - 5);
    }
}
