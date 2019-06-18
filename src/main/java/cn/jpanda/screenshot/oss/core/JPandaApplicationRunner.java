package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.controller.ControllerAnnotationSameNameFXMLSearch;
import cn.jpanda.screenshot.oss.core.controller.DefaultViewContext;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 * 程序运行器
 * 1.首先初始化日志管理器
 * 2.初始化程序的基础数据，比如当前工作目录，默认使用的引导配置文件
 * 3.加载引导配置文件
 */
public class JPandaApplicationRunner {
    protected ViewContext viewContext;
    protected BootstrapLoader bootstrapLoader = new DefaultBootstrapLoader();

    /**
     * 开始执行程序
     *
     * @param stage 舞台
     * @param args  运行参数
     */
    public ViewContext run(Stage stage, Class startClass, String... args) {
        // 引导程序启动,此时已经完成了程序的引导状态，可以交付使用了
        Configuration configuration = bootstrapLoader.load(startClass);
        // 执行引导程序后置操作
        configuration.getAfterBootstrapLoaderProcesses().forEach(AfterBootstrapLoaderProcess::after);
        // 加载视图上下文，准备处理视图问题
        viewContext = new DefaultViewContext(stage, new ControllerAnnotationSameNameFXMLSearch(FXMLLoader.getDefaultClassLoader()), configuration);
        configuration.setViewContext(viewContext);
        return viewContext;
    }
}
