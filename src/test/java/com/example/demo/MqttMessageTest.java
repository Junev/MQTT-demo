package com.example.demo;

import com.example.demo.dao.MqttMessageExample;
import com.example.demo.dao.mapper.MqttMessageMapper;
import com.example.demo.entity.archive.MqttMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
public class MqttMessageTest {

    @Autowired
    private MqttMessageMapper mapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        MqttMessageExample mqttMessageExample = new MqttMessageExample();
        mqttMessageExample.createCriteria().andIdIsNotNull();
        List<MqttMessage> mqttMessages = mapper.selectByExample(mqttMessageExample);
        Assert.isTrue(10 == mqttMessages.size(), "查询数据个数有误");
        mqttMessages.forEach(System.out::println);
    }
}
