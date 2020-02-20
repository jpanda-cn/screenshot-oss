package cn.jpanda.screenshot.oss.store.img.instances.sm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/20 15:45
 */
@Data
public class SmMsUploadResult {
    private boolean success;
    private String code;
    private String error;
    private String message;
    private Data data;
    @JsonProperty("RequestId")
    private String RequestId;

    @lombok.Data
    @Getter
    public static class Data {
        @JsonProperty("file_id")
        private Integer fileId;
        private double width;
        private double height;
        @JsonProperty("filename")
        private String fileName;
        @JsonProperty("storename")
        private String storeName;
        private double size;
        private String path;
        private String hash;
        private String url;
        private String delete;
        private String page;
    }
}
