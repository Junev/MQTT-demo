package com.example.demo;

import com.example.demo.dao.mbp.MqttMessageMbp;
import com.example.demo.dao.mbp.MqttMessageMbpMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
public class MqttMessageMbpTest {

    @Autowired
    private MqttMessageMbpMapper mqttMessageMbpMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<MqttMessageMbp> mqttMessageMbpList = mqttMessageMbpMapper.selectList(null);
        Assert.isTrue(10 == mqttMessageMbpList.size(), "查询数据个数有误");
        mqttMessageMbpList.forEach(System.out::println);
    }
}
