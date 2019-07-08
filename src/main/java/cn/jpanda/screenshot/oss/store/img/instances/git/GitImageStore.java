package cn.jpanda.screenshot.oss.store.img.instances.git;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.GitFileImageStoreConfig;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 将图片存放到git中
 */
@ImgStore(name = GitImageStore.NAME, config = GitFileImageStoreConfig.class)
public class GitImageStore extends AbstractConfigImageStore {


    public static final String NAME = "GIT";
    private Log log;

    public GitImageStore(Configuration configuration) {
        super(configuration);
        log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public boolean canUse() {
        GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);
        return StringUtils.isNotEmpty(gitPersistence.getLocalRepositoryDir())
                && StringUtils.isNotEmpty(gitPersistence.getRemoteRepositoryUrl())
                && StringUtils.isNotEmpty(gitPersistence.getUsername())
                && StringUtils.isNotEmpty(gitPersistence.getPassword());
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String store(BufferedImage image) {
        GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);

        String suffix = "png";
        String name = fileNameGenerator();
        String subPath = gitPersistence.getSubDir() + "/" + name + "." + suffix;
        String path = Paths.get(gitPersistence.getLocalRepositoryDir(), subPath).toAbsolutePath().toString();
        if (gitPersistence.isAsync()) {
            // 开辟新线程执行保存操作
            new Thread(() -> {
                save(gitPersistence, image, suffix, path, name);
            }).start();
        } else {
            save(gitPersistence, image, suffix, path, name);
        }
        return gitPersistence.getRemoteRepositoryUrl() + "/" + subPath;
    }

    private void save(GitPersistence gitPersistence, BufferedImage image, String suffix, String path, String name) {
        try {
            UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitPersistence.getUsername(), gitPersistence.getPassword());

            Git git;
            if (isCloned(gitPersistence)) {
                git = Git.open(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile());
            } else {
                CloneCommand cloneCommand = Git.cloneRepository();
                git = cloneCommand
                        .setURI(gitPersistence.getRemoteRepositoryUrl())
                        .setBranch(gitPersistence.getBranch())
                        .setDirectory(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile())
                        .setCredentialsProvider(usernamePasswordCredentialsProvider)
                        .call();
            }
            // 存储图片到本地仓库
            // 获取保存图片类型

            // 本地图片存储
            Path sp = Paths.get(gitPersistence.getLocalRepositoryDir(), gitPersistence.getSubDir());
            File subDir = sp.toFile();

            if (!subDir.exists()) {
                Files.createDirectory(sp);
                git.add().addFilepattern("./" + gitPersistence.getSubDir()).call();
            }
            save(image, suffix, path);
            // 更新仓库
            git.pull().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
            // 现将图片存放到本地仓库中
            git.add().addFilepattern(StringUtils.isEmpty(gitPersistence.getSubDir()) ? "." : gitPersistence.getSubDir()).call();
            // 提交代码
            git.commit()
                    .setMessage(String.format("add new image named:%s", name + "." + suffix))
                    .call();
            // 推送到远程仓库
            git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
            // 转换为git地址
        } catch (Exception e) {
            configuration.getUniqueBean(ImageStoreResultHandler.class).add(ImageStoreResult
                    .builder()
                    .image(new SimpleObjectProperty<>(image))
                    .imageStore(new SimpleStringProperty(NAME))
                    .path(new SimpleStringProperty(path))
                    .success(new SimpleBooleanProperty(false))
                    .exception(new SimpleObjectProperty<>(e))
                    .build());
        }

    }

    @SneakyThrows
    private boolean isCloned(GitPersistence gitPersistence) {
        File file = Paths.get(gitPersistence.getLocalRepositoryDir(), ".git").toFile();
        return file.exists() && file.isDirectory();
    }

    protected String fileNameGenerator() {
        return UUID.randomUUID().toString();
    }

    protected void save(BufferedImage image, String suffix, String path) throws IOException {
        // 本地图片存储
        File file = Paths.get(path).toFile();
        if (!file.exists() && file.mkdirs() && file.createNewFile()) {
            ImageIO.write(image, suffix, file);
        } else {
            throw new RuntimeException(String.format("can not create file named:%s", path));
        }
    }


}
