<template>
  <el-timeline>
    <el-timeline-item
      v-for="log in logs"
      :key="log.id"
      :timestamp="formatTimeToNow(log.createTime)"
      :type="getType(log.operator)"
      :icon="getIcon(log.operator)"
      size="large"
    >
      <b>{{ log.action }}</b>（操作人：{{ showOperator(log.operator) }}）
    </el-timeline-item>
  </el-timeline>

  <el-empty v-if="!logs.length" description="暂无日志"></el-empty>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getAfterSaleLogs } from '@/api/afterSale'
import { formatTimeToNow } from '@/utils/time'
import { User, Shop, Setting } from '@element-plus/icons-vue'

const props = defineProps({ afterSaleId: Number })
const logs = ref([])

const getType = (o) => ({ user: 'primary', merchant: 'success', system: 'warning' }[o] || 'info')
const getIcon = (o) => ({ user: User, merchant: Shop, system: Setting }[o] || Setting)
const showOperator = (o) => ({ user: '用户', merchant: '商家', system: '系统' }[o])

watch(() => props.afterSaleId, loadLogs, { immediate: true })
async function loadLogs() {
  const res = await getAfterSaleLogs(props.afterSaleId)
  logs.value = res.data
}
</script>
