// pages/address-edit/address-edit.js
const API = require('../../utils/api');

Page({
  data: {
    form: {
      id: null,
      name: '',
      phone: '',
      region: '',
      detail: '',
      isDefault: false,
    },
    region: []
  },

  async onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id);
    }
  },

  async loadDetail(id) {
    try {
      const data = await API.address.get(id);

      data.isDefault = data.isDefault == 1 || data.is_default == 1;

      this.setData({
        form: data,
        region: data.region ? data.region.split(' ') : []
      });
    } catch {
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onRegionChange(e) {
    const value = e.detail.value.join(' ');
    this.setData({
      region: e.detail.value,
      'form.region': value
    });
  },

  onSwitch(e) {
    this.setData({
      'form.isDefault': e.detail.value
    });
  },

  async save() {
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    const form = { ...this.data.form, userId: user.id };

    if (!form.name || !form.phone || !form.region || !form.detail) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }

    try {
      if (form.id) {
        await API.address.update(form);
      } else {
        await API.address.add(form);
      }

      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 600);
    } catch {
      wx.showToast({ title: '保存失败', icon: 'none' });
    }
  }
});
