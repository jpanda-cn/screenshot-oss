package cn.jpanda.screenshot.oss.newcore.scan;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import cn.jpanda.screenshot.oss.newcore.scan.ClassScan;
import cn.jpanda.screenshot.oss.newcore.scan.ClassScanFilter;
import lombok.SneakyThrows;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 默认的类扫描器
 */
public class DefaultClassScan implements ClassScan {
    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());
    /**
     * 文件协议名称
     */
    protected static final String FILE_PROTOCOL_NAME = "file";
    protected static final String JAR_PROTOCOL_NAME = "jar";
    protected static final String CLASS_PROTOCOL_NAME = "class";

    /**
     * 类加载器
     */
    protected ClassLoader classLoader;
    /**
     * 类扫描过滤器
     */
    private ClassScanFilter filter;

    public DefaultClassScan(ClassScanFilter filter) {
        this(Thread.currentThread().getContextClassLoader(), filter);
    }

    public DefaultClassScan(ClassLoader classLoader, ClassScanFilter filter) {
        this.classLoader = classLoader;
        this.filter = filter;
    }

    /**
     * 符合条件的类集合
     */
    private Set<Class> classes;

    @Override
    public ClassScan scan(String packageName) {
        classes = new HashSet<>();
        loadClass(packageName);
        return this;
    }

    @Override
    public Set<Class> loadResult() {
        return classes;
    }

    @SneakyThrows
    protected void loadClass(String packageName) {
        Enumeration<URL> allFiles;
        allFiles = classLoader.getResources(packageName.replaceAll("\\.", "/"));
        while (allFiles.hasMoreElements()) {
            // 获取当前URL
            URL currentUrl = allFiles.nextElement();
            if (null == currentUrl) {
                continue;
            }
            handler(currentUrl, packageName);
        }
    }


    protected void handler(URL url, String packageName) {
        switch (url.getProtocol()) {
            case JAR_PROTOCOL_NAME: {
                handlerJar(url, packageName);
                break;
            }
            case FILE_PROTOCOL_NAME: {
                handlerFile(url, packageName);
                break;
            }
            default: {
                break;
            }
        }
    }

    @SneakyThrows
    protected void handlerJar(URL url, String packageName) {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        if (null == jarURLConnection) {
            return;
        }
        JarFile jarFile = jarURLConnection.getJarFile();
        if (null == jarFile) {
            return;
        }
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(".class")) {
                log.trace(jarEntryName);
                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                if (className.startsWith(packageName)) {
                    addClassByName(className);
                }
                continue;
            }
        }

    }

    @SneakyThrows
    protected void handlerFile(URL url, String packageName) {
        // 获取目录地址
        String pathStr = URLDecoder.decode(url.getPath(), "UTF-8").replaceAll("%20", "");
        if (pathStr.charAt(0) == '/') {
            pathStr = pathStr.substring(1);
        }
        Path path = Paths.get(pathStr);
        // 获取URL对应的文件
        File dir = path.toFile();
        // 文件不存在，文件非目录，文件不可读
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
            return;
        }
        File[] subFiles = dir.listFiles(pathname -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(CLASS_PROTOCOL_NAME)));
        if (null == subFiles) {
            return;
        }
        for (File file : subFiles) {
            if (file.isDirectory()) {
                handler(new URL(url.getProtocol(), url.getHost(), url.getPort(), file.getPath()), packageName + "." + file.getName());
                continue;
            }
            if (file.getName().endsWith(".class")) {
                handlerClassFile(packageName, file.getName().substring(0, file.getName().length() - 6));
                continue;
            }
        }
    }

    protected void handlerClassFile(String packageName, String fileName) {
        String className = packageName + "." + fileName;
        addClassByName(className);
    }

    protected void addClassByName(String className) {
        doAddCLass(className2Class(className));
    }

    protected void doAddCLass(Class clazz) {
        if (filter.doFilter(clazz)) {
            classes.add(clazz);
        }
    }

    @SneakyThrows
    protected Class className2Class(String className) {
        return classLoader.loadClass(className);
    }
}

