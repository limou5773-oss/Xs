<template>
  <div class="review-page">
    <el-card class="page-card">

      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">累计评价数</div>
          <div class="stat-value">{{ stat.total }}</div>
          <div class="stat-tag">当前筛选范围内</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">五星好评</div>
          <div class="stat-value">{{ stat.fiveStar }}</div>
          <div class="stat-tag">满分评价数量</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">中差评数</div>
          <div class="stat-value">{{ stat.badReview }}</div>
          <div class="stat-tag">3星及以下评价</div>
        </div>
      </div>

      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="query.keyword"
            placeholder="搜索：订单编号 / 用户昵称"
            size="small"
            clearable
            prefix-icon="Search"
            class="filter-input"
          />

          <el-select
            v-model="query.rating"
            size="small"
            placeholder="评分等级"
            clearable
            class="filter-select"
          >
            <el-option label="五星好评" :value="5" />
            <el-option label="四星满意" :value="4" />
            <el-option label="三星一般" :value="3" />
            <el-option label="二星较差" :value="2" />
            <el-option label="一星极差" :value="1" />
          </el-select>
        </div>

        <div class="filter-right">
          <el-button type="primary" size="small" @click="doSearch">
            <el-icon class="btn-icon"><Search /></el-icon> 筛选
          </el-button>

          <el-button size="small" @click="resetSearch">重置</el-button>
          
          <el-button size="small" type="success" plain @click="refresh">
            <el-icon class="btn-icon"><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </div>

      <el-table
        :data="reviewList"
        v-loading="loading"
        border
        stripe
        size="small"
        class="review-table"
      >
        <el-table-column label="订单编号" prop="orderNo" min-width="160" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            <span style="color: #606266;">{{ row.orderNo || '未知单号' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="评价用户" width="200">
          <template #default="{ row }">
            <div class="user-box">
              <el-avatar 
                :size="30" 
                :src="row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
              />
              <span class="user-name">{{ row.nickname || '微信用户' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="评分等级" width="160" align="center">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled text-color="#ff9900" />
          </template>
        </el-table-column>

        <el-table-column label="评价内容" prop="content" min-width="250">
          <template #default="{ row }">
            <span class="content-text">{{ row.content }}</span>
          </template>
        </el-table-column>

        <el-table-column label="评价时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box" v-if="row.createTime">
              <div>{{ row.createTime.replace('T', ' ').split(' ')[0] }}</div>
              <div class="time-sub">{{ row.createTime.replace('T', ' ').split(' ')[1] }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, jumper, ->, total"
          :total="total"
          :current-page="query.page"
          :page-size="query.size"
          @current-change="changePage"
        />
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const reviewList = ref([])
const total = ref(0) 

// 搜索/分页参数
const query = ref({
  page: 1,
  size: 10,
  keyword: "",
  rating: null
})

// 👇 新增：自动计算统计卡片的数据
const stat = computed(() => ({
  total: reviewList.value.length,
  fiveStar: reviewList.value.filter(item => item.rating === 5).length,
  badReview: reviewList.value.filter(item => item.rating <= 3).length
}))

// 获取评价数据
const getReviews = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/review/list')
    let allData = res.data || res || []
    
    // 前端模拟筛选
    if (query.value.rating) {
      allData = allData.filter(item => item.rating === query.value.rating)
    }
    
    reviewList.value = allData
    total.value = allData.length
  } catch (error) {
    console.error('获取评价失败:', error)
    ElMessage.error('无法加载评价数据')
  } finally {
    loading.value = false
  }
}

/* 按钮操作逻辑 */
const doSearch = () => {
  query.value.page = 1
  getReviews()
}

const resetSearch = () => {
  query.value = { page: 1, size: 10, keyword: "", rating: null }
  getReviews()
}

const refresh = () => {
  getReviews()
}

const changePage = (p) => {
  query.value.page = p
  getReviews() 
}

onMounted(() => {
  getReviews()
})
</script>

<style scoped>
/* ================================ 1. 页面及卡片基础 ================================ */
.review-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
  min-height: calc(100vh - 120px);
}

/* ================================ 2. 统计卡片 (Stat Cards) 新增 ================================ */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  padding: 14px 18px;
  border-radius: 10px;
  background: linear-gradient(135deg, #fff, #f7f9ff);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
  transition: .18s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.06);
}

/* 装饰圆圈 */
.stat-card::after {
  content: "";
  position: absolute;
  right: -40px;
  bottom: -40px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  opacity: .18;
}

.total::after { background: #409eff; } /* 蓝色 */
.up::after { background: #67c23a; } /* 绿色 */
.down::after { background: #fa5151; } /* 红色 (差评警告) */

.stat-title {
  font-size: 13px;
  color: #909399; 
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
  margin: 4px 0;
}

.stat-tag {
  font-size: 12px;
  color: #c0c4cc; 
}

/* ================================ 3. 筛选栏 (Filter Bar) ================================ */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f7f9fc; 
  border-radius: 10px;
  border: 1px solid #eef1f6;
  margin-bottom: 14px;
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-input { width: 220px; }
.filter-select { width: 140px; }

.btn-icon {
  margin-right: 4px;
}

.filter-bar :deep(.el-input__inner),
.filter-bar :deep(.el-input__wrapper) {
  font-size: 14px !important;
}

/* ================================ 4. 表格主体 (Table) ================================ */
.review-table {
  border-radius: 10px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  font-size: 15px !important;
}

:deep(.el-table__row td) {
  font-size: 15px !important;
  padding: 14px 0 !important;
}

.review-table :deep(.el-table__row) {
  transition: .15s ease;
}

.review-table :deep(.el-table__row:hover) {
  background: #f5f7ff !important;
}

/* ================================ 5. 复杂字段显示 ================================ */
.user-box {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 10px;
  font-weight: 500;
  color: #333;
}

.content-text {
  color: #606266;
  line-height: 1.5;
  display: inline-block;
  max-width: 400px;
}

/* 双行时间显示 */
.time-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.3;
}
.time-sub {
  font-size: 12px;
  color: #909399;
}

/* ================================ 6. 分页 (Pager) ================================ */
.pager {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}
</style>