package cn.jpanda.screenshot.oss.core;

import javafx.fxml.Initializable;

public class ImageStoreRegister {
    /**
     * 渠道名称，比如阿里OSS,七牛OSS,本地存储
     */
    private String name;

    /**
     * 图片存储模块
     */
    private ImageStore imageStore;

    /**
     * 图片配置模块
     */
    private Initializable imageConfig;

}
