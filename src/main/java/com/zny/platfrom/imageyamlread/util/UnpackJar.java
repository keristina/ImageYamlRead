package com.zny.platfrom.imageyamlread.util;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author fxx
 */
@Component
public class UnpackJar {
    //需要提取的文件名后缀
    private static final String SUFFIX_YAML = ".yaml";

    private static final String SUFFIX_JAR = ".jar";

    //指定提取的文件路径
    private static final String PREFIX_SCRIPT_CHART = "/chart";

    //jar包固定文件路径
    private static final String PREFIX_SPRING_BOOT_CLASSES = "BOOT-INF/classes";


    private void extraJarStream(InputStream inputStream, String dir, boolean dep, boolean jarInit, List<String> skipFileList) throws IOException {
        JarEntry entry = null;
        JarInputStream jarInputStream = new JarInputStream(inputStream);
        while ((entry = jarInputStream.getNextJarEntry()) != null) {
            String name = entry.getName();
            if (name.endsWith(SUFFIX_YAML) && name.contains(PREFIX_SCRIPT_CHART)) {
                if (name.startsWith(PREFIX_SPRING_BOOT_CLASSES)) {
                    name = name.substring(PREFIX_SPRING_BOOT_CLASSES.length()+ PREFIX_SCRIPT_CHART.length());
                }
                File file = new File(dir + name);
                if (isSkipFile(skipFileList, file.getName())) {
                    file.deleteOnExit();
                    continue;
                }
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("create dir fail: " + file.getParentFile().getAbsolutePath());
                }
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    StreamUtils.copy(jarInputStream, outputStream);
                }
                continue;
            }
            if (name.endsWith(SUFFIX_JAR) && jarInit) {
                extraJarStream(jarInputStream, dir, true, true, skipFileList);
            }
        }
    }

    public void extra(String jar, String dir, boolean jarInit, String skipFile) throws IOException {
        boolean isUrl = (jar.startsWith("https://") || jar.startsWith("http://") || jar.startsWith("file://"));
        try (InputStream inputStream = isUrl ? (new URL(jar)).openStream() : Files.newInputStream(Paths.get(jar))) {
            File temp = new File(dir);
//            FileUtils.deleteDirectory(temp);
            if (!temp.mkdir()) {
                throw new IOException("create dir fail.");
            }
            List<String> skipFileList = null;
            if (StringUtils.hasLength(skipFile)) {
                skipFileList = Arrays.asList(skipFile.split(","));
            }
            extraJarStream(inputStream, dir, false, jarInit, skipFileList);
        }
    }

    private Boolean isSkipFile(List<String> skipFileList, String fileName) {
        boolean index = false;
        if (!CollectionUtils.isEmpty(skipFileList)) {
            for (String t : skipFileList) {
                if (t.equals(fileName)) {
                    index = true;
                    break;
                }
            }
        }
        return index;
    }

}