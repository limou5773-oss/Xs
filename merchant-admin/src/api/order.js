import request from './request'

// ======================
// 老接口：获取商家订单（不分页）
// ======================
export function getOrders(merchantId, status) {
  return request.get('/api/merchant/orders', {
    params: { merchantId, status }
  })
}

// 商家接单（1 → 2）
export function takeOrder(orderId) {
  return request.post(`/api/merchant/take/${orderId}`)
}

// 商家完成配送（2 → 3）
export function finishOrder(orderId) {
  return request.post(`/api/merchant/finish/${orderId}`)
}

// ======================
// ⭐ 新分页接口（使用 PageHelper）
// POST /api/merchant/order/page
// ======================
export function pageOrders(data) {
  return request({
    url: '/api/merchant/order/page',
    method: 'post',
    data
  })
}
