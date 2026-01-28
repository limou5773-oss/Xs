<template>
  <div class="detail-box">

    <!-- 商品信息 + 退款人信息（左右布局） -->
    <div class="section">
      <h3>商品信息</h3>

      <div class="flex-row">
        <!-- 左侧：商品列表 -->
        <div class="product-list">
          <div v-for="p in record.items" :key="p.id" class="product-item">
            <div class="name">{{ p.productName }}</div>
            <div class="price">￥{{ p.price }} × {{ p.quantity }}</div>
          </div>

          <div class="order-id">订单号：{{ record.orderNo }}</div>
        </div>

        <!-- 右侧：退款人信息（只显示一次） -->
        <div class="refund-box">
          <h4 class="refund-title">退款人信息</h4>
          <div class="refund-name">{{ refundName }}</div>
          <div class="refund-phone">{{ refundPhone }}</div>
          <div class="refund-price">退款金额：￥{{ record.refundPrice }}</div>
        </div>
      </div>
    </div>

    <!-- 申请信息 -->
    <div class="section">
      <h3>申请信息</h3>
      <p><strong>原因：</strong>{{ record.reason }}</p>
      <p><strong>申请时间：</strong>{{ record.createTime }}</p>
    </div>

    <!-- 售后状态 -->
    <div class="section">
      <h3>售后状态</h3>

      <el-tag :type="tagColor(record.status)" size="large">
        {{ statusText(record.status) }}
      </el-tag>

      <p v-if="record.handleTime" class="mt10">
        <strong>处理时间：</strong>{{ record.handleTime }}
      </p>
    </div>

    <!-- 日志 -->
    <div class="section">
      <h3>售后日志</h3>

      <div class="log-box">
        <el-timeline>
          <el-timeline-item
            v-for="(log, index) in logs"
            :key="index"
            :timestamp="log.createTime"
            :color="logPointColor(log.action)"
          >
            {{ log.action }}（操作人：{{ showOp(log.operator) }}）
          </el-timeline-item>

          <el-timeline-item v-if="!logs.length" timestamp="--" type="info">
            暂无操作记录
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <!-- 按钮 -->
    <div class="btn-group" v-if="record.status === 0">
      <el-button type="success" @click="approve(record.id)">同意</el-button>
      <el-button type="danger" @click="reject(record.id)">拒绝</el-button>
    </div>

  </div>
</template>

<script setup>
import { computed } from "vue";
import { ElMessage } from "element-plus";
import request from "@/api/request";

const props = defineProps({
  record: Object,
  logs: Array,
});
const emit = defineEmits(["update"]);

// 地址解析
const refundName = computed(() => {
  try {
    return JSON.parse(props.record.addressSnapshot)?.name ?? "未知";
  } catch {
    return "未知";
  }
});
const refundPhone = computed(() => {
  try {
    return JSON.parse(props.record.addressSnapshot)?.phone ?? "未知";
  } catch {
    return "未知";
  }
});

const statusText = (s) => ["待处理", "已同意退款", "已拒绝"][s];
const tagColor = (s) => ["warning", "success", "danger"][s];

const logPointColor = (action) => {
  if (action.includes("拒绝")) return "#ff4d4f";
  if (action.includes("同意")) return "#67c23a";
  return "#909399";
};

const showOp = (op) =>
  ({ user: "用户", merchant: "商家", system: "系统" }[op] || "未知");

const approve = async (id) => {
  const res = await request.post(`/api/merchant/afterSale/approve/${id}`);
  if (res.code === 200) {
    ElMessage.success("已同意售后");
    emit("update");
  }
};

const reject = async (id) => {
  const res = await request.post(`/api/merchant/afterSale/reject/${id}`);
  if (res.code === 200) {
    ElMessage.success("已拒绝售后");
    emit("update");
  }
};
</script>

<style scoped>
.detail-box {
  padding: 10px;
}

.section {
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

/* 左右布局 */
.flex-row {
  display: flex;
  justify-content: space-between;
}

/* 左侧商品 */
.product-list {
  width: 60%;
}

.product-item {
  margin-bottom: 8px;
}

.name {
  font-weight: 600;
}
.price {
  font-size: 13px;
  color: #f56c6c;
}

.order-id {
  margin-top: 10px;
  color: #777;
}

/* 右侧退款人信息 */
.refund-box {
  width: 35%;
  text-align: right;
}

.refund-title {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 6px;
}

.refund-price {
  margin-top: 6px;
  color: #e6a23c;
  font-weight: 600;
}

/* 日志滚动 */
.log-box {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 6px;
}

/* 按钮 */
.btn-group {
  text-align: center;
  margin-top: 20px;
}

.mt10 {
  margin-top: 10px;
}
</style>
