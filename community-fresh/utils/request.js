/**
 * utils/request.js
 * 通用请求封装
 */
const BASE_URL = 'http://localhost:8080'; // 你的后端地址

function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    // 1. 从缓存取 Token
    const token = wx.getStorageSync('token');

    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        // 2. 这里的 Bearer 后面有个空格，千万别漏了
        'Authorization': token ? `Bearer ${token}` : '' 
      },
      success: (res) => {
        // 3.核心拦截：如果后端返回 401 (未授权)
        if (res.statusCode === 401) {
          wx.showToast({ title: '登录已过期', icon: 'none' });
          
          // 清除脏数据
          wx.removeStorageSync('token');
          wx.removeStorageSync('user');

          // 1.5秒后跳回登录页
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/login' });
          }, 1500);
          
          reject('登录过期');
          return;
        }

        if (res.data.code === 200 || res.data.code === 1) {
          resolve(res.data.data); 
        } else {
          // 业务逻辑错误 (比如库存不足)
          wx.showToast({ 
            title: res.data.msg || '请求失败', 
            icon: 'none' 
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        console.error('网络请求失败:', err);
        wx.showToast({ title: '网络连接异常', icon: 'none' });
        reject(err);
      }
    });
  });
}

// 导出快捷方法
module.exports = {
  request,
  get: (url, data) => request(url, 'GET', data),
  post: (url, data) => request(url, 'POST', data),
  del: (url, data) => request(url, 'DELETE', data),
  put: (url, data) => request(url, 'PUT', data),
  BASE_URL
};