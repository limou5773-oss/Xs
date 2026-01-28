import request from "./request";

// 统一获取 merchantId（如果你的登录存储位置不同，可以调整这里）
const getMerchantId = () => {
  return localStorage.getItem("merchantId") || 1;
};

// =========================
// ① 分页查询售后列表（核心接口）
// =========================
export const getAfterSalePage = (params) => {
  return request.get("/api/merchant/afterSale/page", {
    params: {
      merchantId: getMerchantId(),
      ...params
    }
  });
};

// =========================
// ② 获取售后日志列表
// =========================
export const getAfterSaleLogs = (afterSaleId) => {
  return request.get(`/api/merchant/afterSale/logs/${afterSaleId}`, {
    params: { merchantId: getMerchantId() }
  });
};

// =========================
// ③ 商家同意退款（自动全额）
// =========================
export const approveAfterSale = (id) => {
  return request.post(`/api/merchant/afterSale/approve/${id}`, null, {
    params: { merchantId: getMerchantId() }
  });
};

// =========================
// ④ 商家拒绝售后申请
// =========================
export const rejectAfterSale = (id) => {
  return request.post(`/api/merchant/afterSale/reject/${id}`, null, {
    params: { merchantId: getMerchantId() }
  });
};
