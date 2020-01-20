package cn.jpanda.screenshot.oss.store.img.instances.jianshu;

import lombok.Data;

/**
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 16:02
 */
@Data
public class JianShuUploadResult {
    /**
     * 图片格式
     */
    private String format;
    /**
     * 访问地址
     */
    private String url;
    /**
     * 图片高度
     */
    private Integer height;
    /**
     * 图片宽度
     */
    private Integer width;
}
