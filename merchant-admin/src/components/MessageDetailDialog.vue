<template>
  <el-dialog
    v-model="visible"
    width="480px"
    :title="detail.title"
  >
    <div class="content-box">
      <div class="content">{{ detail.content }}</div>
      <div class="time">{{ detail.createTime }}</div>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="onRead">标记已读</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineExpose } from 'vue'
import { markRead } from "@/api/message"

const visible = ref(false)
const detail = ref({})

function open(msg) {
  detail.value = msg
  visible.value = true
}

async function onRead() {
  await markRead(detail.value.id)
  visible.value = false
  emit("refresh")
}

defineExpose({ open })
</script>

<style scoped>
.content-box {
  padding: 16px;
}
.content {
  font-size: 15px;
  margin-bottom: 10px;
}
.time {
  font-size: 12px;
  color: #999;
}
</style>
