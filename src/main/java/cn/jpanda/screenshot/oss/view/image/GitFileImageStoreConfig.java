package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.git.GitPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Controller
public class GitFileImageStoreConfig implements Initializable {
    public static final String DEFAULT_REPOSITORY_DIRECTOR_NAME = "screenshot";
    /**
     * 本地仓库地址
     */
    public TextField localRepositoryDir;
    /**
     * 子目录
     */
    public TextField subDir;
    /**
     * 远程仓库地址
     */
    public TextField remoteRepositoryUrl;
    /**
     * 分支名称
     */
    public TextField branch;
    /**
     * 用户名
     */
    public TextField username;
    /**
     * 密码
     */
    public PasswordField password;

    /**
     * 是否异步上传
     */
    public CheckBox async;
    public Button cancel;
    public Button save;
    public Button choseLocalRepositoryDir;
    public Button choseSubDir;

    private Configuration configuration;

    public GitFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean update = false;
        GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);
        if (StringUtils.isEmpty(gitPersistence.getLocalRepositoryDir())) {
            gitPersistence.setLocalRepositoryDir(Paths.get(configuration.getWorkPath()).toFile().getAbsolutePath());
            update = true;
        }
        if (StringUtils.isEmpty(gitPersistence.getSubDir())) {
            gitPersistence.setSubDir("");
        }
        if (update) {
            configuration.storePersistence(gitPersistence);
        }
        // 本地仓库地址
        localRepositoryDir.editableProperty().setValue(false);
        localRepositoryDir.textProperty().setValue(gitPersistence.getLocalRepositoryDir());
        localRepositoryDir.tooltipProperty().setValue(new Tooltip(gitPersistence.getLocalRepositoryDir()));

        // 子目录
        subDir.textProperty().setValue(gitPersistence.getSubDir());
        // 远程仓库地址
        remoteRepositoryUrl.textProperty().setValue(gitPersistence.getRemoteRepositoryUrl());
        remoteRepositoryUrl.tooltipProperty().setValue(new Tooltip(gitPersistence.getRemoteRepositoryUrl()));

        // 分支名称
        branch.textProperty().setValue(gitPersistence.getBranch());

        // 用户名
        username.textProperty().setValue(gitPersistence.getUsername());
        // 密码
        password.textProperty().setValue(gitPersistence.getPassword());

        // 是否异步上传
        async.selectedProperty().setValue(gitPersistence.isAsync());
        async.tooltipProperty().setValue(new Tooltip("是否异步上传"));

    }

    public void choseLocalRepositoryDir() {
        localRepositoryDir.textProperty().setValue(chose("请选择本地仓库地址", localRepositoryDir.textProperty().get()));
    }

    public String chose(String title, String oldPath) {
        // 获取当前地址
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get(oldPath).toFile());
        directoryChooser.setTitle(title);
        File newDir = directoryChooser.showDialog(configuration.getViewContext().newStage());
        if (newDir == null) {
            return oldPath;
        }
        String newPath = newDir.getAbsolutePath();
        if (StringUtils.isEmpty(newPath)) {
            return oldPath;
        }
        if (newPath.equals(oldPath)) {
            return oldPath;
        }
        return newPath;
    }

    public void save() {
        GitPersistence old = configuration.getPersistence(GitPersistence.class);
        GitPersistence gitPersistence = new GitPersistence();
        String localRep = localRepositoryDir.textProperty().get();
        String subDirName = subDir.textProperty().get();
        String remote = remoteRepositoryUrl.textProperty().get();
        String bra = branch.textProperty().get();
        String un = username.textProperty().get();
        String pd = password.textProperty().get();
        boolean asy = async.selectedProperty().get();
        // 数据校验
        if (!Paths.get(localRep).toFile().exists()) {
            AlertUtils.alert(Alert.AlertType.ERROR, "本地仓库目录不存在");
            return;
        }
        if (StringUtils.isEmpty(remote) || !Pattern.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", remote)) {
            AlertUtils.alert(Alert.AlertType.ERROR, "远程仓库地址格式错误");
            return;
        }
        gitPersistence.setLocalRepositoryDir(localRep);
        gitPersistence.setSubDir(subDirName);
        gitPersistence.setRemoteRepositoryUrl(remote);
        gitPersistence.setBranch(bra);
        gitPersistence.setUsername(un);
        gitPersistence.setPassword(pd);
        gitPersistence.setAsync(asy);

        // 生成访问口令
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider(gitPersistence.getUsername(), gitPersistence.getPassword());
        Git git;
        try {
            // 判断是否需要重新Clone工程
            boolean needCloned = !isCloned(gitPersistence);
            needCloned = needCloned || !gitPersistence.getLocalRepositoryDir().equals(old.getLocalRepositoryDir());
            // 校验是否需要重新Clone工程
            if (!needCloned) {
                try {
                    git = Git.open(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile());
                } catch (IOException e) {
                    throw new RuntimeException(String.format("仓库:{%s}已存在，但无法打开!\r\n建议在备份数据后，清空该目录，并重试该步骤。\r\n!!!请注意备份数据,部分异常数据为:%s", Paths.get(gitPersistence.getLocalRepositoryDir()).toFile().getAbsolutePath(), e.getMessage()));
                }
            } else {
                git = cloned(gitPersistence, usernamePasswordCredentialsProvider);
                old.setLocalRepositoryDir(gitPersistence.getLocalRepositoryDir());
            }

            boolean anyChange = false;
            // 判断是否需要变更远程路径
            if (!gitPersistence.getRemoteRepositoryUrl().equals(old.getRemoteRepositoryUrl())) {
                List<RemoteConfig> list = git.remoteList().call();
                for (RemoteConfig rc : list) {
                    git.remoteRemove().setRemoteName(rc.getName()).call();
                }
                git.remoteSetUrl().setRemoteUri(new URIish(gitPersistence.getRemoteRepositoryUrl())).call();
                git.branchRename().setNewName(old.getBranch()).call();
                old.setRemoteRepositoryUrl(gitPersistence.getRemoteRepositoryUrl());
                anyChange = true;
                // ping
                git.lsRemote().call();
            }

            // 是否需要修改分支名称
            if (!gitPersistence.getBranch().equals(old.getBranch())) {
                if (gitPersistence.getBranch().trim().equals("")) {
                    gitPersistence.setBranch("master");
                }
                if (git.branchList().call().stream().noneMatch((ref -> gitPersistence.getBranch().equals(ref.getName())))) {
                    git.branchRename().setNewName(gitPersistence.getBranch()).call();
                    old.setBranch(gitPersistence.getBranch());
                    anyChange = true;
                }
            }
            if (anyChange) {
                git.pull().call();
            }
            // 本地图片存储
            Path sp = Paths.get(gitPersistence.getLocalRepositoryDir(), gitPersistence.getSubDir());
            File subDir = sp.toFile();

            if (!subDir.exists()) {
                try {
                    Files.createDirectory(sp);
                } catch (IOException e) {
                    throw new RuntimeException(String.format("无法创建图片存储子目录,请检查配置，部分异常数据为:%s", e.getMessage()));
                }
                try {
                    git.add().addFilepattern("./" + gitPersistence.getSubDir()).call();
                } catch (GitAPIException e) {
                    throw new RuntimeException(String.format("无法提交子目录到Git仓库，部分异常数据为:%s\"", e.getMessage()));
                }
            }
        } catch (Exception e) {
            // 不能完成配置，执行弹窗提示
            e.printStackTrace();
            AlertUtils.alert(Alert.AlertType.WARNING, e.getMessage());
            configuration.storePersistence(old);
            return;
        }
        try {

            git.status().call();
        } catch (Exception e) {
            throw new RuntimeException(String.format("无法保存Git配置，部分异常数据为:%s\"", e.getMessage()));
        }

        configuration.storePersistence(gitPersistence);
        close();
    }

    public void cancel() {
        close();
    }

    public void close() {
        // 取消
        ((Stage) async.getScene().getWindow()).close();
    }

    private Git cloned(GitPersistence gitPersistence, UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider) {
        CloneCommand cloneCommand = Git.cloneRepository();
        try {
            Git git = cloneCommand
                    .setURI(gitPersistence.getRemoteRepositoryUrl())
                    .setBranch(gitPersistence.getBranch())
                    .setDirectory(Paths.get(gitPersistence.getLocalRepositoryDir()).toFile())
                    .setCredentialsProvider(usernamePasswordCredentialsProvider)
                    .call();
            git.fetch().call();
            return git;
        } catch (GitAPIException | JGitInternalException e) {
            throw new RuntimeException(String.format("无法Clone仓库,请检查配置，部分异常数据为:%s", e.getMessage()));
        }
    }

    @SneakyThrows
    private boolean isCloned(GitPersistence gitPersistence) {
        File file = Paths.get(gitPersistence.getLocalRepositoryDir(), ".git").toFile();
        return file.exists() && file.isDirectory();
    }
}
