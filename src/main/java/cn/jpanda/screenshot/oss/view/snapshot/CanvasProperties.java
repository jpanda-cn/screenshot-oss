package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 托盘选中的按钮类型
     */
    private CutInnerType cutInnerType = CutInnerType.DRAG;

    /**
     * 存放所有的子节点
     */
    private List<Group> allGroupNodes = new ArrayList<>();

    public void putGroup(Group group) {
        allGroupNodes.add(group);
    }

    public Group popGroup() {
        if (allGroupNodes.isEmpty()) {
            return null;
        }
        return allGroupNodes.remove(allGroupNodes.size() - 1);
    }

    public List<Group> listGroups() {
        return allGroupNodes;
    }

    private Map<CutInnerType, TrayConfig> trayConfigs = new HashMap<>();

    public CanvasProperties(GraphicsContext globalGraphicsContext, Rectangle cutRectangle, Configuration configuration) {
        this.globalGraphicsContext = globalGraphicsContext;
        this.configuration = configuration;
        this.cutRectangle = cutRectangle;
        cutPane = ((Group) (cutRectangle.getParent()));
    }

    public TrayConfig getTrayConfig(CutInnerType key) {
        if (!trayConfigs.containsKey(key)) {
            addTrayConfig(key, new TrayConfig());
        }
        return trayConfigs.get(key);
    }

    public TrayConfig getCurrentConfig() {
        return getTrayConfig(cutInnerType);
    }

    public void addTrayConfig(CutInnerType key, TrayConfig config) {
        trayConfigs.put(key, config);
    }
}
