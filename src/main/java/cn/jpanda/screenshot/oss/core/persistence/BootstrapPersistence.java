package cn.jpanda.screenshot.oss.core.persistence;

import lombok.Data;

/**
 * 系统引导配置
 */
@Data
public class BootstrapPersistence implements Persistence {
    /**
     * 是否使用了主控密码
     */
    private boolean usePassword;

    private Integer useCount = 0;

    public void updateUseCount() {
        useCount++;
    }
}
