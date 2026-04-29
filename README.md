# 工程简介

从MQTT服务订阅消息，保存到数据库里



## 初始化 MQTT服务
```bash
docker pull emqx/emqx:latest
docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 18083:18083 emqx/emqx:latest
```

## 初始化SQL Server数据库
执行 `sql/init.sql`

## 启动程序
`mvn spring-boot:run`

