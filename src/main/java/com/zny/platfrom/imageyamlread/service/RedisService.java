package com.zny.platfrom.imageyamlread.service;

import com.zny.platfrom.imageyamlread.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zny.platfrom.imageyamlread.util.MapUtil.convertMapToYaml;

/**
 * @author Fengxinxin
 */
@Service
public class RedisService {

    public static String suffix = "yamlRead:";
    private final Yaml yaml;

    private final RedisTemplate<String, String> redisTemplate;

    static Gson gson = new Gson();

    public RedisService(Yaml yaml, RedisTemplate<String, String> redisTemplate) {
        this.yaml = yaml;
        this.redisTemplate = redisTemplate;
    }

    public void storeYamlToRedis(String key,String filePath) throws IOException {
        // 读取YAML文件
        FileInputStream inputStream = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        // 解析YAML内容
        LinkedHashMap<String, Object> yamlContent = yaml.load(isr);
        HashMap<String, String> properties = new HashMap<>();
        MapUtil.flattenMap("",yamlContent,properties);
        System.out.println(properties);
        // 存储键值对到Redis中
        redisTemplate.opsForValue().set(suffix +key,gson.toJson(properties),10, TimeUnit.MINUTES);
    }

    public void storeFileToRedis(String key,String filePath) {
        // 存储键值对到Redis中
        redisTemplate.opsForValue().set(suffix+key,readFileToString(filePath),10, TimeUnit.MINUTES);
    }

    public void storeYamlToRedisWithoutFlatten(String jobRecordId,String filePath) throws IOException {
        // 读取YAML文件
        FileInputStream inputStream = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        // 解析YAML内容
        Map<String, Object> yamlContent = yaml.load(isr);
        // 存储键值对到Redis中
//        System.out.println(convertMapToYaml(yamlContent,0));
        redisTemplate.opsForValue().set(jobRecordId,gson.toJson(yamlContent),10, TimeUnit.MINUTES);
    }

    public Map<String, String> getMap(String key) {
        return gson.fromJson(redisTemplate.opsForValue().get(key),HashMap.class);
    }

    public String getStr(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public static String readFileToString(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }catch (FileNotFoundException exception){
            System.out.println(filePath+"文件不存在");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
