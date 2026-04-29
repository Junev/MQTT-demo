import mqtt from 'mqtt';
// 测试可用公共的 MQTT Broker，broker.emqx.io
const brokerUrl = import.meta.env.VITE_MQTT_BROKER_URL; 

const options = {
  clientId: 'web_client_' + Math.random().toString(16).substr(2, 8),
  username: 'emqtest',      // 如果需要认证，在这里填写
  password: 'passwd~!@eClient.10#$%^&*()_+',
  clean: true,
  keepalive: 60,
  reconnectPeriod: 1000, // 1秒重连一次
};

let client = null;
const getClient = (    
    handleMessage=()=>{},
    handleError=()=>{}
    ) =>  {
    if (client) return client;
    client = mqtt.connect(brokerUrl, options);

    client.on('connect', (
    ) => {
    console.log('连接成功');
    // 订阅主题
    client.subscribe('liyiyuantopic', { qos: 1 }, (err) => {
        if (!err) console.log('订阅成功');
    });
    // 发布消息
    // client.publish('liyiyuantopic', 'Hello from browser', { qos: 1 });
    });

    client.on('message', (topic, message) => {
    console.log('收到消息:', topic, message.toString());
    handleMessage(message.toString());
    });

    client.on('error', (err) => {
    console.error('连接错误:', err);
    handleError(err);
    });
}



export {
    getClient
}