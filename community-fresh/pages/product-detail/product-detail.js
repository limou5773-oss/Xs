const app = getApp();
const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080";

Page({
  data: {
    id: null,
    product: null,
    cartCount: 0,
    loading: true
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadProductDetail(options.id);
    }
    this.updateCartCount();
  },

  // 核心：每次显示都刷新库存
  onShow() {
    if (this.data.id) {
      this.loadProductDetail(this.data.id);
      this.updateCartCount();
    }
  },

  async loadProductDetail(id) {

    try {
      // 加时间戳防止缓存
      const t = Date.now();
      const res = await API.product.detail(id + `?t=${t}`); 
      
      const item = res.data || res;
      if (item) {
        if (item.cover && !item.cover.startsWith('http')) {
          item.cover = BASE_URL + item.cover;
        }
        item.image = item.cover; 
        this.setData({ product: item });
      }
    } catch (err) {
      console.error(err);
    } finally {
      this.setData({ loading: false });
    }
  },

  async addToCart() {
    const item = this.data.product;
    const user = wx.getStorageSync('user');

    if (!user || !user.id) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }

    if (item.stock <= 0) {
      wx.showToast({ title: '已售罄', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '处理中' });
    try {
      await API.cart.add({
        userId: user.id,
        productId: item.id,
        quantity: 1,
        checked: 1,
        productName: item.name, 
        price: item.price,
        cover: item.image 
      });

      wx.showToast({ title: '已加入', icon: 'success' });
      this.updateCartCount();

    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

// 3. 跳转购物车
goCart() {
    wx.navigateTo({
      url: '/pages/cart/cart'
    });
  },
  async updateCartCount() {
    const user = wx.getStorageSync('user');
    if (!user) return;
    try {
      const res = await API.cart.list(user.id);
      const list = Array.isArray(res) ? res : (res.data || []);
      let total = 0;
      list.forEach(item => total += item.quantity);
      this.setData({ cartCount: total });
    } catch (e) { }
  },

  goHome() {
    wx.switchTab({ url: '/pages/home/home' });
  },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  }
});