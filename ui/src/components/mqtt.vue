<template>
  <div class="layout-grid">
    <div class="panel panel-left">
      <h2>发送消息</h2>
      <el-input
            v-model="textarea"
            type="textarea"
            placeholder="请输入..."
            class="textarea-flexible"
    />
      <div class="left-panel-actions">
        <el-button type="primary" class="send-button" @click="sendMessage">API接口发送</el-button>
      </div>
    </div>
    <div class="panel panel-right">
      <div class="header-container">
        <h2>存档的消息</h2>
        <el-button type="primary" @click="fetchArchiveMessages">
          <el-icon><Refresh /></el-icon>
          <span>刷新</span>
        </el-button>
      </div>
      <el-table
        :data="tableData"
        height="calc(100% - 40px)"
        style="width: 100%"
        class="archive-table"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="message" label="消息内容" show-overflow-tooltip />
        <el-table-column prop="saveTime" label="保存时间" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArchiveMessage } from '../types/archive'
import { onMounted, ref } from 'vue'
import { ElNotification } from 'element-plus'
import { mqttApi } from '../api/mqttApi'
import { archiveApi } from '../api/archiveApi'
import { getClient } from '../utils/mqtt'
import { Refresh } from '@element-plus/icons-vue'

const textarea = ref('')
const tableData = ref<ArchiveMessage[]>([])

// 获取归档消息列表
const fetchArchiveMessages = async () => {
  try {
    const response = await archiveApi.getAllMessages()
    // 将返回的数据转换为表格所需的格式
    tableData.value = response.map(item => ({
      id: item.id || 0,
      message: item.message,
      saveTime: item.saveTime ? new Date(item.saveTime).toLocaleString() : '-' // 转换日期格式
    }))
  } catch (error) {
    ElNotification({
      title: '错误',
      message: error instanceof Error ? error.message : '获取归档消息失败',
      type: 'error',
      duration: 3000,
      position: 'bottom-left',
    })
  }
}

// 页面加载完成后获取归档消息
onMounted(() => {
  fetchArchiveMessages()
})

const sendMessage = async () => {
  if (!textarea.value.trim()) {
    ElNotification({ title: '警告', message: '请输入消息内容', type: 'warning', position: 'bottom-left', })
    return
  }

  try {
    await mqttApi.publishMessage({ message: textarea.value })
    // 成功时显示通知提示
    ElNotification({ 
      title: '成功', 
      message: '消息发送成功', 
      type: 'success',
      duration: 3000,
      position: 'bottom-left',
    })
    // 清空输入框
    textarea.value = ''
    // 刷新归档消息列表
    fetchArchiveMessages()
  } catch (error) {
    ElNotification({ 
      title: '错误', 
      message: error instanceof Error ? error.message : '发送失败', 
      type: 'error',
      duration: 3000,
      position: 'bottom-left',
    })
  }
}

// onMounted(() => {
//   getClient(
//     (message: string) => {
//       console.log('Websocket 接收到 mqtt消息 ', message)
//       ElNotification({ 
//         title: 'Websocket 消息', 
//         message: `收到消息: ${message}`, 
//         type: 'success',
//         duration: 3000,
//         position: 'bottom-right',
//     })
//     },
//   )
// })
</script>

<style scoped>
.layout-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 100%;
  height: 100vh; /* 确保网格占满整个视窗高度 */
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel {
  padding: 16px;
  border: 1px solid var(--el-border-color, #d9d9d9);
  border-radius: 8px;
  background: var(--el-color-white, #fff);
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.panel-left {
  background-color: #f5f7ff;
  display: flex;
  flex-direction: column;
}

.textarea-flexible {
  flex: 1; /* 填充剩余空间 */
  display: flex;
  flex-direction: column;
  min-height: 200px; /* 最小高度 */
}

:deep(.el-textarea) {
  height: 100%; /* 让textarea占据全部父元素高度 */
}

:deep(.el-textarea__inner) {
  height: 100%;
  resize: vertical;
  min-height: 200px;
}

.left-panel-actions {
  margin-top: 16px;
}

.send-button {
  width: 100%;
  font-size: 1rem;
}

.panel-right {
  background-color: var(--el-bg-color-page);
  display: flex;
  flex-direction: column;
}

.archive-table {
  flex: 1;
  margin-top: 16px;
  overflow-y: auto;
}
</style>