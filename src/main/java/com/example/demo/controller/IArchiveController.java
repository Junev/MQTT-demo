package com.example.demo.controller;

import com.example.demo.dao.MqttMessageExample;
import com.example.demo.entity.ApiResponse;
import com.example.demo.entity.archive.MqttMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IArchiveController {

    /**
     * 查询所有MQTT消息
     * @return 消息列表
     */
    ResponseEntity<ApiResponse<List<MqttMessage>>> getAllMessages();

    /**
     * 根据ID查询MQTT消息
     * @param id 消息ID
     * @return 消息对象
     */
    ResponseEntity<ApiResponse<MqttMessage>> getMessageById(@PathVariable Integer id);

    /**
     * 根据条件统计MQTT消息数量
     * @param example 查询条件
     * @return 消息数量
     */
    ResponseEntity<ApiResponse<Long>> countMessages(@RequestBody MqttMessageExample example);

    /**
     * 新增MQTT消息
     * @param message 消息对象
     * @return 操作结果
     */
    ResponseEntity<ApiResponse<Void>> addMessage(@RequestBody MqttMessage message);

    /**
     * 删除MQTT消息
     * @param id 消息ID
     * @return 操作结果
     */
    ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Integer id);
}
