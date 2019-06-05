package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.store.ImageStore;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地图片存储
 */
public class LocalImageStore implements ImageStore {


    @Override
    public void store(BufferedImage image) {
        Configuration configuration = BootStrap.configuration;
        LocalImageStorePersistence localImageStorePersistence = configuration.getPersistence(LocalImageStorePersistence.class);

        // 获取保存图片类型
        String path = localImageStorePersistence.getPath();
        String name = fileNameGenerator();
        String suffix = configuration.getImageSuffix();
        path = path + name + "." + configuration.getImageSuffix();
        // 本地图片存储
        save(image, suffix, path);
        configuration.getClipboardCallbackRegistryManager().get(configuration.getClipboardCallback()).callback(image, path);

    }

    protected String fileNameGenerator() {
        return UUID.randomUUID().toString();
    }

    @SneakyThrows
    protected void save(BufferedImage image, String suffix, String path) {
        // 本地图片存储
        ImageIO.write(image, suffix, Paths.get(path).toFile());
    }
}
