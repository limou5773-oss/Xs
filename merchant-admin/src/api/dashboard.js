// src/api/dashboard.js
import request from './request'

// 仪表盘统计
export function getDashboardSummary(merchantId = 1) {
  return request({
    url: '/api/merchant/dashboard/summary',
    method: 'get',
    params: { merchantId }
  })
}
