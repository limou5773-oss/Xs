<template>
  <div class="notice-page">
    <el-card class="page-card">

      <!-- 顶部统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">公告总数</div>
          <div class="stat-value">{{ total }}</div>
          <div class="stat-tag">系统全部公告数量</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">本页公告</div>
          <div class="stat-value">{{ list.length }}</div>
          <div class="stat-tag">当前页显示数量</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">最新公告时间</div>
          <div class="stat-value">
            {{ latestDate }}<br />
            <span class="time-sub">{{ latestClock }}</span>
          </div>
          <div class="stat-tag">最近一条公告</div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="filter-bar">

        <el-input
          v-model="query.keyword"
          placeholder="搜索公告标题"
          clearable
          class="filter-input"
        />

        <div class="filter-right">
          <el-button type="primary" size="small" @click="search">搜索</el-button>
          <el-button size="small" @click="resetSearch">重置</el-button>

          <el-button
            type="success"
            size="small"
            class="add-btn"
            @click="$router.push('/notice/edit')"
          >
            发布公告
          </el-button>
        </div>

      </div>

      <!-- 表格 -->
      <el-table
        :data="list"
        border
        stripe
        size="small"
        class="notice-table"
      >
        <el-table-column prop="title" label="标题" width="220" />

        <el-table-column label="内容" min-width="300">
          <template #default="{ row }">
            <div class="content-preview" v-html="row.content"></div>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.createTime) }}</div>
              <div class="time-sub">{{ formatClock(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text @click="edit(row.id)">
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              text
              @click="remove(row.id)"
            >
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
import { ref, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from "dayjs";
import { useRouter } from "vue-router";

import { noticePage, noticeDelete } from "@/api/notice";

const router = useRouter();

const query = ref({
  page: 1,
  size: 10,
  keyword: "",
});

const list = ref([]);
const total = ref(0);

/* 时间格式 */
const formatDate = (t) => dayjs(t).format("YYYY-MM-DD");
const formatClock = (t) => dayjs(t).format("HH:mm:ss");

/* 最新时间 */
const latestDate = computed(() =>
  list.value.length ? formatDate(list.value[0].createTime) : "-"
);
const latestClock = computed(() =>
  list.value.length ? formatClock(list.value[0].createTime) : ""
);

/* 加载数据 */
const loadData = async () => {
  const res = await noticePage(query.value);
  list.value = res.data.records || [];
  total.value = res.data.total || 0;
};

/* 搜索 */
const search = () => {
  query.value.page = 1;
  loadData();
};

/* 重置 */
const resetSearch = () => {
  query.value = { page: 1, size: 10, keyword: "" };
  loadData();
};

/* 编辑公告 */
const edit = (id) => {
  router.push(`/notice/edit/${id}`);
};

/* 删除公告 */
const remove = (id) => {
  ElMessageBox.confirm("确定删除此公告吗？", "提示", { type: "warning" }).then(
    async () => {
      await noticeDelete(id);
      ElMessage.success("删除成功");
      loadData();
    }
  );
};

/* 分页 */
const changePage = (p) => {
  query.value.page = p;
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.notice-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
}

/* ======= 统计卡片 ======= */
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

/* ======= 搜索栏 ======= */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fc;
  border: 1px solid #e6e8ef;
  margin-bottom: 14px;
}

.filter-input {
  width: 260px;
}

.filter-right {
  display: flex;
  gap: 10px;
}

.add-btn {
  box-shadow: 0 4px 10px rgba(103, 194, 58, 0.32);
}

/* ======= 表格样式 ======= */
.notice-table {
  border-radius: 10px;
  overflow: hidden;
}

.content-preview {
  max-width: 430px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: #555;
  font-size: 13px;
}

.time-box {
  line-height: 1.2;
}

/* 表头字体 */
:deep(.el-table__header-wrapper th) {
  font-size: 15px !important;
}

/* 行字体 */
:deep(.el-table__row td) {
  font-size: 15px !important;
  padding: 14px 0 !important;
}

/* hover 效果 */
.notice-table :deep(.el-table__row:hover) {
  background: #f5faff !important;
}

/* ======= 分页 ======= */
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
