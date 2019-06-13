package cn.jpanda.screenshot.oss.store.save;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.persistences.LocalImageStorePersistence;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地图片存储
 */
public class LocalImageStore implements ImageStore {


    @Override
    public String store(BufferedImage image) {
        Configuration configuration = BootStrap.configuration;
        LocalImageStorePersistence localImageStorePersistence = configuration.getPersistence(LocalImageStorePersistence.class);

        // 获取保存图片类型
        String path = localImageStorePersistence.getPath();
        String name = fileNameGenerator();
        String suffix = configuration.getImageSuffix();
        path = path + name + "." + configuration.getImageSuffix();
        // 本地图片存储
        save(image, suffix, path);
        return path;
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
