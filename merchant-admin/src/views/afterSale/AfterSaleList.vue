<template>
  <div class="after-page">
    <el-card class="page-card">

      <!-- 顶部统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">售后总数</div>
          <div class="stat-value">{{ stat.total }}</div>
          <div class="stat-tag">当前筛选范围内</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">本页售后</div>
          <div class="stat-value">{{ list.length }}</div>
          <div class="stat-tag">当前页显示数量</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">最近处理时间</div>
          <div class="stat-value">
            {{ latestDate || '-' }}<br />
            <span class="time-sub">{{ latestTime }}</span>
          </div>
          <div class="stat-tag">最近一条售后处理时间</div>
        </div>
      </div>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="query.keyword"
            placeholder="搜索订单号 / 姓名 / 手机"
            size="small"
            clearable
            prefix-icon="Search"
            class="filter-input"
          />

          <el-select
            v-model="query.status"
            placeholder="状态"
            size="small"
            clearable
            class="filter-select"
          >
            <el-option label="待处理" :value="0" />
            <el-option label="已同意退款" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <!-- ⭐ 新增：已取消 -->
            <el-option label="已取消" :value="3" />
          </el-select>

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
            <el-icon class="btn-icon"><Search /></el-icon>搜索
          </el-button>
          <el-button size="small" @click="resetSearch">重置</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table :data="list" border stripe size="small" class="after-table">

        <!-- 订单号 -->
        <el-table-column prop="orderNo" label="订单号" min-width="150" align="center" />

        <!-- 商品信息 -->
        <el-table-column label="商品信息" min-width="180">
          <template #default="{ row }">
            <div class="product-list">
              <div v-for="(p, idx) in row.items" :key="idx" class="product-item">
                {{ p.productName }} × {{ p.quantity }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 退款人 -->
        <el-table-column label="退款人" width="160" align="center">
          <template #default="{ row }">
            <div class="user-box">
              <div class="user-name">{{ row.refundName }}</div>
              <div class="user-phone">{{ row.refundPhone }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 退款金额 -->
        <el-table-column label="退款金额(￥)" width="130" align="center">
          <template #default="{ row }">
            <b style="color:#E6A23C">{{ row.refundPrice ?? '-' }}</b>
          </template>
        </el-table-column>

        <!-- 退款原因 -->
        <el-table-column prop="reason" label="退款原因" min-width="150" show-overflow-tooltip />

        <!-- 状态 -->
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="tagColor(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 申请时间 -->
        <el-table-column label="申请时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.createTime) }}</div>
              <div class="time-sub">{{ formatTime(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 处理时间 -->
        <el-table-column label="处理时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ formatDate(row.handleTime) }}</div>
              <div class="time-sub">{{ formatTime(row.handleTime) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="openDetail(row)">
              查看详情
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

    <el-dialog v-model="dialogVisible" width="680px" destroy-on-close>
      <template #header>
        <h3 class="dialog-title">售后详情</h3>
      </template>

      <AfterSaleDetail :record="detail" :logs="logs" @update="handleUpdated" />
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { getAfterSalePage, getAfterSaleLogs } from "@/api/afterSale";
import AfterSaleDetail from "./AfterSaleDetail.vue";

const list = ref([]);
const total = ref(0);

const query = ref({
  page: 1,
  size: 10,
  keyword: "",
  status: null,
  startTime: "",
  endTime: "",
});

const dateRange = ref(null);

const formatDate = (t) => (t ? dayjs(t).format("YYYY-MM-DD") : "-");
const formatTime = (t) => (t ? dayjs(t).format("HH:mm:ss") : "");

// 顶部统计
const stat = computed(() => ({ total: total.value }));

// ⭐ 状态文案：0 待处理 / 1 已同意退款 / 2 已拒绝 / 3 已取消
const statusText = (s) =>
  ["待处理", "已同意退款", "已拒绝", "已取消"][s] ?? "-";

// ⭐ tag 颜色映射：和上面的状态一一对应
const tagColor = (s) =>
  ["warning", "success", "danger", "info"][s] ?? "info";

/* 解析 addressSnapshot + 商品 items */
const loadData = async () => {
  const params = { ...query.value };

  if (dateRange.value?.length === 2) {
    params.startTime = dateRange.value[0];
    params.endTime = dateRange.value[1];
  } else {
    params.startTime = "";
    params.endTime = "";
  }

  const res = await getAfterSalePage(params);

  list.value = (res.data.records || []).map((item) => {
    try {
      const addr = JSON.parse(item.addressSnapshot || "{}");
      item.refundName = addr.name || "未知姓名";
      item.refundPhone = addr.phone || "未知手机";
    } catch {
      item.refundName = "未知姓名";
      item.refundPhone = "未知手机";
    }

    // 后端返回的商品明细字段
    item.items = item.items || [];

    return item;
  });

  total.value = res.data.total || 0;
};

const search = () => {
  query.value.page = 1;
  loadData();
};

const resetSearch = () => {
  query.value = {
    page: 1,
    size: 10,
    keyword: "",
    status: null,
    startTime: "",
    endTime: "",
  };
  dateRange.value = null;
  loadData();
};

const changePage = (p) => {
  query.value.page = p;
  loadData();
};

const dialogVisible = ref(false);
const detail = ref({});
const logs = ref([]);

const openDetail = async (row) => {
  detail.value = row;
  dialogVisible.value = true;
  const res = await getAfterSaleLogs(row.id);
  logs.value = res.data || [];
};

const handleUpdated = () => {
  dialogVisible.value = false;
  loadData();
};

onMounted(loadData);
</script>

<style scoped>
.after-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
}

/* 商品信息列样式 */
.product-list {
  line-height: 1.4;
}
.product-item {
  font-size: 14px;
  color: #333;
}

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
  transition: 0.18s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.06);
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
  width: 220px;
}

.filter-select {
  width: 130px;
}

.filter-date {
  width: 260px;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
}
.user-phone {
  font-size: 13px;
  color: #666;
}

.time-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  line-height: 1.2;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
}
</style>
