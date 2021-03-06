package cn.jpanda.screenshot.oss.persistences;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.i18n.I18nEnums;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

/**
 * 默认使用的全局配置
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/13 13:47
 */
@Data
@Profile
public class GlobalConfigPersistence implements Persistence {

    /**
     * 是否使用主控密码
     */
    private boolean usePassword;

    /**
     * 截图时隐藏主窗口
     */
    private boolean hideIndexScreen = true;

    /**
     * 图片存储方式
     */
    private String imageStore = "本地保存";
    /**
     * 剪切板存储内容
     */
    private String clipboardCallback = "地址";
    /**
     * 保存的图片格式
     */
    private String imageSuffix = "png";

    /**
     * 当前语言环境
     */
    private String locale = I18nEnums.CHINESE.toString();
}
