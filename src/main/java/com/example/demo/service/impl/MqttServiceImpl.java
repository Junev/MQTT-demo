package com.example.demo.service.impl;

import com.example.demo.config.MqttConfig;
import com.example.demo.mqtt.MyMqttClient;
import com.example.demo.service.IMqttService;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
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
    private MqttConnectionOptions connectOptions;

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MqttMessageCallback mqttMessageCallback;

    /**
     * 初始化MQTT客户端, 订阅l主题
     */
    @PostConstruct
    public void init() {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.setCallback(mqttMessageCallback);
                mqttClient.connect(connectOptions);
            }

            String topic = mqttConfig.getTopic();
            if (topic != null && !topic.isEmpty()) {
                subscribe(topic);
            } else {
                logger.warn("未配置MQTT订阅主题");
            }
        } catch (MqttException e) {
            logger.error("MQTT客户端连接失败", e);
        }
    }

    @Override
    public void subscribe(String topic) {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect(connectOptions);
            }

            mqttClient.subscribe(topic, MyMqttClient.getQos());

//            logger.info("订阅成功 - Topic: {}", topic);
        } catch (MqttException e) {
//            logger.error("订阅失败 - Topic: {}", topic, e);
            throw new RuntimeException("订阅失败", e);
        }
    }

    @Override
    public void unsubscribe(String topic) {
        try {
            if (!mqttClient.isConnected()) {
                return;
            }

            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
//            logger.error("取消订阅失败 - Topic: {}", topic, e);
            throw new RuntimeException("取消订阅失败", e);
        }
    }

    @Override
    public void publish(String topic, String message) {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect(connectOptions);
            }

            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(MyMqttClient.getQos());
            mqttMessage.setRetained(false);

            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
//            logger.error("消息发布失败 - Topic: {}", topic, e);
            throw new RuntimeException("消息发布失败", e);
        }
    }

    @Override
    public void publish(String message) {
        publish(mqttConfig.getTopic(), message);
    }
}
