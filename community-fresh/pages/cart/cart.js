// pages/cart/cart.js
const app = getApp();
const BASE_URL = 'http://localhost:8080';

Page({
  data: {
    cartList: [],
    totalPrice: '0.00',
    totalCount: 0,
    isAllChecked: false,
    recommendList: [],
    isEditMode: false
  },

  onShow() {
    this.loadCartData();
    this.loadRecommend();
    this.setData({ isEditMode: false });
  },

  onPullDownRefresh() {
    this.loadCartData();
    this.loadRecommend();
    wx.stopPullDownRefresh();
  },

  toggleEditMode() {
    const nextMode = !this.data.isEditMode;
    this.setData({ isEditMode: nextMode });

    if (nextMode) {
      const resetList = this.data.cartList.map(item => {
        item.checked = 0;
        return item;
      });
      this.setData({ 
        cartList: resetList,
        isAllChecked: false,
        totalCount: 0, // 选中的数量归零
        totalPrice: '0.00'
      });
    } else {
      this.loadCartData();
    }
  },

  // 批量删除
  batchDelete() {
    const selectedItems = this.data.cartList.filter(item => item.checked);
    if (selectedItems.length === 0) {
      return wx.showToast({ title: '请先勾选要删除的商品', icon: 'none' });
    }

    wx.showModal({
      title: '警告',
      content: `确定删除这 ${selectedItems.length} 件商品吗？`,
      confirmColor: '#ff4d4f',
      success: (res) => {
        if (res.confirm) {
          this.executeBatchDelete(selectedItems);
        }
      }
    });
  },

  executeBatchDelete(items) {
    const token = wx.getStorageSync('token');
    let auth = token.startsWith('Bearer ') ? token : 'Bearer ' + token;
    
    wx.showLoading({ title: '删除中' });

    const promises = items.map(item => {
      return new Promise((resolve) => {
        wx.request({
          url: `${BASE_URL}/api/cart/remove/${item.id}`,
          method: 'DELETE',
          header: { 'Authorization': auth },
          success: resolve,
          fail: resolve
        });
      });
    });

    Promise.all(promises).then(() => {
      wx.hideLoading();
      wx.showToast({ title: '删除成功', icon: 'success' });
      this.loadCartData();
      this.setData({ isEditMode: false });
    });
  },

  loadCartData() {
    const token = wx.getStorageSync('token');
    const user = wx.getStorageSync('user');
    if (!token || !user || !user.id) {
      this.setData({ cartList: [], totalCount: 0, totalPrice: '0.00' });
      return;
    }
    let auth = token.startsWith('Bearer ') ? token : 'Bearer ' + token;
    wx.request({
      url: `${BASE_URL}/api/cart/list/${user.id}`,
      header: { 'Authorization': auth },
      success: (res) => {
        if (res.statusCode === 401) {
            wx.removeStorageSync('token');
            this.setData({ cartList: [] });
            return;
        }
        if (res.data.code === 200) {
          const list = res.data.data || [];
          const newList = list.map(item => {
            item.cover = this.handleImageUrl(item.cover);
            return item;
          });
          this.setData({ cartList: newList });
          this.calculateTotal();
        }
      }
    });
  },

  loadRecommend() {
    wx.request({
      url: `${BASE_URL}/api/product/recommend`,
      method: 'GET',
      success: (res) => {
        if (res.data.code === 200) {
          const list = res.data.data || [];
          const newList = list.map(item => ({
            ...item,
            image: this.handleImageUrl(item.cover)
          }));
          this.setData({ recommendList: newList });
        }
      }
    });
  },

  toggleCheck(e) {
    const { id, checked } = e.currentTarget.dataset;
    const newVal = checked === 1 ? 0 : 1;
    const list = this.data.cartList.map(i => { if(i.id===id) i.checked=newVal; return i; });
    this.setData({ cartList: list });
    this.calculateTotal();
    if (!this.data.isEditMode) {
      this.requestUpdate(id, { checked: newVal });
    }
  },
  
  toggleAll() {
    const newVal = !this.data.isAllChecked ? 1 : 0;
    const list = this.data.cartList.map(i => { 
        i.checked=newVal; 
        if (!this.data.isEditMode) this.requestUpdate(i.id, {checked: newVal}); 
        return i; 
    });
    this.setData({ cartList: list, isAllChecked: !!newVal });
    this.calculateTotal();
  },

  updateQuantity(e) {
    const { id, type } = e.currentTarget.dataset;
    const item = this.data.cartList.find(i => i.id === id);
    let num = item.quantity;
    if (type === 'plus') {
        if(num >= item.stock) return wx.showToast({title:'库存不足', icon:'none'});
        num++;
    } else num--;
    if (num < 1) return;
    item.quantity = num;
    this.setData({ cartList: this.data.cartList });
    this.calculateTotal();
    this.requestUpdate(id, { quantity: num });
  },

  deleteItem(e) {
    const id = e.currentTarget.dataset.id;
    const token = wx.getStorageSync('token');
    let auth = token.startsWith('Bearer ') ? token : 'Bearer '+token;
    wx.showModal({
      title: '提示', content: '确认删除？',
      success: (res) => {
        if(res.confirm) {
          wx.request({
            url: `${BASE_URL}/api/cart/remove/${id}`, method: 'DELETE', header: {'Authorization': auth},
            success: (r) => { if(r.data.code===200) this.loadCartData(); }
          });
        }
      }
    });
  },

  calculateTotal() {
    let total = 0; let count = 0; let allChecked = true;
    if(this.data.cartList.length===0) allChecked=false;
    
    this.data.cartList.forEach(i => {
      if(i.checked) { 
          total += i.price * i.quantity; 
          count += i.quantity; 
      } else {
          allChecked = false;
      }
    });
    this.setData({ totalPrice: total.toFixed(2), totalCount: count, isAllChecked: allChecked });
  },

  requestUpdate(id, data) {
    const token = wx.getStorageSync('token');
    let auth = token.startsWith('Bearer ') ? token : 'Bearer '+token;
    const url = data.hasOwnProperty('checked') ? `${BASE_URL}/api/cart/check/${id}` : `${BASE_URL}/api/cart/update/${id}`;
    wx.request({ url, method: 'POST', header: {'Authorization': auth}, data });
  },

  goCheckout() {
    if (this.data.totalCount > 0)
      wx.navigateTo({
        url: '/pages/checkout/checkout?type=cart_checkout'
      });
  },
  goAllProducts() { wx.switchTab({ url: '/pages/product-list/product-list' }); },
  goHome() { wx.switchTab({ url: '/pages/product-list/product-list' }); },
  goDetail(e) { wx.navigateTo({ url: `/pages/product-detail/product-detail?id=${e.currentTarget.dataset.pid}` }); },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  }
});