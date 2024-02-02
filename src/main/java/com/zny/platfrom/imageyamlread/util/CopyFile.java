package com.zny.platfrom.imageyamlread.util;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Administrator
 */
@Component
public class CopyFile {

    private static final String SUFFIX_YAML = ".yaml";


    public void copyFileByPath(String a, String b) throws IOException {
        copyFile(new File(a), new File(b));
    }

    public void copyFile(File sourceDir, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();
            assert files != null;
            for (File file : files) {
                String name = file.getName();
                if (file.isFile()) {
                    if (!name.endsWith(SUFFIX_YAML)){
                        continue;
                    }
                    Files.copy(file.toPath(), Paths.get(targetDir.getPath() + "/" + name));
                }else if (file.isDirectory()) {
                    File newTargetDir = new File(targetDir, file.getName());
                    copyFile(file, newTargetDir);
                }
            }
        }
    }

}
