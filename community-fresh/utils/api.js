// utils/api.js
const request = require('./request.js');

const API = {
  // 1. 登录
  auth: {
    wxLogin: (data) => request.post('/api/auth/wxLogin', data)
  },

  // 2. 用户
  user: {
    get: (id) => request.get(`/api/user/${id}`),
    update: (data) => request.post('/api/user/update', data)
  },

  // 3. 地址
  address: {
    list: (userId) => request.get(`/api/address/list/${userId}`),
    get: (id) => request.get(`/api/address/${id}`),
    add: (data) => request.post('/api/address/add', data),
    update: (data) => request.post('/api/address/update', data),
    delete: (id) => request.del(`/api/address/delete/${id}`)
  },

  // 4. 购物车
  cart: {
    list: (userId) => request.get(`/api/cart/list/${userId}`),
    add: (data) => request.post('/api/cart/add', data),
    remove: (id) => request.del(`/api/cart/remove/${id}`),
    update: (id, quantity) => request.post(`/api/cart/update/${id}`, { quantity }),
    check: (id, checked) => request.post(`/api/cart/check/${id}`, { checked }),
    clear: (userId) => request.del(`/api/cart/clear/${userId}`)
  },

  // 5. 商品
  product: {
    list: () => request.get('/api/product/list'),
    page: (data) => request.post('/api/product/page', data),
    recommend: () => request.get('/api/product/recommend'),
    byCategory: (id) => request.get(`/api/product/category/${id}`),
    search: (keyword) =>
      request.get(`/api/product/search?keyword=${encodeURIComponent(keyword)}`),
    detail: (id) => request.get(`/api/product/detail/${id}`)
  },

  // 6. 分类
  category: {
    list: () => request.get('/api/category/list')
  },

  // 7. 订单
  order: {
    list: (userId) => request.get(`/api/order/list/${userId}`),
    detail: (id) => request.get(`/api/order/detail/${id}`),
    create: (data) => request.post('/api/order/create', data),
    pay: (id) => request.post(`/api/order/pay/${id}`),
    cancel: (id) => request.post(`/api/order/cancel/${id}`),
    confirm: (id) => request.post(`/api/order/confirm/${id}`)
  },

  // 8. 公告
  notice: {
    latest: () => request.get('/api/notice/latest')
  },

  // 9. 售后
  afterSale: {
    apply: (data) => request.post('/api/afterSale/apply', data),
    cancel: (data) => request.post('/api/afterSale/cancel', data),
    detail: (orderId) => request.get(`/api/afterSale/detail/${orderId}`)
  },

  // 10. 消息
  message: {
    list: (userId) => request.get(`/api/message/list/${userId}`)
  },

  // 11. 评价 (新增)
  review: {
    add: (data) => request.post('/api/review/add', data),
    list: () => request.get('/api/review/list'),
    listByProduct: (productId) => request.get(`/api/review/product/${productId}`)
  }
};

module.exports = API;