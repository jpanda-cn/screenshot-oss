package cn.jpanda.screenshot.oss.core.configuration;

import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.context.ViewContext;
import cn.jpanda.screenshot.oss.core.persistence.DataPersistenceStrategy;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class Configuration {

    /**
     * 当前工作目录
     */
    @Getter
    @Setter
    private String currentWorkDir;
    /**
     * 主配置文件名称
     */
    @Getter
    @Setter
    private String mainConfigFileName;
    /**
     * 数据持久化策略
     */
    @Getter
    @Setter
    private DataPersistenceStrategy dataPersistenceStrategy;

    /**
     * 桌面截图获取接口
     */
    @Getter
    @Setter
    private ScreenCapture screenCapture;

    /**
     * 视图上下文
     */
    @Getter
    @Setter
    private ViewContext viewContext;

    /**
     * 获取主配置文件的完全名称
     */
    public String getMainConfigFileFullName() {
        return currentWorkDir + File.separator + mainConfigFileName;
    }
}
