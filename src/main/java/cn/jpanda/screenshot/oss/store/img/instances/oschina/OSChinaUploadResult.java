package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import lombok.Data;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:34
 */
@Data
public class OSChinaUploadResult {
    private Integer uploaded;
    private String fileName;
    private String url;
}
