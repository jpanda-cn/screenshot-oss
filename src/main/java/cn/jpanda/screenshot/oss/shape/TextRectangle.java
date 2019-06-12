package cn.jpanda.screenshot.oss.shape;

import cn.jpanda.screenshot.oss.common.toolkit.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import com.sun.javafx.font.PrismFontLoader;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());
    /**
     * 边框默认填充
     */
    public static final int BORDER_DEFAULT_PADDING = 5;
    /**
     * 边框默认颜色
     */
    public static final Color BORDER_DEFAULT_COLOR = Color.RED;
    /**
     * 边框默认宽度
     */
    public static final double BORDER_DEFAULT_STROKE_WIDTH = 1D;
    /**
     * 换行符
     */
    public static Character ENTER_CHAR = '\n';
    /**
     * 用于输入文字的文本框
     */
    private TextArea textArea;

    /**
     * 限制文本框出现的位置
     */
    private Rectangle limitRectangle;
    /**
     * 文本域边框
     */
    private Rectangle extBorder;
    /**
     * 用于缓存处理后的文本内容和原始文本内容的对照关系
     */
    private Map<String, String> textCache = new ConcurrentHashMap<>(2);

    /**
     * 边框间距
     */
    private SimpleIntegerProperty borderPadding = new SimpleIntegerProperty(BORDER_DEFAULT_PADDING);
    private SimpleObjectProperty<Color> borderColor = new SimpleObjectProperty<>(BORDER_DEFAULT_COLOR);
    private SimpleDoubleProperty borderWidth = new SimpleDoubleProperty(BORDER_DEFAULT_STROKE_WIDTH);

    public TextRectangle(Rectangle rectangle) {
        super();
        this.limitRectangle = rectangle;
        init();
    }

    protected void init() {
        // 配置文字对齐方式
        setAlignment(Pos.TOP_LEFT);
        // 初始化当前文本域
        initTextArea();
        initBorder();
    }

    protected void initBorder() {
        // 为文本域配置一个边框
        extBorder = ShapeCovertHelper.toRectangle(this);
        extBorder.fillProperty().set(Color.TRANSPARENT);
        extBorder.strokeProperty().bind(borderColor);
        extBorder.strokeWidthProperty().bind(borderWidth);

        // 绑定宽/高度
        extBorder.widthProperty().bind(this.widthProperty().add(borderPadding.multiply(2)));
        extBorder.heightProperty().bind(this.heightProperty().add(borderPadding.multiply(2)));
        layoutXProperty().bind(extBorder.xProperty().add(borderPadding));
        layoutYProperty().bind(extBorder.yProperty().add(borderPadding));
        parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                removeChild(oldValue);
                addChild(newValue);
                extBorder.toBack();
            }

            private void removeChild(Parent parent) {
                if (parent == null) {
                    return;
                }
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(extBorder);
                } else if (parent instanceof Group) {
                    ((Group) parent).getChildren().remove(extBorder);
                }
            }

            private void addChild(Parent parent) {
                if (parent == null) {
                    return;
                }
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().add(extBorder);
                } else if (parent instanceof Group) {
                    ((Group) parent).getChildren().add(extBorder);
                }
            }
        });
        // 添加一个拖动事件
        extBorder.addEventFilter(MouseEvent.ANY, new DragRectangleEventHandler(extBorder, limitRectangle, null));
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
        // 透明背景
        this.textArea.getStylesheets().add("/css/text-area-transparent.css");

        // 默认大小
        textArea.setPrefColumnCount(2);
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

    protected String readText4Cache(String str) {
        // 判断字符串是否存在于缓存中
        String result = textCache.getOrDefault(str, str);
        textCache.remove(str);
        return result;
    }

    protected void saveText2Cache(String str, String newStr) {
        textCache.put(str, newStr);
    }

    protected void reRenderText(String str) {
        String text = reCreateText(str);
        int rowCount = (int) text.chars().filter((c) -> c == ((int) ENTER_CHAR)).count() + 1;
        Optional<String> longContent = Stream.of(text.split(String.valueOf(ENTER_CHAR))).max(Comparator.comparingDouble(this::computerStrWidth));
        log.trace(textArea.getPadding().toString());
        // 更新文本内容
        log.trace("text:%s", text);
        textArea.textProperty().setValue(text);

        // 更新高度
        log.trace("rowCount:%d", rowCount);
        textArea.prefRowCountProperty().set(rowCount);
        double height = computerStrHeight(rowCount);
        textArea.minHeightProperty().setValue(height);
        textArea.prefHeightProperty().setValue(height);
        // 更新宽度
        if (longContent.isPresent()) {
            double width = computerStrWidth(longContent.get());
            textArea.prefWidthProperty().set(width + 5);
            textArea.minWidthProperty().set(textArea.prefWidthProperty().get());
            log.trace("width:%f", textArea.prefWidthProperty().getValue());
        }
    }

    protected String reCreateText(String text) {
        // 根据内容长度动态确定高度和宽度
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        // 从缓存中获取到原始文本
        text = readText4Cache(text);
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
                    // 保存文本缓存
                    saveText2Cache(newText.toString(), text);
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
        // 保存文本缓存
        saveText2Cache(newText.toString(), text);
        return newText.toString();
    }

    protected boolean needNewLine(double w) {
        // 当前文本域允许的最大宽度
        // 外部限制区域宽度加上X减去文本域的起始X
        double boundW = limitRectangle.xProperty().add(limitRectangle.widthProperty()).subtract(textArea.getParent().getLayoutX()).get();
        textArea.setMaxWidth(boundW - (borderPadding.add(1).get()));
        return w >= textArea.getMaxWidth();
    }

    protected boolean cantNewLine(double h) {
        return !(h <= limitRectangle.yProperty().add(limitRectangle.heightProperty()).subtract(textArea.getParent().getLayoutY()).get() - 5);
    }
}
