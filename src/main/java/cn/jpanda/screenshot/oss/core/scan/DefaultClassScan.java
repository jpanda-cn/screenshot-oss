package cn.jpanda.screenshot.oss.core.scan;

import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private Log log = LogFactory.getLog(getClass());
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
        log.trace(String.format("init ClassLoadScan with:%s and %s", classLoader.getClass().getCanonicalName(), filter.getClass().getCanonicalName()));
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
        log.trace(String.format("start class scan with package named:%s ", packageName));
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
            log.trace(String.format("handler url:%s", currentUrl.getPath()));
            handler(currentUrl, packageName);
        }
    }


    protected void handler(URL url, String packageName) {
        switch (url.getProtocol()) {
            case JAR_PROTOCOL_NAME: {
                log.trace(String.format("%s is Jar", url.getPath()));
                handlerJar(url, packageName);
                break;
            }
            case FILE_PROTOCOL_NAME: {
                log.trace(String.format("%s is file", url.getPath()));
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
        log.trace(String.format("connection to %s ...", url.getPath()));
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
                System.out.println(jarEntryName);
                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                if (className.startsWith(packageName)) {
                    log.trace(String.format(String.format("registry class named %s", className)));
                    addClassByName(className);
                }
                continue;
            }
            log.trace(String.format("the file named %s is not class file", jarEntryName));
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
            log.trace(String.format("dir:%s can`t handler", dir.getName()));
            return;
        }
        File[] subFiles = dir.listFiles(pathname -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(CLASS_PROTOCOL_NAME)));
        if (null == subFiles) {
            return;
        }
        for (File file : subFiles) {
            if (file.isDirectory()) {
                log.trace(String.format("handler %s dir`s subDir %s", url.getPath(), file.getName()));
                handler(new URL(url.getProtocol(), url.getHost(), url.getPort(), file.getPath()), packageName + "." + file.getName());
                continue;
            }
            if (file.getName().endsWith(".class")) {
                log.trace(String.format("handler class file with package:%s and class named: %s", packageName, file.getName()));
                handlerClassFile(packageName, file.getName().substring(0, file.getName().length() - 6));
                continue;
            }
            log.info(String.format("continue %s/%s", packageName, file.getName()));
        }
    }

    protected void handlerClassFile(String packageName, String fileName) {
        String className = packageName + "." + fileName;
        log.trace(String.format("will registry class %s", className));
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
        log.trace(String.format("load class named %s", className));
        return classLoader.loadClass(className);
    }
}

