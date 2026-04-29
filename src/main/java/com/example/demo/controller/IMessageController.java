package com.example.demo.controller;

import com.example.demo.entity.ApiResponse;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IMessageController {

    /**
     * 发送MQTT消息
     * @param request 包含topic和message的请求体
     * @return 响应结果
     */
    ResponseEntity<ApiResponse<Void>> publishMessage(@RequestBody PublishRequest request);


    @Data
    public class PublishRequest {

        private String topic;

        private String message;
    }
}
