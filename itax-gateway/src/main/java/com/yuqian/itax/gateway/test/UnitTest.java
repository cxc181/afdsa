package com.yuqian.itax.gateway.test;

import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;
/**
 * @Description 单元测试类
 * @Author  Kaven
 * @Date   2020/7/28 09:55
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UnitTest {
    @Autowired
    private OssService ossService;
    @Autowired
    private DictionaryService dictionaryService;

    @Test
    public void test(){
        String bucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        if (!ossService.doesObjectExist("YCS/20200724/1595558517732.png", bucket)) {
            System.out.println("不存在");
        } else {
            System.out.println("存在");
        }
    }
}
