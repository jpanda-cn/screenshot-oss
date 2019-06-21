package cn.jpanda.screenshot.oss.store.img.instances.git;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.GitFileImageStoreConfig;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 将图片存放到git中
 */
@ImgStore(name = GitImageStore.NAME, config = GitFileImageStoreConfig.class)
public class GitImageStore extends AbstractConfigImageStore {
    static final String NAME = "GIT";

    public GitImageStore(Configuration configuration) {
        super(configuration);
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
    @SneakyThrows
    public String store(BufferedImage image) {
        GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);

        String suffix = "png";
        String name = fileNameGenerator();
        String subPath = gitPersistence.getSubDir() + "/" + name + "." + suffix;
        String path = Paths.get(gitPersistence.getLocalRepositoryDir(), subPath).toAbsolutePath().toString() + ("." + suffix);
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

    @SneakyThrows
    private void save(GitPersistence gitPersistence, BufferedImage image, String suffix, String path, String name) {
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

    }

    @SneakyThrows
    private boolean isCloned(GitPersistence gitPersistence) {
        File file = Paths.get(gitPersistence.getLocalRepositoryDir(), ".git").toFile();
        return file.exists() && file.isDirectory();
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
