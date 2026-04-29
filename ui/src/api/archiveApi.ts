// Archive API client
import type { MqttMessage } from '../types/archive';
import { API_CONFIG, getApiUrl } from './config';

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface MqttMessageExample {
  // Define the structure based on your backend
  // This is a placeholder - adjust according to your actual MqttMessageExample
  [key: string]: any;
}

export class ArchiveApi {
  private baseUrl = getApiUrl(API_CONFIG.ENDPOINTS.ARCHIVE);

  /**
   * 查询所有MQTT消息
   */
  async getAllMessages(): Promise<MqttMessage[]> {
    const response = await fetch(`${this.baseUrl}/messages`);
    const result: ApiResponse<MqttMessage[]> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
    return result.data;
  }

  /**
   * 根据ID查询MQTT消息
   */
  async getMessageById(id: number): Promise<MqttMessage> {
    const response = await fetch(`${this.baseUrl}/messages/${id}`);
    const result: ApiResponse<MqttMessage> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
    return result.data;
  }

  /**
   * 根据条件统计MQTT消息数量
   */
  async countMessages(example: MqttMessageExample): Promise<number> {
    const response = await fetch(`${this.baseUrl}/messages/count`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(example),
    });
    const result: ApiResponse<number> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
    return result.data;
  }

  /**
   * 新增MQTT消息
   */
  async addMessage(message: MqttMessage): Promise<void> {
    const response = await fetch(`${this.baseUrl}/messages`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(message),
    });
    const result: ApiResponse<void> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
  }

  /**
   * 删除MQTT消息
   */
  async deleteMessage(id: number): Promise<void> {
    const response = await fetch(`${this.baseUrl}/messages/${id}`, {
      method: 'DELETE',
    });
    const result: ApiResponse<void> = await response.json();
    if (result.code !== 200) {
      throw new Error(result.message);
    }
  }
}

export const archiveApi = new ArchiveApi();