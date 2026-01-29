const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080";

Page({
  data: {
    user: null,
    orderCount: { pendingPay: 0, pendingSend: 0, pendingReceive: 0, finished: 0 },
    hasIcons: false 
  },

  onShow() {
    this.initUser();
  },

  async initUser() {
    const localUser = wx.getStorageSync('user');
    
    if (!localUser || !localUser.id) {
      this.setData({ user: null, orderCount: {} });
      return;
    }

    try {
      const user = await API.user.get(localUser.id);
      if (user.avatar && !user.avatar.startsWith('http')) {
        user.avatar = BASE_URL + user.avatar;
      }
      this.setData({ user });
      wx.setStorageSync('user', user);

      this.loadOrderCount(user.id);
    } catch (err) {
      console.error("刷新用户信息失败", err);
    }
  },

  async loadOrderCount(userId) {
    try {
      const orders = await API.order.list(userId);

      const count = {
        pendingPay:   orders.filter(o => String(o.status) === '0').length,
        pendingSend:  orders.filter(o => String(o.status) === '1').length,
        pendingReceive: orders.filter(o => String(o.status) === '2').length,
        finished:     orders.filter(o => String(o.status) === '3').length
      };
      
      this.setData({ orderCount: count });
    } catch (e) {
      console.error("订单数量加载失败", e);
    }
  },

  goEdit() {
    if (!this.data.user) { 
      wx.navigateTo({ url: '/pages/login/login' }); 
      return; 
    }
    wx.navigateTo({ url: '/pages/edit-profile/edit-profile' });
  },

  goOrder(e) {
    if (!this.data.user) {
      return wx.showToast({ title: '请先登录', icon: 'none' });
    }

    let status = e.currentTarget.dataset.status; // '-1' / '0' / '1' / ...
    if (status === 'all') status = -1;          // 兼容一下 all 写法

    const url = `/pages/order/order?status=${status}`;
    wx.navigateTo({ 
      url,
      fail: () => {
        wx.switchTab({
          url: '/pages/order/order',
          success: () => wx.setStorageSync('temp_order_status', status)
        });
      }
    });
  },

  goPage(e) {
    if (!this.data.user) {
      return wx.showToast({ title: '请先登录', icon: 'none' });
    }
    wx.navigateTo({ url: e.currentTarget.dataset.url });
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('user');
          wx.removeStorageSync('token');
          this.setData({ user: null, orderCount: {} });
        }
      }
    });
  }
});
