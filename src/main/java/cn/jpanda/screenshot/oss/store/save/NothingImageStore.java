package cn.jpanda.screenshot.oss.store.save;

import java.awt.image.BufferedImage;

/**
 * 不对图片进行实际的存储操作
 */
public class NothingImageStore implements ImageStore {
    @Override
    public String store(BufferedImage image) {
        // TODO 鉴于该模式的特殊性质，选择该模式进行存储的时候，将会强制调用ImageClipboardCallback
        return "图片未被存储";
    }
}
