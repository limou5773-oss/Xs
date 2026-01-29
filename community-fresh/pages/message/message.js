const API = require('../../utils/api');

Page({
  data: { list: [] },

  async onShow() {
    const user = wx.getStorageSync('user');
    if (!user) return;

    try {
      const list = await API.message.list(user.id);
      this.setData({ list: list || [] });
    } catch {
      wx.showToast({ title: '加载消息失败', icon: 'none' });
    }
  }
});
