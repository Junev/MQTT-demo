package com.example.demo.service;

import com.example.demo.dao.MqttMessageExample;
import com.example.demo.entity.archive.MqttMessage;

import java.util.List;

public interface IArchiveService {

    List<MqttMessage> selectAll();

    List<MqttMessage> selectByExample(MqttMessageExample example);

    MqttMessage selectByPrimaryKey(Integer id);

    long countByExample(MqttMessageExample example);

    int insert(MqttMessage record);

    int deleteByPrimaryKey(Integer id);

}
