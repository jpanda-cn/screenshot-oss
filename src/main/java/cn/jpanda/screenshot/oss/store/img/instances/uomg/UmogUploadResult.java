package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/20 15:45
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UmogUploadResult {
    private String code;
    private String imgurl;
    private String msg;
}
