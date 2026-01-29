
const app = getApp();

const BASE_URL = 'http://localhost:8080';

Page({
  data: {
    categoryList: [],
    activeId: null,
    toView: '',
    totalPrice: '0.00',
    totalCount: 0
  },

  onShow() {
    this.initData();
  },

  async initData() {
    wx.showLoading({ title: '加载中' });
    try {
      const catRes = await this.request(`${BASE_URL}/api/category/list`);
      const categories = catRes.data || [];

      const prodRes = await this.request(`${BASE_URL}/api/product/page`, 'POST', { page: 1, size: 1000, status: 1 });
      const products = prodRes.data.records || [];

      const organized = categories.map(cat => {
        const items = products.filter(p => p.categoryId === cat.id).map(p => {
          p.image = this.handleImageUrl(p.image || p.cover);
          p.cartNum = 0; 
          return p;
        });
        return { ...cat, products: items };
      }).filter(c => c.products.length > 0); 

      this.setData({ 
        categoryList: organized,
        activeId: organized.length > 0 ? organized[0].id : null
      });
      
      // 4. 同步购物车数据
      this.syncCartData();

    } catch (err) { console.error(err); } 
    finally { wx.hideLoading(); }
  },

  // 同步购物车 (更新数量和总价)
  syncCartData() {
    const token = wx.getStorageSync('token');
    if (!token) return;

    wx.request({
      url: `${BASE_URL}/api/cart/list/1`, // ⚠️这里 1 换成真实 userId
      header: { 'Authorization': token },
      success: (res) => {
        if (res.data.code === 200) {
          const cartItems = res.data.data || [];
          let count = 0;
          let price = 0;
          const map = {};

          cartItems.forEach(item => {
            count += item.quantity;
            price += item.price * item.quantity;
            map[item.productId] = item.quantity;
          });

          const newCatList = this.data.categoryList.map(cat => {
            cat.products = cat.products.map(p => {
              p.cartNum = map[p.id] || 0;
              return p;
            });
            return cat;
          });

          this.setData({
            categoryList: newCatList,
            totalCount: count,
            totalPrice: price.toFixed(2)
          });
        }
      }
    });
  },

  // 加减操作
  updateCart(e) {
    const { item, type } = e.currentTarget.dataset;
    const token = wx.getStorageSync('token');
    if (!token) { wx.navigateTo({ url: '/pages/login/login' }); return; }

    if (type === 'plus') {
        // 加
        wx.request({
            url: `${BASE_URL}/api/cart/add`,
            method: 'POST', header: { 'Authorization': token },
            data: { productId: item.id, quantity: 1, checked: 1 },
            success: () => this.syncCartData()
        });
    } else {
        // 减 
        wx.showToast({ title: '请去结算修改', icon: 'none' });
    }
  },

  onMenuClick(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ activeId: id, toView: 'cat_' + id });
  },

  goDetail(e) {
    wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${e.currentTarget.dataset.id}` });
  },

  goCart() { wx.switchTab({ url: '/pages/cart/cart' }); },
  
  goCheckout() {
    if (this.data.totalCount > 0) {
      wx.navigateTo({ url: '/pages/order-detail/order-detail?type=cart_checkout' });
    }
  },

  request(url, method = 'GET', data = {}) {
    return new Promise((resolve, reject) => {
      wx.request({ url, method, data, header: { 'Authorization': wx.getStorageSync('token') }, success: resolve, fail: reject });
    });
  },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  }
});