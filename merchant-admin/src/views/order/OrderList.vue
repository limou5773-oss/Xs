<template>
  <div class="order-page">
    <el-card class="page-card">

      <div class="stat-cards">
        <div class="stat-card total">
          <div class="stat-title">订单总数</div>
          <div class="stat-value">{{ stat.total }}</div>
          <div class="stat-tag">当前筛选范围内</div>
        </div>

        <div class="stat-card up">
          <div class="stat-title">配送中</div>
          <div class="stat-value">{{ stat.delivering }}</div>
          <div class="stat-tag">正在配送的订单</div>
        </div>

        <div class="stat-card down">
          <div class="stat-title">已完成</div>
          <div class="stat-value">{{ stat.finished }}</div>
          <div class="stat-tag">已完成的订单</div>
        </div>
      </div>

      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="query.keyword"
            placeholder="搜索：订单号 / 收货人 / 手机"
            size="small"
            clearable
            prefix-icon="Search"
            class="filter-input"
          />

          <el-select
            v-model="query.status"
            size="small"
            placeholder="订单状态"
            clearable
            class="filter-select"
          >
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="配送中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
            <el-option label="已退款" :value="5" />
          </el-select>

          <div class="date-group">
            <el-date-picker
              v-model="query.startTime"
              type="datetime"
              size="small"
              placeholder="开始时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="filter-date"
            />
            <span class="date-sep">-</span>
            <el-date-picker
              v-model="query.endTime"
              type="datetime"
              size="small"
              placeholder="结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              class="filter-date"
            />
          </div>
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
        :data="orders"
        border
        stripe
        size="small"
        class="order-table"
      >
        <el-table-column 
          prop="orderNo" 
          label="订单号" 
          min-width="160" 
          align="center" 
          show-overflow-tooltip 
        />

        <el-table-column 
          prop="totalPrice" 
          label="金额(￥)" 
          width="100" 
          align="center" 
        />

        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColor(row.status)" size="small" effect="plain">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="收货人" width="180">
          <template #default="{ row }">
            <div class="addr-box">
              <div class="line1">
                <span class="addr-name">{{ row.address?.name }}</span>
                <span class="addr-phone">{{ row.address?.phone }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="收货地址" min-width="200">
          <template #default="{ row }">
            {{ [row.address?.region, row.address?.detail].filter(Boolean).join(' ') }}
          </template>
        </el-table-column>

        <el-table-column label="备注" width="150" align="center">
          <template #default="{ row }">
            <span v-if="row.remark" class="ellipsis" :title="row.remark">
              {{ row.remark }}
            </span>
            <span v-else class="remark-empty">-</span>
          </template>
        </el-table-column>

        <el-table-column label="下单时间" width="160" align="center">
          <template #default="{ row }">
            <div class="time-box">
              <div>{{ row.createTime?.replace('T',' ').split(' ')[0] }}</div>
              <div class="time-sub">{{ row.createTime?.replace('T',' ').split(' ')[1] }}</div>
            </div>
          </template>
        </el-table-column>


        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              :type="progressBtnType(row.status)" 
              text 
              @click="openTimeline(row)"
            >
              配送进度
            </el-button>
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

    <el-dialog v-model="dialogVisible" width="580px">
      <template #header>
        <h3 class="dialog-title">订单配送进度</h3>
        <div class="dialog-sub-title">订单号：{{ currentOrder?.orderNo }}</div>
      </template>

      <el-timeline style="padding-left: 10px; margin-top: 10px;">
        <el-timeline-item
          v-for="s in steps"
          :key="s.status"
          :type="s.color"
          :icon="s.icon"
          :hollow="currentOrder?.status !== s.status"
          :timestamp="s.time"
          placement="top"
        >
          <div class="step-card">
            <div class="step-title">{{ s.title }}</div>
            <div class="step-desc">{{ s.desc }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>

      <div class="action-box" v-if="[1, 2].includes(currentOrder?.status)">
        <el-button v-if="currentOrder?.status === 1" type="warning" @click="doTakeOrder">
          立即接单
        </el-button>
        <el-button v-if="currentOrder?.status === 2" type="success" @click="doFinishOrder">
          确认送达
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search, Check, Close, Wallet, Van, Finished, CircleClose, Refresh } from "@element-plus/icons-vue";
import { pageOrders, takeOrder, finishOrder } from "@/api/order";

const orders = ref([]);
const total = ref(0);

const query = ref({
  page: 1,
  size: 10,
  keyword: "",
  status: null,
  startTime: "",
  endTime: ""
});

/* 顶部统计 */
const stat = computed(() => ({
  total: total.value,
  delivering: orders.value.filter(o => o.status === 2).length,
  finished: orders.value.filter(o => o.status === 3).length
}));

/* 状态映射 */
// 状态：0=待支付, 1=已支付, 2=配送中, 3=已完成, 4=已取消, 5=已退款
const statusText = s => ['待支付','已支付','配送中','已完成','已取消','已退款'][s];
// 状态颜色：info, warning, primary, success, danger, danger
const statusColor = s => ['info','warning','primary','success','danger','danger'][s];
// 配送进度按钮类型：info, warning, primary, success, info, danger
const progressBtnType = s => ['info','warning','primary','success','info','danger'][s];

/* 数据加载 */
const loadOrders = async () => {
  // 注意：在实际应用中，这里需要处理查询参数的空值问题
  // 仅发送非空参数，以保证 API 请求的干净
  const params = Object.fromEntries(
    Object.entries(query.value).filter(([_, v]) => v !== null && v !== '')
  );

  const res = await pageOrders(params);
  const data = res.data || {};

  orders.value = (data.records || []).map(o => ({
    ...o,
    // 解析地址快照 JSON 字符串
    address: o.addressSnapshot ? JSON.parse(o.addressSnapshot) : {}
  }));

  total.value = data.total || 0;
};

/* 搜索逻辑 */
const doSearch = () => {
  query.value.page = 1;
  loadOrders();
};

const resetSearch = () => {
  query.value = {
    page: 1,
    size: 10,
    keyword: "",
    status: null,
    startTime: "",
    endTime: ""
  };
  loadOrders();
};

const refresh = () => loadOrders();

const changePage = p => {
  query.value.page = p;
  loadOrders();
};

/* 进度条逻辑 */
const dialogVisible = ref(false);
const currentOrder = ref(null);
const steps = ref([]);

const openTimeline = row => {
  currentOrder.value = row;
  
  const TIME = {
    1: row.payTime,
    2: row.takeTime,
    3: row.finishTime,
    4: row.cancelTime,
    5: row.refundTime
  };

  // 特殊状态处理：取消和退款
  if (row.status === 4) {
    steps.value = [{
      status: 4, title: "已取消", desc: "订单已取消",
      icon: CircleClose, color: "danger", time: TIME[4]
    }];
  } else if (row.status === 5) {
    steps.value = [{
      status: 5, title: "已退款", desc: "商家已退款",
      icon: Close, color: "danger", time: TIME[5]
    }];
  } else {
    // 正常流程
    steps.value = [
      { status: 0, title: "待支付", desc: "用户下单", icon: Wallet, color: "info", time: row.createTime },
      { status: 1, title: "已支付", desc: "等待商家接单", icon: Check, color: "success", time: TIME[1] },
      { status: 2, title: "配送中", desc: "骑手正在火速配送", icon: Van, color: "primary", time: TIME[2] },
      { status: 3, title: "已完成", desc: "商品已送达", icon: Finished, color: "success", time: TIME[3] }
    ].filter(s => s.status <= row.status); // 只显示当前状态及之前的步骤
  }

  dialogVisible.value = true;
};

/* 业务操作 */
const doTakeOrder = async () => {
  const res = await takeOrder(currentOrder.value.id);
  if (res.code === 200) {
    ElMessage.success("接单成功");
    dialogVisible.value = false;
    loadOrders();
  }
};

const doFinishOrder = async () => {
  const res = await finishOrder(currentOrder.value.id);
  if (res.code === 200) {
    ElMessage.success("配送完成");
    dialogVisible.value = false;
    loadOrders();
  }
};

onMounted(loadOrders);
</script>

<style scoped>
/*
 * 样式指南：遵循分类页面 (Category) 的设计规范，确保颜色、阴影、圆角、字号等元素一致。
 * 1. 页面及卡片基础
 * 2. 统计卡片 (Stat Cards)
 * 3. 筛选栏 (Filter Bar)
 * 4. 表格主体 (Table)
 * 5. 表格内复杂字段 (地址/时间/备注)
 * 6. 分页 (Pager)
 * 7. 对话框 (Dialog/Timeline)
*/

/* ================================ 1. 页面及卡片基础 ================================ */
.order-page {
  padding: 16px 18px;
}

.page-card {
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(31, 45, 61, 0.06);
  border: none;
}

/* ================================ 2. 统计卡片 (Stat Cards) ================================ */
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
.down::after { background: #e6a23c; } /* 黄色 */

.stat-title {
  font-size: 13px;
  color: #909399; /* Element Plus 默认浅灰 */
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
}

.stat-tag {
  font-size: 12px;
  color: #c0c4cc; /* Element Plus 默认更浅灰 */
}

/* ================================ 3. 筛选栏 (Filter Bar) ================================ */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f7f9fc; /* 统一浅背景色 */
  border-radius: 10px;
  border: 1px solid #eef1f6;
  margin-bottom: 10px;
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-input { width: 200px; }
.filter-select { width: 120px; }
.filter-date { width: 170px; } 

.date-group {
  display: flex;
  align-items: center;
  gap: 5px;
}
.date-sep {
  color: #909399;
}

.btn-icon {
  margin-right: 4px;
}

/* 统一输入框/选择器字号 */
.filter-bar :deep(.el-input__inner),
.filter-bar :deep(.el-input__wrapper),
.filter-bar :deep(.el-range-input) {
  font-size: 14px !important;
}


/* ================================ 4. 表格主体 (Table) ================================ */
.order-table {
  border-radius: 10px;
  overflow: hidden;
}

/* 统一表格字体和间距 */
:deep(.el-table__header-wrapper th) {
  font-size: 15px !important;
}

:deep(.el-table__row td) {
  font-size: 15px !important;
  padding: 14px 0 !important;
}

/* 统一悬停效果 */
.order-table :deep(.el-table__row) {
  transition: .15s ease;
}

.order-table :deep(.el-table__row:hover) {
  background: #f5f7ff !important;
}


/* ================================ 5. 表格内复杂字段 ================================ */

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

/* 地址显示 */
.addr-name {
  font-weight: 600;
  margin-right: 8px;
  font-size: 14px;
}
.addr-phone {
  color: #666;
  font-size: 13px;
}
.line1 {
  margin-bottom: 2px;
}
.line2 {
  font-size: 13px;
  color: #888;
}

/* 备注 (统一省略样式) */
.ellipsis {
  display: inline-block;
  max-width: 130px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: #606266;
}
.remark-empty { color: #c0c4cc; } /* 统一使用浅灰 */


/* ================================ 6. 分页 (Pager) ================================ */
.pager {
  display: flex;
  justify-content: center;
  margin-top: 14px;
}


/* ================================ 7. 对话框 (Dialog/Timeline) ================================ */
.dialog-title {
  font-size: 18px;
  font-weight: 600;
}
.dialog-sub-title {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
  font-weight: normal;
}

/* 进度卡片样式 */
.step-card {
  background: #f9fafc;
  padding: 8px 12px;
  border-radius: 6px;
}
.step-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.step-desc {
  font-size: 13px;
  color: #606266;
  margin-top: 4px;
}

/* 底部操作区 */
.action-box {
  text-align: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
}
</style>