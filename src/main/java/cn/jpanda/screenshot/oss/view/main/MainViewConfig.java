package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
public class MainViewConfig implements Persistence {

    private Long useCount = 0L;
    /**
     * 是否使用主控密码
     */
    private boolean usePassword;

    /**
     * 是否使用截图预览
     */
    private boolean preview = true;

}
