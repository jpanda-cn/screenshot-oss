package cn.jpanda.screenshot.oss.view;

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
     * 主控密码
     */
    private String password;
}
