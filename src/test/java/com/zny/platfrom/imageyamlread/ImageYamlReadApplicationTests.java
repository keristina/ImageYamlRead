package com.zny.platfrom.imageyamlread;

import com.zny.platfrom.imageyamlread.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ImageYamlReadApplicationTests {

    @Autowired
    RedisService service;
    @Test
    void contextLoads() throws IOException {
//        service.readYaml("D:\\program\\ImageYamlRead\\src\\main\\resources\\autoApplication.yml");
    }

}
