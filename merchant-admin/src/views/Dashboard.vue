<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <h3>今日订单数</h3>
          <p class="num">{{ data.todayOrders }}</p>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <h3>今日销售额</h3>
          <p class="num">¥{{ data.todaySales }}</p>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <h3>待处理售后</h3>
          <p class="num">{{ data.pendingAfterSales }}</p>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <h3>上架商品数</h3>
          <p class="num">{{ data.productCount }}</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单趋势图 -->
    <el-card style="margin-top: 20px;">
      <h3>📈 最近7天订单趋势</h3>
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardSummary } from '@/api/dashboard'

const data = ref({
  todayOrders: 0,
  todaySales: 0,
  pendingAfterSales: 0,
  productCount: 0,
  // [{ date: '2025-11-30', count: 3 }, ...]
  recentOrders: []
})

const chartRef = ref(null)
let chart = null

// 初始化 / 更新图表
const initChart = () => {
  if (!chartRef.value) return

  if (!chart) {
    chart = echarts.init(chartRef.value)
  }

  const days = data.value.recentOrders.map(i => i.date)
  const counts = data.value.recentOrders.map(i => i.count)

  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: days,
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#ccc' } },
      axisLabel: { color: '#666' },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: counts,
        areaStyle: {},   // 渐变面积
        symbolSize: 6
      }
    ]
  })

  chart.resize()
}

// 加载仪表盘数据
const load = async () => {
  try {
    const res = await getDashboardSummary(1)
    const body = res && res.code !== undefined ? res : res.data
    const d = body && body.data ? body.data : body || {}

    console.log('仪表盘 summary 接口返回：', d)

    data.value = {
      todayOrders: d.todayOrders || 0,
      todaySales: d.todaySales || 0,
      // 后端字段是 pendingAfterSale，这里做个兼容
      pendingAfterSales: d.pendingAfterSales ?? d.pendingAfterSale ?? 0,
      // 后端字段是 onSaleProduct，这里也兼容一下
      productCount: d.productCount ?? d.onSaleProduct ?? 0,
      // 你现在后端还没返回 recentOrders，这里容错一下
      recentOrders: Array.isArray(d.recentOrders) ? d.recentOrders : []
    }

    await nextTick()
    initChart()
  } catch (e) {
    console.error('加载仪表盘失败', e)
  }
}

const handleResize = () => {
  if (chart) chart.resize()
}

onMounted(() => {
  load()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.num {
  font-size: 26px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}
</style>
