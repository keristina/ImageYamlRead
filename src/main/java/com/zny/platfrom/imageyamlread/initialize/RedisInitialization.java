package com.zny.platfrom.imageyamlread.initialize;

import com.zny.platfrom.imageyamlread.service.RedisService;
import com.zny.platfrom.imageyamlread.util.CopyFile;
import com.zny.platfrom.imageyamlread.util.UnpackJar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

import static com.zny.platfrom.imageyamlread.service.RedisService.suffix;


/**
 * @author Fengxinxin
 */
@Component
public class RedisInitialization {

    final RedisService redisService;
    final UnpackJar unpackJar;

    final CopyFile copyFile;

    @Value("${yamlPath}")
    String yamlPath;

    @Value("${valuesFile}")
    String valuesFile;

    @Value("${consulFile}")
    String consulFile;

    @Value("${serviceFile}")
    String serviceFile;

    @Value("${pvcFile}")
    String pvcFile;

    @Value("${deploymentsFile}")
    String deploymentsFile;


    @Value("${file-path}")
    String path;

    @Value("${jobRecordId}")
    String jobRecordId;

    @Value("${name_prefix}")
    String name_prefix;

    String SUFFIX_YAML = ".yaml";

    @Value("${project.type}")
    String projectType;

    public RedisInitialization(RedisService redisService, UnpackJar unpackJar, CopyFile copyFile) {
        this.redisService = redisService;
        this.unpackJar = unpackJar;
        this.copyFile = copyFile;
    }

    @PostConstruct
    public void init() throws IOException {
        //提取自动创建的相关配置文件到yamlPath路径下
        if ("jar".equals(projectType)){
            unpackJar.extra(path,yamlPath,false,null);
        }else if (".net".equals(projectType)){
            copyFile.copyFileByPath(path,yamlPath);
        }
        //读取yamlPath下的配置文件到redis中
        redisService.storeYamlToRedis(name_prefix+jobRecordId+":values",yamlPath+"/"+valuesFile+SUFFIX_YAML);
        redisService.storeFileToRedis(name_prefix+jobRecordId+":consul",yamlPath+"/"+consulFile+SUFFIX_YAML);
        redisService.storeFileToRedis(name_prefix+jobRecordId+":pvc",yamlPath+"/"+pvcFile+SUFFIX_YAML);
        redisService.storeFileToRedis(name_prefix+jobRecordId+":service",yamlPath+"/"+serviceFile+SUFFIX_YAML);
        redisService.storeFileToRedis(name_prefix+jobRecordId+":deployments",yamlPath+"/"+deploymentsFile+SUFFIX_YAML);
        // 在这里编写你希望在初始化完成时执行的代码
        System.out.println("自动配置文件保存完成");
        String values = redisService.getStr(suffix+name_prefix+jobRecordId+":values");
        String consul = redisService.getStr(suffix+name_prefix+jobRecordId+":consul");
        String pvc = redisService.getStr(suffix+name_prefix+jobRecordId+":pvc");
        String service = redisService.getStr(suffix+name_prefix+jobRecordId+":service");
        String deployments = redisService.getStr(suffix+name_prefix+jobRecordId+":deployments");
        System.out.println(suffix+jobRecordId+":values:"+values);
        System.out.println(suffix+jobRecordId+":consul:"+consul);
        System.out.println(suffix+jobRecordId+":pvc:"+pvc);
        System.out.println(suffix+jobRecordId+":service:"+service);
        System.out.println(suffix+jobRecordId+":deployments:"+deployments);
    }
}
