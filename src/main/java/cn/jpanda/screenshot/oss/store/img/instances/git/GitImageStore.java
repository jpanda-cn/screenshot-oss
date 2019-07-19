package cn.jpanda.screenshot.oss.store.img.instances.git;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ImgStore;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.store.ExceptionType;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.AbstractConfigImageStore;
import cn.jpanda.screenshot.oss.view.image.GitFileImageStoreConfig;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
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
        String accessPath = "";
        if (gitPersistence.getRemoteRepositoryUrl().endsWith(".git")) {
            accessPath = gitPersistence.getRemoteRepositoryUrl().substring(0, gitPersistence.getRemoteRepositoryUrl().length() - ".git".length());
        } else {
            accessPath = gitPersistence.getRemoteRepositoryUrl();
        }
        // 追加分支
        return accessPath + ("/raw/" + gitPersistence.getBranch() + "/" + subPath);
    }

    private void save(GitPersistence gitPersistence, BufferedImage image, String suffix, String path, String name) {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitPersistence.getUsername(), gitPersistence.getPassword());

        Git git = createGit(usernamePasswordCredentialsProvider, gitPersistence, image, suffix, path, name);
        if (git == null) {
            return;
        }
        if (checkSubDir(gitPersistence, image, path)
                && saveLocal(image, suffix, path)
                && add2Git(git, gitPersistence, image, path)
                && gitPull(git, usernamePasswordCredentialsProvider, image, path)
                && gitAdd(git, gitPersistence, image, path)
                && gitCommit(git, image, suffix, path, name)
                && gitPush(git, usernamePasswordCredentialsProvider, image, path)

        ) {
            return;
        }

    }

    @Override
    public boolean retry(ImageStoreResultWrapper imageStoreResultWrapper) {
        ExceptionType exceptionType = imageStoreResultWrapper.getExceptionType();
        if (exceptionType instanceof GitExceptionType) {
            GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);
            UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitPersistence.getUsername(), gitPersistence.getPassword());
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(Paths.get(imageStoreResultWrapper.getPath()).toFile());
            } catch (IOException e) {
                return false;
            }
            String path = imageStoreResultWrapper.getPath();
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            String name = path.substring((int) MathUtils.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1);
            name = name.substring(0, name.length() - suffix.length() - 1);
            Git git = createGit(usernamePasswordCredentialsProvider, gitPersistence, bufferedImage, suffix, path, name);
            if (git == null) {
                return false;
            }
            if (exceptionType.getLevel() >= GitExceptionType.CANT_CREATE_SUB_DIRECTORY.getLevel()) {
                if (!checkSubDir(gitPersistence, bufferedImage, path)) {
                    return false;
                }
            }
            if (exceptionType.getLevel() >= GitExceptionType.CANT_SAVE_TO_LOCAL.getLevel()) {
                if (!saveLocal(bufferedImage, suffix, path)) {
                    return false;
                }
            }
            if (exceptionType.getLevel() >= GitExceptionType.CANT_ADD_GIT_FILE.getLevel()) {
                if (!add2Git(git, gitPersistence, bufferedImage, path)) {
                    return false;
                }
            }

            if (exceptionType.getLevel() >= GitExceptionType.CANT_UPDATE.getLevel()) {
                if (!gitPull(git, usernamePasswordCredentialsProvider, bufferedImage, path)) {
                    return false;
                }
            }
            if (exceptionType.getLevel() >= GitExceptionType.CANT_COMMIT.getLevel()) {
                if (!gitCommit(git, bufferedImage, suffix, path, name)) {
                    return false;
                }
            }
            if (exceptionType.getLevel() >= GitExceptionType.CANT_PUSH.getLevel()) {
                return gitPush(git, usernamePasswordCredentialsProvider, bufferedImage, path);
            }
        }
        return true;
    }

    private Git createGit(UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider, GitPersistence gitPersistence, BufferedImage image, String suffix, String path, String name) {
        // 基础校验
        Git git;
        if (isCloned(gitPersistence)) {
            try {
                git = Git.open(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile());
            } catch (IOException e) {
                addException(image, path, false, e, GitExceptionType.CANT_CREATE_FILE);
                return null;
            }
        } else {
            CloneCommand cloneCommand = Git.cloneRepository();
            try {
                git = cloneCommand
                        .setURI(gitPersistence.getRemoteRepositoryUrl())
                        .setBranch(gitPersistence.getBranch())
                        .setDirectory(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile())
                        .setCredentialsProvider(usernamePasswordCredentialsProvider)
                        .call();
            } catch (GitAPIException | JGitInternalException e) {
                addException(image, path, false, e, GitExceptionType.CANT_CREATE_FILE);
                return null;
            }
        }
        return git;
    }

    private boolean checkSubDir(GitPersistence gitPersistence, BufferedImage image, String path) {
        Path sp = Paths.get(gitPersistence.getLocalRepositoryDir(), gitPersistence.getSubDir());
        File subDir = sp.toFile();

        if (!subDir.exists()) {
            try {
                Files.createDirectory(sp);
            } catch (IOException e) {
                addException(image, path, false, e, GitExceptionType.CANT_CREATE_SUB_DIRECTORY);
                return false;
            }
        }
        return true;
    }

    private boolean saveLocal(BufferedImage image, String suffix, String path) {
        // 本地图片存储
        try {
            save(image, suffix, path);
        } catch (RuntimeException | IOException e) {
            addException(image, path, false, e, GitExceptionType.CANT_SAVE_TO_LOCAL);
            return false;
        }
        return true;
    }

    private boolean add2Git(Git git, GitPersistence gitPersistence, BufferedImage image, String path) {
        try {
            git.add().addFilepattern("./" + gitPersistence.getSubDir()).call();
        } catch (GitAPIException e) {
            addException(image, path, false, e, GitExceptionType.CANT_ADD_GIT_FILE);
            return false;
        }
        return true;
    }

    private boolean gitPull(Git git, UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider, BufferedImage image, String path) {
        // 更新仓库
        try {

            try {
                git.pull().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
            } catch (TransportException ignored) {
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
            addException(image, path, false, e, GitExceptionType.CANT_UPDATE);
            return false;
        }
        return true;
    }

    private boolean gitAdd(Git git, GitPersistence gitPersistence, BufferedImage image, String path) {
        // 现将图片存放到本地仓库中
        try {
            git.add().addFilepattern(StringUtils.isEmpty(gitPersistence.getSubDir()) ? "." : gitPersistence.getSubDir()).call();
        } catch (GitAPIException e) {
            addException(image, path, false, e, GitExceptionType.CANT_ADD_GIT_FILE);
            return false;
        }
        return true;
    }

    private boolean gitCommit(Git git, BufferedImage image, String suffix, String path, String name) {
        // 现将图片存放到本地仓库中
        // 提交代码
        try {
            git.commit()
                    .setMessage(String.format("add new image named:%s", name + "." + suffix))
                    .call();
        } catch (GitAPIException e) {
            addException(image, path, false, e, GitExceptionType.CANT_COMMIT);
            return false;
        }
        return true;
    }

    private boolean gitPush(Git git, UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider, BufferedImage image, String path) {
        // 推送到远程仓库
        try {
            git.push().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        } catch (GitAPIException e) {
            addException(image, path, false, e, GitExceptionType.CANT_PUSH);
            return false;
        }
        return true;
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

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
            ImageIO.write(image, suffix, file);
        }
        ImageIO.write(image, suffix, file);
    }

}
