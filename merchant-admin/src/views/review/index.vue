<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header">
        <span style="font-size: 18px; font-weight: bold;">顾客评价管理</span>
      </div>

      <el-table :data="reviewList" v-loading="loading" border stripe>
        <el-table-column label="评价ID" prop="id" width="80" align="center" />
        <el-table-column label="订单ID" prop="orderId" width="120" align="center" />
        
        <el-table-column label="评价用户" width="200">
          <template slot-scope="scope">
            <div style="display: flex; align-items: center;">
              <el-avatar :src="scope.row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" size="small" />
              <span style="margin-left: 10px">{{ scope.row.nickname || '微信用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="评分等级" width="160" align="center">
          <template slot-scope="scope">
            <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>

        <el-table-column label="评价内容" prop="content" show-overflow-tooltip />

        <el-table-column label="评价时间" width="180" align="center">
          <template slot-scope="scope">
            {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ') : '' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      loading: false,
      reviewList: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await request.get('/api/review/list')
        this.reviewList = res.data || res
      } catch (error) {
        this.$message.error('评价列表加载失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>