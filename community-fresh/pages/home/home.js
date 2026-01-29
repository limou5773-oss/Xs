// pages/home/home.js
const app = getApp();
const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080"; 

Page({
  data: {
    // 配送/订单状态信息
    deliveryInfo: {
      show: false,         
      statusText: '',      
      subText: '',         
      staffName: '店长·张',
      isDelivering: false  
    },
    menuItems: [], 
    products: [],  
    isLoading: false,
    notice: '' // 公告内容
  },

  onLoad: function () {
    this.initData();
  },

  onShow: function () {
    this.checkLatestOrder();
    this.initData();
  },

  onPullDownRefresh: function () {
    this.checkLatestOrder();
    this.initData(() => {
      wx.stopPullDownRefresh();
    });
  },

  async initData(callback) {
    if (this.data.products.length === 0) {
        this.setData({ isLoading: true });
    }

    try {
      const t = Date.now(); 
      
      // 并发请求：分类 + 推荐 + 公告
      const [catRes, prodRes, noticeRes] = await Promise.all([
        this.requestPromise(`${BASE_URL}/api/category/list?t=${t}`),
        this.requestPromise(`${BASE_URL}/api/product/recommend?t=${t}`),
        this.requestPromise(`${BASE_URL}/api/notice/latest?t=${t}`)
      ]);

      // 1. 处理分类 (只取前4个，适配一行布局)
      const catList = (catRes && (Array.isArray(catRes) ? catRes : catRes.data)) || [];
      const finalCatList = catList.slice(0, 4).map(item => ({
        id: item.id,
        name: item.name
      }));

      // 2. 处理推荐商品
      const prodListRaw = (prodRes && (Array.isArray(prodRes) ? prodRes : prodRes.data)) || [];
      const finalProdList = prodListRaw.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        stock: item.stock,
        unit: item.unit || '份',
        description: item.description,
        image: this.handleImageUrl(item.cover || item.image)
      }));

      // 3. 处理公告
      let noticeContent = '';
      if (noticeRes) {
         noticeContent = noticeRes.data || (typeof noticeRes === 'string' ? noticeRes : '');
         // 去掉可能存在的 HTML 标签
         noticeContent = noticeContent.replace(/<[^>]+>/g, '');
      }

      this.setData({
        menuItems: finalCatList,
        products: finalProdList,
        notice: noticeContent
      });

    } catch (err) {
      console.error("首页数据加载失败", err);
    } finally {
      this.setData({ isLoading: false });
      if (callback) callback();
    }
  },

  async checkLatestOrder() {
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      this.setData({ 'deliveryInfo.show': false });
      return;
    }
    try {
      const res = await API.order.list(user.id);
      let list = Array.isArray(res) ? res : (res.data || []);
      
      list.sort((a, b) => {
          const t1 = new Date(a.createTime.replace('T', ' ').replace(/-/g, '/')).getTime();
          const t2 = new Date(b.createTime.replace('T', ' ').replace(/-/g, '/')).getTime();
          return t2 - t1; 
      });

      const activeOrder = list.find(o => [1, 2, 3].includes(o.status));
      
      let info = { show: true, staffName: '店长·张', isDelivering: false };

      if (activeOrder) {
        if (activeOrder.status === 1) {
          info.statusText = '商家已接单';
          info.subText = '正在为您精心备货中...';
        } else if (activeOrder.status === 2) {
          info.statusText = '商家正在为您配送中';
          info.isDelivering = true;
          const now = new Date();
          const target = new Date(now.getTime() + 20 * 60000);
          const pad = n => n < 10 ? '0' + n : n;
          info.subText = `预计送达 ${pad(target.getHours())}:${pad(target.getMinutes())}`;
        } else if (activeOrder.status === 3) {
           info.statusText = '订单已送达';
           info.subText = '感谢您的光临，期待再次相遇';
        } else { info.show = false; }
      } else { info.show = false; }

      this.setData({ deliveryInfo: info });
    } catch (e) {
      this.setData({ 'deliveryInfo.show': false });
    }
  },

  async addToCart(e) {
    const item = e.currentTarget.dataset.item;
    const user = wx.getStorageSync('user');
    if (!user || !user.id) { wx.navigateTo({ url: '/pages/login/login' }); return; }
    if (item.stock <= 0) { wx.showToast({ title: '已售罄', icon: 'none' }); return; }

    wx.showLoading({ title: '加入中' });
    try {
      await API.cart.add({
        userId: user.id, productId: item.id, quantity: 1, checked: 1,
        productName: item.name, price: item.price, cover: item.image 
      });
      wx.showToast({ title: '已加入', icon: 'success' });
    } catch (err) { console.error(err); } finally { wx.hideLoading(); }
  },

  requestPromise(url, method = 'GET', data = {}) {
    return new Promise((resolve) => {
      let token = wx.getStorageSync('token');
      if (token && !token.startsWith('Bearer ')) token = 'Bearer ' + token;
      wx.request({
        url, method, data, 
        header: { 'Authorization': token || '' },
        success: res => resolve(res.data),
        fail: () => resolve(null)
      });
    });
  },

  goProductList() { wx.switchTab({ url: '/pages/product-list/product-list' }); },
  
  onGridTap(e) {
    const catId = e.currentTarget.dataset.id;
    wx.setStorageSync('active_category', catId);
    this.goProductList();
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    if(id) wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${id}` });
  },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  },

  calculateDeliveryTime() {}, 
  callShop() { wx.makePhoneCall({ phoneNumber: '13800138000' }); }
});