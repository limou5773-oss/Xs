<template>
  <div class="category-page">
    <el-card class="page-card">

      <!-- 顶部统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">分类总数</div>
          <div class="stat-value">{{ stat.total }}</div>
          <div class="stat-tag">当前筛选范围内</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">本页分类</div>
          <div class="stat-value">{{ list.length }}</div>
          <div class="stat-tag">当前页显示数量</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">最近更新时间</div>
          <div class="stat-value">
            {{ latestDate || '-' }}<br>
            <span class="time-sub">{{ latestTime }}</span>
          </div>
          <div class="stat-tag">最近一条分类更新时间</div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <div class="filter-left">

          <el-input
            v-model="query.keyword"
            placeholder="搜索分类名称"
            size="small"
            clearable
            prefix-icon="Search"
            class="filter-input"
          />

          <el-date-picker
            v-model="dateRange"
            type="daterange"
            size="small"
            unlink-panels
            range-separator="-"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="filter-date"
          />
        </div>

        <div class="filter-right">
          <el-button type="primary" size="small" @click="search">
            <el-icon class="btn-icon"><Search /></el-icon> 搜索
          </el-button>

          <el-button size="small" @click="resetSearch">重置</el-button>

          <el-button
            size="small"
            type="success"
            class="add-btn"
            @click="openDialog()"
          >
            <el-icon class="btn-icon"><Plus /></el-icon> 新增分类
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :data="list"
        border
        stripe
        size="small"
        class="category-table"
      >
        <!-- 分类名称（居中） -->
        <el-table-column
          prop="name"
          label="分类名称"
          min-width="150"
          align="center"
          show-overflow-tooltip
        />

        <!-- 创建时间 -->
        <el-table-column label="创建时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.createTime) }}</div>
              <div class="time-sub">{{ formatTime(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 更新时间 -->
        <el-table-column label="更新时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.updateTime) }}</div>
              <div class="time-sub">{{ formatTime(row.updateTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openDialog(row)">
              编辑
            </el-button>

            <el-button size="small" type="danger" text @click="remove(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>

      </el-table>

      <!-- 分页 -->
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

    <!-- 新增 / 编辑对话框 -->
    <el-dialog v-model="dialogVisible" width="400px" @keydown.enter="saveCategory">
      <template #header>
        <h3 class="dialog-title">{{ dialogTitle }}</h3>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input
            v-model="form.name"
            maxlength="20"
            placeholder="请输入分类名称"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  getCategoryPage,
  addCategory,
  updateCategory,
  deleteCategory
} from '@/api/category'

const list = ref([])
const total = ref(0)

const query = ref({
  page: 1,
  size: 10,
  keyword: '',
  startTime: '',
  endTime: ''
})

const dateRange = ref(null)

/* 时间格式化 */
const formatDate = (t) => t ? dayjs(t).format("YYYY-MM-DD") : "-"
const formatTime = (t) => t ? dayjs(t).format("HH:mm:ss") : ""

/* 最近更新时间 */
const latestDate = computed(() =>
  list.value.length ? formatDate(list.value[0].updateTime || list.value[0].createTime) : "-"
)
const latestTime = computed(() =>
  list.value.length ? formatTime(list.value[0].updateTime || list.value[0].createTime) : ""
)

/* 顶部统计 */
const stat = computed(() => ({
  total: total.value
}))

/* 获取列表 */
const loadData = async () => {
  const params = { ...query.value }

  if (dateRange.value?.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  } else {
    params.startTime = ''
    params.endTime = ''
  }

  const res = await getCategoryPage(params)
  list.value = res.data.records || []
  total.value = res.data.total || 0
}

/* 搜索 */
const search = () => {
  query.value.page = 1
  loadData()
}

const resetSearch = () => {
  query.value = { page: 1, size: 10, keyword: '', startTime: '', endTime: '' }
  dateRange.value = null
  loadData()
}

const changePage = (p) => {
  query.value.page = p
  loadData()
}

/* 弹窗 */
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')

const form = ref({ id: null, name: '' })

const openDialog = (row) => {
  if (row) {
    dialogTitle.value = '编辑分类'
    form.value = { ...row }
  } else {
    dialogTitle.value = '新增分类'
    form.value = { id: null, name: '' }
  }
  dialogVisible.value = true
}

/* 保存 */
const saveCategory = async () => {
  if (!form.value.name.trim()) return ElMessage.warning('请输入分类名称')

  const api = form.value.id ? updateCategory : addCategory
  const res = await api(form.value)

  if (res.code === 200) {
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  }
}

/* 删除 */
const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该分类吗？', '提示', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.category-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31,45,61,0.06);
  border: none;
}

/* 顶部统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3,1fr);
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  padding: 14px 18px;
  border-radius: 10px;
  background: linear-gradient(135deg,#fff,#f7f9ff);
  box-shadow: 0 4px 16px rgba(0,0,0,0.03);
  transition: .18s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(0,0,0,0.06);
}

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

.total::after { background: #409eff; }
.up::after { background: #67c23a; }
.down::after { background: #e6a23c; }

.stat-title {
  font-size: 13px;
  color: #909399;
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
}

.stat-tag {
  font-size: 12px;
  color: #c0c4cc;
}

.time-sub {
  font-size: 12px;
  color: #909399;
}

/* 搜索栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f7f9fc;
  border-radius: 10px;
  border: 1px solid #eef1f6;
  margin-bottom: 10px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-input {
  width: 200px;
}

.filter-date {
  width: 220px;
}

.btn-icon {
  margin-right: 4px;
}

/* 新增分类按钮：绿色阴影 + hover 浮动 */
.add-btn {
  box-shadow: 0 4px 10px rgba(103,194,58,0.35);
  transition: 0.18s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(103,194,58,0.45);
}

/* 表格 */
.category-table {
  border-radius: 10px;
  overflow: hidden;
}

.category-table :deep(.el-table__row) {
  transition: .15s ease;
}

.category-table :deep(.el-table__row:hover) {
  background: #f5f7ff !important;
}

/* 分行时间 */
.time-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.2;
}

/* 字体统一 */
:deep(.el-table__header-wrapper th) {
  font-size: 15px !important;
}

:deep(.el-table__row td) {
  font-size: 15px !important;
  padding: 14px 0 !important;
}

.filter-bar :deep(.el-input__inner),
.filter-bar :deep(.el-input__wrapper),
.filter-bar :deep(.el-range-input) {
  font-size: 14px !important;
}

/* 分页 */
.pager {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}

/* 弹窗 */
.dialog-title {
  font-size: 18px;
  font-weight: 600;
}
</style>
