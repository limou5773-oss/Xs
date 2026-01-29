const API = require('../../utils/api');
const BASE_URL = 'http://localhost:8080';

Page({
  data: {
    address: null,
    goodsList: [],
    totalPrice: '0.00',
    totalCount: 0,
    remark: '',
    showPayPopup: false,
    lastOrderId: null
  },

  onShow() {
    // 1. 地址回显
    const selectAddr = wx.getStorageSync('selectAddress');
    if (selectAddr) {
      this.setData({ address: selectAddr });
      wx.removeStorageSync('selectAddress');
    } else if (!this.data.address) {
      this.loadDefaultAddress();
    }

    // 2. 加载商品
    this.loadCheckoutGoods();
  },

  // 加载默认地址
  async loadDefaultAddress() {
    const user = wx.getStorageSync('user');
    if (!user) return;

    try {
      const res = await API.address.list(user.id);
      const list = Array.isArray(res) ? res : (res.data || []);
      const def = list.find(a => a.isDefault) || list[0];
      if (def) this.setData({ address: def });
    } catch (e) {
      console.error(e);
    }
  },

  // 加载结算商品
  async loadCheckoutGoods() {
    const user = wx.getStorageSync('user');
    if (!user) return;

    try {
      const res = await API.cart.list(user.id);
      const allItems = Array.isArray(res) ? res : (res.data || []);
      
      // 只结算勾选的
      const checkedItems = allItems.filter(i => i.checked === 1);

      if (checkedItems.length === 0) {
        wx.showToast({ title: '未选择商品', icon: 'none' });
        setTimeout(() => wx.navigateBack(), 1500);
        return;
      }

      let total = 0;
      let count = 0;

      const list = checkedItems.map(item => {
        item.cover = this.handleImageUrl(item.cover);
        total += item.price * item.quantity;
        count += item.quantity;
        return item;
      });

      this.setData({
        goodsList: list,
        totalPrice: total.toFixed(2),
        totalCount: count
      });

    } catch (e) {
      console.error(e);
    }
  },


  async submitOrder() {
    if (!this.data.address) {
      return wx.showToast({ title: '请选择收货地址', icon: 'none' });
    }

    const user = wx.getStorageSync('user');
    const addr = this.data.address;

    const items = this.data.goodsList.map(g => ({
      productId: g.productId,
      productName: g.productName,
      cover: g.cover,
      price: g.price,
      quantity: g.quantity
    }));

    const postData = {
      userId: user.id,
      merchantId: 1,
      remark: this.data.remark,
      addressSnapshot: JSON.stringify({
        name: addr.name,
        phone: addr.phone,
        region: addr.region,
        detail: addr.detail
      }),
      items: items
    };

    wx.showLoading({ title: '提交中...' });

    try {
      const res = await API.order.create(postData);
      
      wx.hideLoading();
      const orderId = res; 
      
      if (orderId) {
        this.setData({
          lastOrderId: orderId,
          showPayPopup: true // 弹出支付层
        });
      } else {
        wx.showToast({ title: '订单创建失败', icon: 'none' });
      }

    } catch (err) {
      wx.hideLoading();
      console.error(err);
      wx.showToast({ title: '提交失败', icon: 'none' });
    }
  },

  // 确认支付
  async confirmPay() {
    const orderId = this.data.lastOrderId;
    if (!orderId) return;

    try {
      await API.order.pay(orderId);
      
      this.setData({ showPayPopup: false });
      wx.showToast({ title: '支付成功', icon: 'success' });

      // 跳转到订单列表
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/order/order?status=1' }); // 跳到待发货
      }, 800);

    } catch (err) {
      wx.showToast({ title: '支付失败', icon: 'none' });
    }
  },

  cancelPay() {
    this.setData({ showPayPopup: false });
    wx.showToast({ title: '订单已生成，请尽快支付', icon: 'none' });

    setTimeout(() => {
        wx.redirectTo({ url: '/pages/order/order?status=0' }); 
    }, 1000);
  },

  goAddressList() {
    wx.navigateTo({ url: '/pages/address/address?type=select' });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  }
});