// API configuration
export const API_CONFIG = {
  // Backend API base URL
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',

  // API endpoints
  ENDPOINTS: {
    ARCHIVE: '/api/archive',
    MQTT: '/api/mqtt',
  },

  // Request timeout in milliseconds
  TIMEOUT: 10000,

  // HTTP headers
  HEADERS: {
    'Content-Type': 'application/json',
  },
} as const;

// Helper function to get full API URL
export const getApiUrl = (endpoint: string): string => {
  return `${API_CONFIG.BASE_URL}${endpoint}`;
};