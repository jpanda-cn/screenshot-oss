package cn.jpanda.screenshot.oss.store.img.instances;

import cn.jpanda.screenshot.oss.common.enums.ImageType;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.persistences.LocalImageStorePersistence;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.LocalFileImageStoreConfig;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.UUID;


/**
 * 本地图片存储
 */
@ImgStore(name = LocalImageStore.NAME, type = ImageType.HAS_PATH, config = LocalFileImageStoreConfig.class)
public class LocalImageStore extends AbstractConfigImageStore {
    final static String NAME = "本地保存";

    public LocalImageStore(Configuration configuration) {
        super(configuration);
    }


    @Override
    public String store(BufferedImage image) {
        LocalImageStorePersistence localImageStorePersistence = configuration.getPersistence(LocalImageStorePersistence.class);
        if (StringUtils.isEmpty(localImageStorePersistence.getPath())) {
            localImageStorePersistence.setPath(configuration.getWorkPath());
            configuration.storePersistence(localImageStorePersistence);
        }
        // 获取保存图片类型
        String path = localImageStorePersistence.getPath();
        String name = fileNameGenerator();
        String suffix = "png";
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

    @Override
    public String getName() {
        return NAME;
    }
}
