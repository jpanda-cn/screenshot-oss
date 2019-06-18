package cn.jpanda.screenshot.oss.store.img.instances;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.img.ImageStore;

import java.awt.image.BufferedImage;

/**
 * 不对图片进行实际的存储操作
 */
@ImgStore(name = "不保存", type = ImageType.NO_PATH)
public class NothingImageStore implements ImageStore {

    @Override
    public String store(BufferedImage image) {
        // !! 鉴于该模式的特殊性质，选择该模式进行存储的时候,不会产生地址供使用
        return "图片未被存储";
    }
}
