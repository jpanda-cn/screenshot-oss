package cn.jpanda.screenshot.oss.store.img.instances.imgurl;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("imgid")
    private String imgId;
    @JsonProperty("relative_path")
    private String relativePath;
    private String url;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private String delete;
}
