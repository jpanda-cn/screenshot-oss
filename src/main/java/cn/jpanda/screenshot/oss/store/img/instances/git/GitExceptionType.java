package cn.jpanda.screenshot.oss.store.img.instances.git;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class GitExceptionType extends ExceptionType {
    public static final GitExceptionType CANT_CREATE_FILE = new GitExceptionType("无法创建文件", 1);
    public static final GitExceptionType CANT_CREATE_SUB_DIRECTORY = new GitExceptionType("无法创建子目录", 2);
    public static final GitExceptionType CANT_SAVE_TO_LOCAL = new GitExceptionType("不能将图片保存到本地", 3);
    public static final GitExceptionType CANT_ADD_GIT_FILE = new GitExceptionType("无法将文件关联至GIT", 4);
    public static final GitExceptionType CANT_UPDATE = new GitExceptionType("无法更新GIT仓库", 5);
    public static final GitExceptionType CANT_COMMIT = new GitExceptionType("无法提交", 6);
    public static final GitExceptionType CANT_PUSH = new GitExceptionType("无法推送到远程仓库", 7);

    public GitExceptionType(String description, Integer level) {
        super(description, level);
    }

    public GitExceptionType() {

    }
}
