package cn.jpanda.screenshot.oss.store.img.instances.imgurl;

import lombok.Data;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/20 15:45
 */
@Data
public class ImgUrlUploadResult {
    private Integer code;
    private String id;
    private String imgid;
    private String relative_path;
    private String url;
    private String thumbnail_url;
    private Integer width;
    private Integer height;
}
