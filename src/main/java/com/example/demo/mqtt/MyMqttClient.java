package com.example.demo.mqtt;

import com.example.demo.config.MqttConfig;
import lombok.Getter;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyMqttClient {
    @Getter
    private static MqttConfig mqttConfig;
    @Getter
    private static final Integer qos = 0;

    @Autowired
    public void setMqttConfig(MqttConfig mqttConfig) {
        MyMqttClient.mqttConfig = mqttConfig;
    }

    @Bean
    MqttClient mqttClient() throws MqttException {
        String broker = mqttConfig.getBroker();
        String clientId = mqttConfig.getClientId();

        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
        return client;
    }

    @Bean
    MqttConnectionOptions mqttConnectOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setUserName(mqttConfig.getUsername());
        options.setPassword(mqttConfig.getPassword().getBytes());
        options.setAutomaticReconnect(true);
        // 设置连接超时时间
        options.setConnectionTimeout(30);
        // 设置心跳间隔
        options.setKeepAliveInterval(20);
        // 设置断开连接后的最大重试次数
        options.setMaxReconnectDelay(10000);
        // 设置清除会话
        options.setCleanStart(true);
        // 设置遗嘱消息（可选，但有助于识别客户端异常断开）
        // options.setWillMessage(...);
        return options;
    }

}