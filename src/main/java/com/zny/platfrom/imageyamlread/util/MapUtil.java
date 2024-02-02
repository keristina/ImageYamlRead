package com.zny.platfrom.imageyamlread.util;

import java.util.List;
import java.util.Map;

/**
 * @author Fengxinxin
 */
public class MapUtil {
    public static void flattenMap(String prefix, Map<String, Object> map, Map<String, String> properties) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if(value==null){
                properties.put(key,null);
                return;
            }
            if (value instanceof Map) {
                flattenMap(key, (Map<String, Object>) value, properties);
            } else {
                properties.put(key, value.toString());
            }
        }
    }
    public static String convertMapToYaml(Map<String, Object> map,int level) {
        StringBuilder yamlString = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                for (int i = 0; i < level; i++) {
                    yamlString.append("  ");
                }
                // 如果值是Map类型，则递归调用convertMapToYaml方法处理嵌套的Map
                String nestedYaml = convertMapToYaml((Map<String, Object>) value,level +1);
                yamlString.append(key).append(":").append("\n").append(nestedYaml);
            }
            if (value instanceof List) {
                for (int i = 0; i < level; i++) {
                    yamlString.append("  ");
                }
                // 如果值是Map类型，则递归调用convertMapToYaml方法处理嵌套的Map
                String nestedYaml = convertMapToYaml(((List<Map>) value).get(0),level +1);
                yamlString.append(key).append(":").append("\n").append(" -").append(nestedYaml.substring(2,nestedYaml.length()));
            }
            else {
                for (int i = 0; i < level; i++) {
                    yamlString.append("  ");
                }
                // 如果值不是Map类型，则直接拼接键值对
                yamlString.append(key).append(": ").append(value).append("\n");
            }
        }
        return yamlString.toString();
    }
}
