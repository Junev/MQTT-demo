package com.example.demo.service;

public interface IMqttService {
    void publish(String topic, String message);

    void publish(String message);

    void subscribe(String topic);

    void unsubscribe(String topic);
}
