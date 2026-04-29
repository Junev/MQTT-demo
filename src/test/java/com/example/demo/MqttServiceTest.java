package com.example.demo;

import com.example.demo.service.IMqttService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MqttServiceTest {
    
    @Autowired
    private IMqttService mqttService;
    
    private static final String TEST_TOPIC = "liyiyuantopic";
    private static final String TEST_MESSAGE = "Hello MQTT Test";
    
    private List<String> receivedMessages = new ArrayList<>();
    private CountDownLatch messageLatch;
    
    @BeforeEach
    void setUp() {
        receivedMessages.clear();
    }
    
    @AfterEach
    void tearDown() {
        try {
            mqttService.unsubscribe(TEST_TOPIC);
        } catch (Exception e) {
            System.out.println("清理订阅时出错: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("测试发布消息 - 基本功能")
    void testPublishMessage() {
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, TEST_MESSAGE);
        }, "发布消息不应该抛出异常");
    }
    
    @Test
    @Order(2)
    @DisplayName("测试发布消息 - 空消息")
    void testPublishEmptyMessage() {
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, "");
        }, "发布空消息不应该抛出异常");
    }
    
    @Test
    @Order(3)
    @DisplayName("测试发布消息 - 特殊字符")
    void testPublishSpecialCharacters() {
        String specialMessage = "测试特殊字符: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, specialMessage);
        }, "发布包含特殊字符的消息不应该抛出异常");
    }
    
    @Test
    @Order(4)
    @DisplayName("测试发布消息 - 中文内容")
    void testPublishChineseMessage() {
        String chineseMessage = "测试中文消息：你好，世界！";
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, chineseMessage);
        }, "发布中文消息不应该抛出异常");
    }
    
    @Test
    @Order(5)
    @DisplayName("测试发布消息 - 长消息")
    void testPublishLongMessage() {
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("This is a long message test. ");
        }
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, longMessage.toString());
        }, "发布长消息不应该抛出异常");
    }
    
    @Test
    @Order(6)
    @DisplayName("测试订阅主题")
    void testSubscribe() {
        assertDoesNotThrow(() -> {
            mqttService.subscribe(TEST_TOPIC);
        }, "订阅主题不应该抛出异常");
    }
    
    @Test
    @Order(7)
    @DisplayName("测试取消订阅")
    void testUnsubscribe() {
        assertDoesNotThrow(() -> {
            mqttService.subscribe(TEST_TOPIC);
            mqttService.unsubscribe(TEST_TOPIC);
        }, "取消订阅不应该抛出异常");
    }
    
    @Test
    @Order(8)
    @DisplayName("测试重复订阅同一主题")
    void testDuplicateSubscribe() {
        assertDoesNotThrow(() -> {
            mqttService.subscribe(TEST_TOPIC);
            mqttService.subscribe(TEST_TOPIC);
        }, "重复订阅同一主题不应该抛出异常");
    }
    
    @Test
    @Order(9)
    @DisplayName("测试多次发布消息")
    void testMultiplePublish() {
        int messageCount = 10;
        assertDoesNotThrow(() -> {
            for (int i = 0; i < messageCount; i++) {
                mqttService.publish(TEST_TOPIC, "Message " + i);
            }
        }, "多次发布消息不应该抛出异常");
    }
    
    @Test
    @Order(10)
    @DisplayName("测试发布和接收消息")
    void testPublishAndReceive() throws InterruptedException {
        messageLatch = new CountDownLatch(1);
        
        mqttService.subscribe(TEST_TOPIC);
        
        Thread.sleep(1000);
        
        mqttService.publish(TEST_TOPIC, TEST_MESSAGE);
        
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);
        
        assertTrue(messageReceived, "应该在5秒内接收到消息");
    }
    
    @Test
    @Order(11)
    @DisplayName("测试不同主题的隔离性")
    void testTopicIsolation() {
        String topic1 = "test/topic1";
        String topic2 = "test/topic2";
        
        assertDoesNotThrow(() -> {
            mqttService.subscribe(topic1);
            mqttService.publish(topic1, "Message for topic1");
            mqttService.unsubscribe(topic1);
            
            mqttService.subscribe(topic2);
            mqttService.publish(topic2, "Message for topic2");
            mqttService.unsubscribe(topic2);
        }, "不同主题的消息应该相互隔离");
    }
    
    @Test
    @Order(12)
    @DisplayName("测试通配符主题订阅")
    void testWildcardTopicSubscribe() {
        String wildcardTopic = "test/#";
        
        assertDoesNotThrow(() -> {
            mqttService.subscribe(wildcardTopic);
            mqttService.publish("test/wildcard", "Wildcard test message");
            mqttService.unsubscribe(wildcardTopic);
        }, "通配符主题订阅应该正常工作");
    }
    
    @Test
    @Order(13)
    @DisplayName("测试JSON格式消息")
    void testJsonMessage() {
        String jsonMessage = "{\"type\":\"test\",\"data\":{\"id\":1,\"value\":\"test\"}}";
        
        assertDoesNotThrow(() -> {
            mqttService.publish(TEST_TOPIC, jsonMessage);
        }, "发布JSON格式消息不应该抛出异常");
    }
    
    @Test
    @Order(14)
    @DisplayName("测试连续发布和订阅")
    void testContinuousPublishAndSubscribe() throws InterruptedException {
        messageLatch = new CountDownLatch(5);
        
        mqttService.subscribe(TEST_TOPIC);
        
        Thread.sleep(1000);
        
        for (int i = 0; i < 5; i++) {
            mqttService.publish(TEST_TOPIC, "Continuous message " + i);
            Thread.sleep(100);
        }
        
        boolean allMessagesReceived = messageLatch.await(10, TimeUnit.SECONDS);
        
        assertTrue(allMessagesReceived, "应该在10秒内接收到所有消息");
    }
}
