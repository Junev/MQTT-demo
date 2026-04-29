package com.example.demo.controller.impl;

import com.example.demo.controller.IArchiveController;
import com.example.demo.dao.MqttMessageExample;
import com.example.demo.entity.ApiResponse;
import com.example.demo.entity.archive.MqttMessage;
import com.example.demo.service.IArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MQTT消息归档保存到数据库，并提供增删查改功能
 */
@RestController
@RequestMapping("/api/archive")
public class ArchiveControllerImpl implements IArchiveController {

    @Autowired
    private IArchiveService archiveService;

    @Override
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<MqttMessage>>> getAllMessages() {
        try {
            List<MqttMessage> messages = archiveService.selectAll();
            return ResponseEntity.ok(ApiResponse.success("查询成功", messages));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }


    @Override
    @GetMapping("/messages/{id}")
    public ResponseEntity<ApiResponse<MqttMessage>> getMessageById(@PathVariable Integer id) {
        try {
            MqttMessage message = archiveService.selectByPrimaryKey(id);
            if (message != null) {
                return ResponseEntity.ok(ApiResponse.success("查询成功", message));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Override
    @PostMapping("/messages/count")
    public ResponseEntity<ApiResponse<Long>> countMessages(@RequestBody MqttMessageExample example) {
        try {
            long count = archiveService.countByExample(example);
            return ResponseEntity.ok(ApiResponse.success("统计成功", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("统计失败: " + e.getMessage()));
        }
    }

    @Override
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<Void>> addMessage(@RequestBody MqttMessage message) {
        try {
            int result = archiveService.insert(message);
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("添加成功"));
            } else {
                return ResponseEntity.internalServerError().body(ApiResponse.error("添加失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("添加失败: " + e.getMessage()));
        }
    }

    @Override
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Integer id) {
        try {
            int result = archiveService.deleteByPrimaryKey(id);
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("删除成功"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }
}
