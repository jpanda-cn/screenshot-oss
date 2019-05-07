package cn.jpanda.screenshot.oss;


import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.context.ViewContext;
import cn.jpanda.screenshot.oss.view.MainViewConfig;
import cn.jpanda.screenshot.oss.view.password.ConfigPassword;

public class JpandaBootstrap extends BootStrap {
    @Override
    protected void doStart() {
        MainViewConfig mainViewConfig = configuration.getDataPersistenceStrategy().load(MainViewConfig.class);
        ViewContext viewContext = configuration.getViewContext();
        mainViewConfig.setUseCount(mainViewConfig.getUseCount() + 1);
        configuration.getDataPersistenceStrategy().store(mainViewConfig);
        if (mainViewConfig.getUseCount() == 1) {
            // 初次使用,提示用户是否使用主控密码，弹出一个新的窗口用于设置主控密码。
            viewContext.showScene(viewContext.getScene(ConfigPassword.class));
        }
    }
}
