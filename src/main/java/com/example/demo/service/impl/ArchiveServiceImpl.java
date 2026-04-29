package com.example.demo.service.impl;

import com.example.demo.dao.MqttMessageExample;
import com.example.demo.entity.archive.MqttMessage;
import com.example.demo.dao.mapper.MqttMessageMapper;
import com.example.demo.service.IArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MQTT消息归档服务
 */
@Service
public class ArchiveServiceImpl implements IArchiveService {

    @Autowired
    private MqttMessageMapper mqttMessageMapper;

    @Override
    public List<MqttMessage> selectAll() {
        return mqttMessageMapper.selectByExample(new MqttMessageExample());
    }

    @Override
    public List<MqttMessage> selectByExample(MqttMessageExample example) {
        return mqttMessageMapper.selectByExample(example);
    }

    @Override
    public MqttMessage selectByPrimaryKey(Integer id) {
        return mqttMessageMapper.selectByPrimaryKey(id);
    }

    @Override
    public long countByExample(MqttMessageExample example) {
        return mqttMessageMapper.countByExample(example);
    }

    @Override
    public int insert(MqttMessage record) {
        return mqttMessageMapper.insert(record);
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return mqttMessageMapper.deleteByPrimaryKey(id);
    }
}
