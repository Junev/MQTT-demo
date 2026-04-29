// MQTT API client
import { API_CONFIG, getApiUrl } from './config';

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface PublishRequest {
  topic?: string;
  message: string;
}

export class MqttApi {
  private baseUrl = getApiUrl(API_CONFIG.ENDPOINTS.MQTT);

  /**
   * 发送MQTT消息
   */
  async publishMessage(request: PublishRequest): Promise<void> {
    const response = await fetch(`${this.baseUrl}/publish-message`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
    const result: ApiResponse<void> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
  }
}

export const mqttApi = new MqttApi();