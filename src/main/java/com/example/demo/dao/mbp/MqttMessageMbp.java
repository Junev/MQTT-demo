package com.example.demo.dao.mbp;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mqtt_message")
public class MqttMessageMbp {

    private String message;

    private Date saveTime;

    private Boolean isdeleted;
}
