package cn.jpanda.screenshot.oss.store.img.instances;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.ImgStore;
import cn.jpanda.screenshot.oss.persistences.LocalImageStorePersistence;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.view.image.LocalFileImageStoreConfig;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地图片存储
 */
@ImgStore(name = "本地保存", type = ImageType.HAS_PATH, config = LocalFileImageStoreConfig.class)
public class LocalImageStore implements ImageStore {

    private Configuration configuration;

    public LocalImageStore(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String store(BufferedImage image) {
        LocalImageStorePersistence localImageStorePersistence = configuration.getPersistence(LocalImageStorePersistence.class);

        // 获取保存图片类型
        String path = localImageStorePersistence.getPath();
        String name = fileNameGenerator();
        String suffix = "jpeg";
        path = path + name + "." + suffix;
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
