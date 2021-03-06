package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultScreenshotsElementConvertor;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementConvertor;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElements;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementsHolder;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CanvasProperties {
    private Configuration configuration;
    /**
     * 全局的画布对象
     */
    private GraphicsContext globalGraphicsContext;
    /**
     * 截图容器
     */
    private Group cutPane;
    /**
     * 用户已选中的截图区域
     */
    private Rectangle cutRectangle;

    /**
     * 背景图片
     */
    private WritableImage backgroundImage;
    /**
     * 计算用的背景图
     */
    protected WritableImage computerImage;

    /**
     * 托盘选中的按钮类型
     */
    private CutInnerType cutInnerType = CutInnerType.DRAG;

    /**
     * 存放所有的子节点
     */
    private ScreenshotsElementsHolder screenshotsElementsHolder = new ScreenshotsElementsHolder();

    private ScreenshotsElementConvertor screenshotsElementConvertor = new DefaultScreenshotsElementConvertor(screenshotsElementsHolder);


    public List<Node> listGroups() {
        return screenshotsElementsHolder.listEffective().stream().map(ScreenshotsElements::getTopNode).collect(Collectors.toList());
    }


    private Map<CutInnerType, TrayConfig> trayConfigs = new HashMap<>();

    public CanvasProperties(GraphicsContext globalGraphicsContext, Rectangle cutRectangle, Configuration configuration, WritableImage writableImage, WritableImage computerImage) {
        this.globalGraphicsContext = globalGraphicsContext;
        this.configuration = configuration;
        this.cutRectangle = cutRectangle;
        this.backgroundImage = writableImage;
        this.computerImage = computerImage;
        cutPane = ((Group) (cutRectangle.getParent()));
    }

    @SneakyThrows
    public TrayConfig getTrayConfig(CutInnerType key) {
        return getTrayConfig(key, true);
    }

    @SneakyThrows
    public TrayConfig getTrayConfig(CutInnerType key, boolean clone) {
        if (clone) {
            return getCurrentConfig(key).shallowClone();
        }
        return getCurrentConfig(key);
    }

    private TrayConfig getCurrentConfig(CutInnerType key) {
        if (!trayConfigs.containsKey(key)) {
            addTrayConfig(key, new TrayConfig());
        }
        return trayConfigs.get(key);
    }

    public TrayConfig getCurrentConfig() {
        return getCurrentConfig(cutInnerType);
    }

    public void addTrayConfig(CutInnerType key, TrayConfig config) {
        trayConfigs.put(key, config);
    }

    public void setCutInnerType(CutInnerType cutInnerType) {
        this.cutInnerType = cutInnerType;
        configuration.registryUniquePropertiesHolder(CutInnerType.class, cutInnerType);
    }
}
