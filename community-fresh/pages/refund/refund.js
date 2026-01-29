
const API = require('../../utils/api');

Page({
  data: {
    orderId: null,
    type: 1,        // 1 仅退款  2 退货退款
    reason: ''
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: Number(options.orderId) });
    } else {
      wx.showToast({ title: '参数错误', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1500);
    }
  },

  // 选择售后类型
  selectType(e) {
    this.setData({ type: Number(e.currentTarget.dataset.type) });
  },

  // 填写原因
  onInput(e) {
    this.setData({ reason: e.detail.value });
  },

  // 提交申请
  async submit() {
    if (!this.data.reason.trim()) {
      return wx.showToast({ title: '请填写申请原因', icon: 'none' });
    }

    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      return wx.showToast({ title: '请先登录', icon: 'none' });
    }

    wx.showLoading({ title: '提交中...' });

    try {
      await API.afterSale.apply({
        orderId: this.data.orderId,
        userId: user.id,
        type: this.data.type,
        reason: this.data.reason
      });

      wx.hideLoading();
      wx.showToast({ title: '申请成功', icon: 'success', duration: 2000 });

      setTimeout(() => {
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        if (prevPage && typeof prevPage.loadDetail === 'function') {
          prevPage.loadDetail();
        }
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      wx.hideLoading();
      console.error(err);
    }
  }
});
