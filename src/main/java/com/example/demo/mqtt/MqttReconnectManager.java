package com.example.demo.mqtt;

import com.example.demo.config.MqttConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MQTT重连管理器，统一处理MQTT客户端的连接和重连逻辑
 */
@Slf4j
@Component
public class MqttReconnectManager {

    @Resource
    private MqttClient mqttClient;

    @Resource
    private MqttConnectionOptions connectionOptions;

    @Resource
    private MqttConfig mqttConfig;

    @Autowired
    private MqttCallback mqttCallback;

    // 添加一个标志，防止重复重连
    private volatile boolean isAttemptingReconnect = false;

    /**
     * 尝试连接到MQTT服务器
     */
    public void attemptConnection() {
        try {
            if (!mqttClient.isConnected()) {
                log.info("正在尝试连接到MQTT服务器...");
                
                // 如果客户端未连接，尝试连接
                mqttClient.setCallback(mqttCallback);
                mqttClient.connect(connectionOptions);
                
                // 连接成功后，订阅主题
                subscribeToTopic();
                
                log.info("成功连接到MQTT服务器");
            }
        } catch (MqttException e) {
            log.error("连接到MQTT服务器失败: {}", e.getMessage());
        }
    }

    /**
     * 订阅配置的主题
     */
    public void subscribeToTopic() {
        try {
            String topic = mqttConfig.getTopic();
            if (topic != null && !topic.isEmpty()) {
                if (mqttClient.isConnected()) {
                    mqttClient.subscribe(topic, MyMqttClient.getQos());
                    log.info("已订阅主题: {}", topic);
                } else {
                    log.warn("MQTT客户端未连接，无法订阅主题: {}", topic);
                }
            } else {
                log.warn("未配置MQTT订阅主题");
            }
        } catch (MqttException e) {
            log.error("订阅主题失败: {}", e.getMessage());
        }
    }

    /**
     * 尝试重连
     */
    public void attemptReconnection() {
        if (isAttemptingReconnect) {
            log.debug("已经在尝试重连过程中，跳过本次重连请求");
            return;
        }

        isAttemptingReconnect = true;
        Thread reconnectThread = new Thread(() -> {
            int retryCount = 0;
            final int maxRetryAttempts = 100; // 最大重试次数

            while (retryCount < maxRetryAttempts && !Thread.currentThread().isInterrupted()) {
                try {
                    log.info("正在进行第 {} 次重连尝试...", retryCount + 1);

                    // 如果客户端没有连接，则尝试连接
                    if (!mqttClient.isConnected()) {
                        // 断线后需要先关闭再重新连接
                        try {
                            mqttClient.close();
                        } catch (Exception e) {
                            log.debug("关闭旧连接时出现异常，继续进行重连: {}", e.getMessage());
                        }

                        // 重新创建客户端实例
                        MqttClient newClient = new MqttClient(
                                mqttConfig.getBroker(),
                                mqttConfig.getClientId(),
                                new MemoryPersistence()
                        );

                        // 设置回调
                        newClient.setCallback(mqttCallback);

                        // 连接
                        newClient.connect(connectionOptions);

                        // 替换旧的客户端引用
                        // 由于mqttClient是Spring管理的Bean，我们不能简单地替换引用
                        // 所以这里直接使用新连接的客户端
                        log.info("已创建新的MQTT连接");

                        // 重新订阅主题
                        String topic = mqttConfig.getTopic();
                        if (topic != null && !topic.isEmpty()) {
                            newClient.subscribe(topic, MyMqttClient.getQos());
                            log.info("已重新连接并订阅主题: {}", topic);
                        } else {
                            log.warn("未配置MQTT订阅主题，无法重新订阅");
                        }

                        log.info("重连成功!");
                        break; // 成功连接后退出循环
                    }

                } catch (MqttException e) {
                    retryCount++;
                    log.error("重连失败，将在3秒后进行第 {} 次重试 (最大尝试{}次): {}", retryCount, maxRetryAttempts, e.getMessage());

                    try {
                        Thread.sleep(3000); // 等待3秒后重试
                    } catch (InterruptedException ie) {
                        log.error("重连线程被中断");
                        Thread.currentThread().interrupt();
                        break;
                    }
                } finally {
                    if (mqttClient.isConnected()) {
                        isAttemptingReconnect = false;
                    }
                }
            }

            if (retryCount >= maxRetryAttempts) {
                log.error("达到最大重试次数，停止重连尝试");
                isAttemptingReconnect = false;
            }
        });

        reconnectThread.setName("MQTT-Reconnect-Thread");
        reconnectThread.setDaemon(true);
        reconnectThread.start();
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}