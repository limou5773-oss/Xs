<template>
  <div class="product-page">
    <el-card class="page-card">

      <!-- 顶部统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">商品总数</div>
          <div class="stat-value">{{ stat.total }}</div>
          <div class="stat-tag">当前筛选范围内</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">上架商品</div>
          <div class="stat-value">{{ stat.up }}</div>
          <div class="stat-tag">销售中</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">下架商品</div>
          <div class="stat-value">{{ stat.down }}</div>
          <div class="stat-tag">暂不展示</div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="query.keyword"
            placeholder="搜索商品名称"
            size="small"
            clearable
            prefix-icon="Search"
            class="filter-input"
          />

          <el-select
            v-model="query.categoryId"
            clearable
            placeholder="分类"
            size="small"
            class="filter-select"
          >
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>

          <el-date-picker
            v-model="dateRange"
            type="daterange"
            size="small"
            unlink-panels
            range-separator="-"
            start-placeholder="开始"
            end-placeholder="结束"
            class="filter-date"
          />
        </div>

        <div class="filter-right">
          <el-button size="small" type="primary" @click="search">
            <el-icon class="btn-icon"><Search /></el-icon> 搜索
          </el-button>

          <el-button size="small" @click="resetSearch">重置</el-button>

          <el-button
            size="small"
            type="success"
            class="add-btn"
            @click="goAdd"
          >
            <el-icon class="btn-icon"><Plus /></el-icon> 新增商品
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :data="list"
        border
        stripe
        size="small"
        class="product-table"
      >
        <!-- 封面 -->
        <el-table-column label="封面" width="90" align="center">
          <template #default="{ row }">
            <div class="cover-wrapper" v-if="row.cover">
              <img :src="row.cover" class="cover-img" />
            </div>
            <span v-else class="cover-empty">暂无</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="name"
          label="商品名称"
          min-width="150"
          show-overflow-tooltip
        />

        <el-table-column
          prop="categoryName"
          label="分类"
          width="120"
          align="center"
        />

        <el-table-column
          prop="price"
          label="价格(¥)"
          width="100"
          align="center"
        />

        <el-table-column
          prop="stock"
          label="库存"
          width="90"
          align="center"
        />

        <el-table-column
          prop="unit"
          label="单位"
          width="80"
          align="center"
        />

        <!-- 简介 -->
        <el-table-column label="介绍" min-width="160">
          <template #default="{ row }">
            {{ row.description ? row.description.substring(0, 20) + '…' : '—' }}
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column
          label="状态"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="上架"
              inactive-text="下架"
              @change="() => changeStatus(row)"
            />
          </template>
        </el-table-column>

        <!-- 更新时间（拆行） -->
        <el-table-column
          label="更新时间"
          width="160"
          align="center"
        >
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.updateTime) }}</div>
              <div class="time-sub">{{ formatClock(row.updateTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column
          label="操作"
          width="170"
          align="center"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="edit(row)">
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue"
import { useRouter } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import { Plus, Search } from "@element-plus/icons-vue"
import dayjs from "dayjs"

import { productPage, deleteProduct, updateProduct } from "@/api/product"
import { getCategories } from "@/api/category"

const router = useRouter()

/* 列表 & 总数 */
const list = ref([])
const total = ref(0)
const categories = ref([])

const query = ref({
  page: 1,
  size: 10,
  keyword: "",
  categoryId: null,
  startTime: "",
  endTime: ""
})

const dateRange = ref(null)

/* 时间格式化 */
const formatDate = (t) => dayjs(t).format("YYYY-MM-DD")
const formatClock = (t) => dayjs(t).format("HH:mm:ss")

/* 统计 */
const stat = computed(() => {
  const totalCount = list.value.length
  const up = list.value.filter((i) => i.status === 1).length
  const down = list.value.filter((i) => i.status === 0).length
  return { total: totalCount, up, down }
})

/* 加载分类 */
const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

/* 加载商品 */
const loadData = async () => {
  const params = { ...query.value }

  if (dateRange.value?.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  } else {
    params.startTime = ""
    params.endTime = ""
  }

  const res = await productPage(params)
  list.value = res.data.records || []
  total.value = res.data.total || 0
}

/* 搜索 */
const search = () => {
  query.value.page = 1
  loadData()
}

/* 重置 */
const resetSearch = () => {
  query.value = { page: 1, size: 10, keyword: "", categoryId: null, startTime: "", endTime: "" }
  dateRange.value = null
  loadData()
}

/* 新增 */
const goAdd = () => router.push("/product/edit")

/* 编辑 */
const edit = (row) => router.push(`/product/edit/${row.id}`)

/* 删除 */
const remove = async (id) => {
  await ElMessageBox.confirm("确认删除该商品吗？", "提示", { type: "warning" })
  await deleteProduct(id)
  ElMessage.success("删除成功")
  loadData()
}

/* 页码 */
const changePage = (p) => {
  query.value.page = p
  loadData()
}

/* 修改状态 */
const changeStatus = async (row) => {
  const backup = row.status
  try {
    await updateProduct(row)
    ElMessage.success(`已${row.status === 1 ? "上架" : "下架"}`)
    loadData()
  } catch (e) {
    row.status = backup
    ElMessage.error("操作失败，请稍后重试")
  }
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.product-page {
  padding: 16px 18px;
}

/* 外层卡片 */
.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  padding: 14px 18px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffffff, #f7f9ff);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
  position: relative;
  overflow: hidden;
  transition: 0.18s;
}

.stat-card::after {
  content: "";
  position: absolute;
  right: -40px;
  bottom: -40px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  opacity: 0.18;
}

.total::after {
  background: #409eff;
}
.up::after {
  background: #67c23a;
}
.down::after {
  background: #f56c6c;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.06);
}

.stat-title {
  font-size: 13px;
  color: #909399;
}
.stat-value {
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  margin: 4px 0;
}
.stat-tag {
  font-size: 12px;
  color: #c0c4cc;
}

/* 搜索栏 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f7f9fc;
  border: 1px solid #eef1f6;
  border-radius: 10px;
  margin-bottom: 10px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-input {
  width: 200px;
}
.filter-select {
  width: 130px;
}
.filter-date {
  width: 220px;
}

.btn-icon {
  margin-right: 4px;
}

.add-btn {
  box-shadow: 0 4px 10px rgba(103, 194, 58, 0.35);
}

/* 表格 */
.product-table {
  border-radius: 10px;
  overflow: hidden;
}

.product-table :deep(.el-table__row:hover) {
  background-color: #f5f7ff !important;
}

/* 封面样式 */
.cover-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* ⭐自动裁切成正方形，不变形 */
}


.cover-wrapper:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.cover-empty {
  font-size: 12px;
  color: #c0c4cc;
}

/* 时间拆行 */
.time-box {
  line-height: 1.2;
}
.time-sub {
  font-size: 12px;
  color: #909399;
}

/* 分页 */
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

/* 表头、内容字体统一 */
:deep(.el-table__header-wrapper th),
:deep(.el-table__row td) {
  font-size: 15px !important;
}
</style>
