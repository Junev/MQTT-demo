// Type definitions for archive entities

export interface MqttMessage {
  id?: number;
  message: string;
  saveTime?: Date;
  isdeleted?: boolean;
}

export interface ArchiveMessage {
  id: number;
  message: string;
  saveTime: string; // 或者 Date，取决于API返回的格式
}

export interface MqttMessageExample {
  // Define query conditions based on your backend
  // This is a placeholder - adjust according to your actual MqttMessageExample
  [key: string]: any;
}