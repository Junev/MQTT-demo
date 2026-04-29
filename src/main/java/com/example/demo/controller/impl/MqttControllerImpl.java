package com.example.demo.controller.impl;

import com.example.demo.controller.IMessageController;
import com.example.demo.entity.ApiResponse;
import com.example.demo.service.IMqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 发布MQTT消息
 */
@RestController
@RequestMapping("/api/mqtt")
public class MqttControllerImpl implements IMessageController {

    private static final Logger logger = LoggerFactory.getLogger(MqttControllerImpl.class);

    @Autowired
    private IMqttService mqttService;


    @Override
    @PostMapping("/publish-message")
    public ResponseEntity<ApiResponse<Void>> publishMessage(PublishRequest request) {
        if (request.getMessage() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("消息内容不能为空"));
        }

        mqttService.publish(request.getMessage());
        return ResponseEntity.ok(
                ApiResponse.success("消息发布成功")
        );
    }
}
