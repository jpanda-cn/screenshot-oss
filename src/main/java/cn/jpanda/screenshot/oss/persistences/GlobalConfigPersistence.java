package cn.jpanda.screenshot.oss.persistences;

import cn.jpanda.screenshot.oss.newcore.persistence.Persistence;
import lombok.Data;

/**
 * 默认使用的全局配置
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/13 13:47
 */
@Data
public class GlobalConfigPersistence implements Persistence {

    private Long useCount = 0L;
    /**
     * 是否使用主控密码
     */
    private boolean usePassword;

    /**
     * 是否使用截图预览
     */
    private boolean preview = true;

    /**
     * 当前使用的截图屏幕的索引
     */
    private int screenIndex = 0;
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
}
