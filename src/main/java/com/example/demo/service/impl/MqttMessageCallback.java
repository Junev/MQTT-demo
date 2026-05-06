package com.example.demo.service.impl;

import com.example.demo.mqtt.MqttReconnectManager;
import com.example.demo.service.IArchiveService;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * MQTT消息处理回调
 * 用于注册消息处理器
 */
@Component
public class MqttMessageCallback implements MqttCallback {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageCallback.class);

    @Autowired
    IArchiveService archiveService;
    
    @Autowired
    @Lazy
    private MqttReconnectManager mqttReconnectManager;


    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        logger.warn("MQTT连接断开: {}", mqttDisconnectResponse != null ? mqttDisconnectResponse.getReasonString() : "未知原因");
        logger.info("开始尝试重新连接...");
        mqttReconnectManager.attemptReconnection();
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        logger.error("MQTT错误", e);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        logger.info("收到MQTT消息 - Topic: {}, Message: {}", topic, payload);
        handleMessage(topic, payload);
    }

    /**
     * 处理收到的MQTT消息，保存到数据库里
     * @param topic
     * @param payload
     */
    private void handleMessage(String topic, String payload) {
//        logger.debug("处理MQTT消息 - Topic: {}, Message: {}", topic, payload);

        com.example.demo.entity.archive.MqttMessage record = new com.example.demo.entity.archive.MqttMessage();
        record.setMessage(payload);
        record.setSaveTime(new Date());
        record.setIsdeleted(false);
        archiveService.insert(record);
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {

    }

    @Override
    public void connectComplete(boolean b, String s) {
        logger.info("MQTT连接完成 - reconnect: {}, serverURI: {}", b, s);
        
        // 连接完成后，确保订阅到所需主题
        mqttReconnectManager.subscribeToTopic();
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {

    }
}