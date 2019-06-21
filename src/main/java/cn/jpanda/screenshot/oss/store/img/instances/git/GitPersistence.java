package cn.jpanda.screenshot.oss.store.img.instances.git;

import cn.jpanda.screenshot.oss.core.annotations.Encrypt;
import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import lombok.Data;

@Data
@Profile
public class GitPersistence implements Persistence {
    /**
     * 本地仓库地址
     */
    private String localRepositoryDir;
    /**
     * 子目录名称
     */
    private String subDir="";
    /**
     * 远程仓库地址
     */
    private String remoteRepositoryUrl;
    /**
     * 分支
     */
    private String branch;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    @Encrypt
    private String password;

    /**
     * 是否异步上传
     */
    private boolean async = true;
}
