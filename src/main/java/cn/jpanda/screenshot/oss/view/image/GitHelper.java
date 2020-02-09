package cn.jpanda.screenshot.oss.view.image;

import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.util.FS;

import java.io.File;

public class GitHelper {

    /**
     * 判断指定文件夹是否为GIT仓库
     *
     * @param file 文件
     * @return 是否为GIT仓库
     */
    public static boolean isGitRepository(File file) {
        return RepositoryCache.FileKey.resolve(file, FS.DETECTED) != null;
    }

    /**
     * 判断指定文件夹是否为GIT仓库
     *
     * @param file 文件
     * @return 是否为GIT仓库
     */
    public static boolean isGitRepository(String file) {
        return RepositoryCache.FileKey.resolve(new File(file), FS.DETECTED) != null;
    }

    /**
     * 判断指定文件夹是否为GIT仓库
     *
     * @param file 文件
     * @param fs   文件系统
     * @return 是否为GIT仓库
     */
    public static boolean isGitRepository(File file, FS fs) {
        return RepositoryCache.FileKey.resolve(file, fs) != null;
    }

    /**
     * 判断指定文件夹是否为GIT仓库
     *
     * @param file 文件
     * @param fs   文件系统
     * @return 是否为GIT仓库
     */
    public static boolean isGitRepository(String file, FS fs) {
        return RepositoryCache.FileKey.resolve(new File(file), fs) != null;
    }


    /**
     * 打开指定仓库，如果指定仓库不存在则clone仓库到指定目录
     *
     * @param file                仓库目录
     * @param credentialsProvider 登录凭证
     * @param url                 仓库远程地址
     * @param branch              分支
     * @return Git对象
     */
    @SneakyThrows
    public static Git load(File file
            , CredentialsProvider credentialsProvider
            , String url
            , String branch
    ) {
        if (isGitRepository(file)) {
            return Git.open(file);
        }
        return Git.cloneRepository()
                .setURI(url)
                .setBranch(branch)
                .setDirectory(file)
                .setCredentialsProvider(credentialsProvider)
                .setNoCheckout(false)
                .call();
    }
}
