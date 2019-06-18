package cn.jpanda.screenshot.oss.core.persistence;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import lombok.Data;

/**
 * 引导配置文件
 */
@Data
@Profile(bootstrap = true)
public class BootstrapPersistence implements Persistence {
    /**
     * 是否使用了主控密码
     */
    private boolean usePassword;

    /**
     * 当前使用次数
     */
    private Integer useCount = 0;

    /**
     * 更新使用次数
     */
    public void updateUseCount() {
        useCount++;
    }
}
