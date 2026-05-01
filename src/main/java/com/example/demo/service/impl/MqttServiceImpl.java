package com.example.demo.service.impl;

import com.example.demo.config.MqttConfig;
import com.example.demo.mqtt.MqttReconnectManager;
import com.example.demo.mqtt.MyMqttClient;
import com.example.demo.service.IMqttService;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * MQTT服务，提供发布和订阅、取消订阅功能
 */
@Service
public class MqttServiceImpl implements IMqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttServiceImpl.class);

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MqttReconnectManager mqttReconnectManager;

    /**
     * 初始化MQTT客户端, 订阅主题
     */
    @PostConstruct
    public void init() {
        // 尝试连接到MQTT服务器
        mqttReconnectManager.attemptConnection();

        String topic = mqttConfig.getTopic();
        if (topic != null && !topic.isEmpty()) {
            subscribe(topic);
        } else {
            logger.warn("未配置MQTT订阅主题");
        }
    }

    @Override
    public void subscribe(String topic) {
        try {
            // 检查连接状态，如果未连接则尝试重连
            if (!mqttClient.isConnected()) {
                logger.warn("MQTT客户端未连接，正在尝试重新连接...");
                mqttReconnectManager.attemptConnection();
                if (!mqttClient.isConnected()) {
                    logger.error("无法连接到MQTT服务器");
                    throw new RuntimeException("MQTT客户端连接失败");
                }
                logger.info("MQTT客户端重新连接成功");
            }

            mqttClient.subscribe(topic, MyMqttClient.getQos());
            logger.info("成功订阅主题: {}", topic);
        } catch (MqttException e) {
            logger.error("订阅失败 - Topic: {}, 错误: {}", topic, e.getMessage());
            throw new RuntimeException("订阅失败", e);
        }
    }

    @Override
    public void unsubscribe(String topic) {
        try {
            if (!mqttClient.isConnected()) {
                logger.warn("MQTT客户端未连接，无法取消订阅");
                return;
            }

            mqttClient.unsubscribe(topic);
            logger.info("成功取消订阅主题: {}", topic);
        } catch (MqttException e) {
            logger.error("取消订阅失败 - Topic: {}, 错误: {}", topic, e.getMessage());
            throw new RuntimeException("取消订阅失败", e);
        }
    }

    @Override
    public void publish(String topic, String message) {
        try {
            // 检查连接状态，如果未连接则尝试重连
            if (!mqttClient.isConnected()) {
                logger.warn("MQTT客户端未连接，正在尝试重新连接...");
                mqttReconnectManager.attemptConnection();
                if (!mqttClient.isConnected()) {
                    logger.error("无法连接到MQTT服务器");
                    throw new RuntimeException("MQTT客户端连接失败");
                }
                logger.info("MQTT客户端重新连接成功");
            }

            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(MyMqttClient.getQos());
            mqttMessage.setRetained(false);

            mqttClient.publish(topic, mqttMessage);
            logger.info("成功发布消息到主题: {}, 消息内容: {}", topic, message);
        } catch (MqttException e) {
            logger.error("消息发布失败 - Topic: {}, 错误: {}", topic, e.getMessage());
            throw new RuntimeException("消息发布失败", e);
        }
    }

    @Override
    public void publish(String message) {
        publish(mqttConfig.getTopic(), message);
    }
}
