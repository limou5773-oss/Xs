<template>
  <div class="message-page">
    <el-card class="page-card">

      <!-- 顶部统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">消息总数</div>
          <div class="stat-value">{{ total }}</div>
          <div class="stat-tag">系统全部消息数量</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">未读消息</div>
          <div class="stat-value">{{ unread }}</div>
          <div class="stat-tag">尚未查看的消息</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">最近消息时间</div>
          <div class="stat-value">
            {{ latestTimeDate }}<br />
            <span class="time-sub">{{ latestTimeClock }}</span>
          </div>
          <div class="stat-tag">最近一条消息</div>
        </div>
      </div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <!-- 左侧：输入框 -->
      <div class="filter-left">
        <el-input
          v-model="query.keyword"
          placeholder="搜索订单相关消息（内容 / 标题）"
          clearable
          class="filter-input"
        />
      </div>

      <!-- 右侧：按钮（搜索 / 重置 / 刷新） -->
      <div class="filter-right">
        <el-button type="primary" class="btn-primary" @click="doSearch">
          搜索
        </el-button>

        <el-button @click="resetSearch">重置</el-button>

        <el-button type="success" @click="refresh">
          刷新
        </el-button>
      </div>
    </div>



      <!-- 表格 -->
      <el-table
        :data="list"
        border
        stripe
        size="small"
        class="message-table"
        :row-class-name="rowClassName"
      >
        <!-- 类型 -->
        <el-table-column label="类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getTagType(row)" size="small">
              {{ getTypeText(row) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 内容 -->
        <el-table-column label="内容" min-width="360">
          <template #default="{ row }">
            <span class="ellipsis" :title="row.content">
              {{ row.content }}
            </span>
          </template>
        </el-table-column>

        <!-- 时间 -->
        <el-table-column label="时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.createTime) }}</div>
              <div class="time-sub">{{ formatClock(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead ? 'info' : 'danger'" size="small">
              {{ row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text @click="view(row)">查看</el-button>

            <el-button
              v-if="!row.isRead"
              size="small"
              type="primary"
              text
              @click="doRead(row.id)"
            >
              标记已读
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

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" width="480px">
      <template #header>
        <h3 class="dialog-title">{{ current.title }}</h3>
      </template>

      <div class="dialog-body">
        <p class="dialog-content">{{ current.content }}</p>
        <p class="dialog-time">
          {{ formatDate(current.createTime) }}
          {{ formatClock(current.createTime) }}
        </p>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!current.isRead" type="primary" @click="doRead(current.id)">
          标记已读
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue"
import { ElMessage } from "element-plus"
import { Search } from "@element-plus/icons-vue"
import dayjs from "dayjs"

import { getMessages, getUnreadCount, markRead } from "@/api/message"

const query = ref({
  page: 1,
  size: 10,
  keyword: ""
})

const list = ref([])
const total = ref(0)
const unread = ref(0)

const dialogVisible = ref(false)
const current = ref({})

/* ========== 时间格式化 ========== */
const formatDate = (t) => dayjs(t).format("YYYY-MM-DD")
const formatClock = (t) => dayjs(t).format("HH:mm:ss")

const latestTimeDate = computed(() =>
  list.value.length ? formatDate(list.value[0].createTime) : "-"
)
const latestTimeClock = computed(() =>
  list.value.length ? formatClock(list.value[0].createTime) : ""
)

/* ========== 消息类型文字与颜色 ========== */
const getTypeText = (row) => {
  const t = (row.title || "") + (row.content || "")
  if (/下单|支付|准备/.test(t)) return "用户下单"
  if (/配送|派送|送达/.test(t)) return "配送中"
  if (/完成/.test(t)) return "已完成"
  if (/退款/.test(t)) return "已退款"
  return "其他"
}

const getTagType = (row) => {
  const t = (row.title || "") + (row.content || "")
  if (/下单|支付|准备/.test(t)) return "success"
  if (/配送|派送|送达/.test(t)) return "warning"
  if (/完成/.test(t)) return "info"
  if (/退款/.test(t)) return "danger"
  return "info"
}

/* ========== 行样式：未读高亮 ========== */
const rowClassName = ({ row }) => (!row.isRead ? "row-unread" : "")

/* ========== 数据加载 ========== */
const loadList = async () => {
  const res = await getMessages(query.value)
  list.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadUnread = async () => {
  const res = await getUnreadCount()
  unread.value = res.data || 0
}

const refresh = async () => {
  await Promise.all([loadList(), loadUnread()])
}

const doSearch = () => {
  query.value.page = 1
  loadList()
}

const resetSearch = () => {
  query.value = { page: 1, size: 10, keyword: "" }
  loadList()
}

const view = (row) => {
  current.value = row
  dialogVisible.value = true
}

const doRead = async (id) => {
  await markRead(id)
  ElMessage.success("已标记为已读")
  dialogVisible.value = false
  refresh()
}

const changePage = (p) => {
  query.value.page = p
  loadList()
}

onMounted(refresh)
</script>

<style scoped>
.message-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
}

/* 顶部统计 */
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
  background: #e6a23c;
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
  margin: 4px 0;
}
.stat-tag {
  font-size: 12px;
  color: #bcbec4;
}

.time-sub {
  font-size: 12px;
  color: #909399;
}

/* 搜索栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fc;
  border: 1px solid #e6e8ef;
  margin-bottom: 16px;
}

.filter-input {
  width: 260px;
}

.add-btn {
  box-shadow: 0 4px 10px rgba(103, 194, 58, 0.3);
}

/* 表格 */
.message-table {
  --el-table-border-color: #ebeef5;
  --el-table-header-bg-color: #f5f7fa;
  border-radius: 10px;
  overflow: hidden;
}

.message-table :deep(.row-unread) {
  background: #fffdf7;
}
.message-table :deep(.row-unread:hover) {
  background: #fff7e6 !important;
}

.time-box {
  line-height: 1.2;
}

/* 省略号 */
.ellipsis {
  display: inline-block;
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 分页 */
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

/* 弹窗 */
.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.dialog-body {
  padding: 8px 4px;
}
.dialog-time {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
}
/* 表头字体变大 */
:deep(.el-table__header-wrapper th) {
  font-size: 15px !important;
}

/* 表格内容字体放大 */
:deep(.el-table__row td) {
  font-size: 15px !important;
  padding: 14px 0 !important; /* 行高更舒服 */
}

/* 搜索栏输入框字体放大 */
.filter-bar :deep(.el-input__inner),
.filter-bar :deep(.el-input__wrapper),
.filter-bar :deep(.el-range-input) {
  font-size: 14px !important;
}

.filter-bar {
  display: flex;
  justify-content: space-between; /* ⭐ 左右分布 */
  align-items: center;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fc;
  border: 1px solid #e6e8ef;
  margin-bottom: 14px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 10px; /* 按钮间距 */
}

</style>
