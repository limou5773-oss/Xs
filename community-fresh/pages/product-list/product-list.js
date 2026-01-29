// pages/product-list/product-list.js
const app = getApp();
const API = require('../../utils/api');
const BASE_URL = 'http://localhost:8080';

Page({
  // 非响应式变量：记录右侧 scroll-view 的真实滚动位置
  _currentScrollTop: 0,

  data: {
    keyword: '',
    activeCategoryId: null, // null = 全部
    categoryList: [],       // 当前展示的数据
    rawCategoryList: [],    // 原始完整数据
    toView: '',             // 兼容之前逻辑，当前未使用
    totalPrice: '0.00',
    totalCount: 0,
    cartIdMap: {},
    cartAnimation: false,
    isLoading: false,

    // 右侧滚动联动相关
    scrollTop: 0,      // 只在“程序滚动”时使用
    sectionTops: [],   // 每个分类 header 距离顶部的距离 [{ id, top }]
    scrollLock: false  // 是否处于“程序滚动”中，防止 onScroll 乱改 activeCategoryId
  },

  onShow() {
    const activeCatId = wx.getStorageSync('active_category');
    if (activeCatId) {
      this.setData({
        activeCategoryId: Number(activeCatId)
      });
      wx.removeStorageSync('active_category');
    }
    this.initData();
  },

  onPullDownRefresh() {
    this.initData();
    wx.stopPullDownRefresh();
  },

  async initData() {
    if (this.data.categoryList.length === 0) {
      wx.showLoading({ title: '加载中...' });
    }

    try {
      const t = Date.now();
      const [catRes, prodRes] = await Promise.all([
        this.requestPromise(`${BASE_URL}/api/category/list?t=${t}`),
        this.requestPromise(
          `${BASE_URL}/api/product/page?t=${t}`,
          'POST',
          { page: 1, size: 1000, status: 1 }
        )
      ]);

      const categories = Array.isArray(catRes) ? catRes : (catRes.data || []);
      const products = (prodRes.data && prodRes.data.records)
        ? prodRes.data.records
        : [];

      // 组装数据（不过滤空分类）
      const organized = categories.map(cat => {
        const items = products
          .filter(p => p.categoryId == cat.id)
          .map(p => {
            p.image = this.handleImageUrl(p.image || p.cover);
            p.stock = Number(p.stock) || 0;
            p.price = Number(p.price) || 0;
            p.cartNum = 0;
            return p;
          });
        return { ...cat, products: items };
      });

      const finalData = organized;
      const currentId = this.data.activeCategoryId;

      // 计算本次要设置的 scrollTop
      const newScrollTop = currentId ? this.data.scrollTop : 0;
      this._currentScrollTop = newScrollTop;

      this.setData({
        categoryList: finalData,
        rawCategoryList: finalData,
        activeCategoryId: currentId,
        scrollTop: newScrollTop
      });

      // 等视图渲染完再计算各分类位置 + 自动滚到当前分类
      wx.nextTick(() => {
        this.calcSectionTops(() => {
          this.scrollToCategory(this.data.activeCategoryId, false);
        });
      });

      this.syncCartData();
    } catch (err) {
      console.error(err);
    } finally {
      wx.hideLoading();
    }
  },

  // 计算右侧每个分类 header 的 top
  calcSectionTops(callback) {
    const query = wx.createSelectorQuery().in(this);
    query.select('.right-content').fields({ rect: true, size: true });
    query.selectAll('.js-cat-header').fields({ rect: true, dataset: true });
    query.exec(res => {
      const scrollViewRect = res[0];
      const headers = res[1] || [];
      if (!scrollViewRect || !headers.length) {
        this.setData({ sectionTops: [] });
        if (typeof callback === 'function') callback();
        return;
      }

      const baseTop = scrollViewRect.top;
      const currentScrollTop = this._currentScrollTop || 0;

      const sectionTops = headers.map(h => ({
        id: Number(h.dataset.id),
        top: h.top - baseTop + currentScrollTop
      }));

      this.setData({ sectionTops });
      if (typeof callback === 'function') callback();
    });
  },

  // 滚动到指定分类
  scrollToCategory(id, useLock = true) {
    // id 为 null 表示滚到顶部（全部）
    if (id === undefined || id === null || id === 'null') {
      const targetTop = 0;
      this._currentScrollTop = targetTop;

      if (useLock) {
        this.setData({ scrollLock: true, scrollTop: targetTop });
        setTimeout(() => {
          this.setData({ scrollLock: false });
        }, 300);
      } else {
        this.setData({ scrollTop: targetTop });
      }
      return;
    }

    const list = this.data.sectionTops || [];
    const numId = Number(id);
    const match = list.find(item => item.id === numId);
    if (!match) return;

    const targetTop = match.top;
    this._currentScrollTop = targetTop;

    if (useLock) {
      this.setData({ scrollLock: true, scrollTop: targetTop });
      setTimeout(() => {
        this.setData({ scrollLock: false });
      }, 300);
    } else {
      this.setData({ scrollTop: targetTop });
    }
  },

  // 点击左侧分类（含“全部”）
  onCategoryChange(e) {
    let id = e.currentTarget.dataset.id;

    if (id === undefined || id === null || id === 'null') {
      id = null;
    } else {
      id = Number(id);
    }

    this.setData({ activeCategoryId: id });
    this.scrollToCategory(id, true);
  },

  // 右侧滚动时联动左侧高亮
  onRightScroll(e) {
    const scrollTop = e.detail.scrollTop || 0;
    this._currentScrollTop = scrollTop; // 只更新内部变量，不 setData

    if (this.data.scrollLock) {
      // 正在执行“程序滚动”，忽略这次滚动事件
      return;
    }

    const list = this.data.sectionTops;
    if (!list || !list.length) return;

    const firstTop = list[0].top || 0;
    let activeId = null;

    // 顶部区域较小时，高亮“全部”
    if (scrollTop < firstTop / 2) {
      activeId = null;
    } else {
      const offset = 5;
      activeId = list[0].id;
      for (let i = 0; i < list.length; i++) {
        if (scrollTop + offset >= list[i].top) {
          activeId = list[i].id;
        } else {
          break;
        }
      }
    }

    if (activeId !== this.data.activeCategoryId) {
      this.setData({ activeCategoryId: activeId });
    }
  },

  async syncCartData() {
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      this.setData({
        totalCount: 0,
        totalPrice: '0.00',
        cartIdMap: {}
      });
      const cleanList = this.data.categoryList.map(c => {
        c.products.forEach(p => (p.cartNum = 0));
        return c;
      });
      this.setData({ categoryList: cleanList });
      return;
    }
    try {
      const res = await API.cart.list(user.id);
      const cartItems = Array.isArray(res) ? res : (res.data || []);
      let count = 0;
      let price = 0;
      const qtyMap = {};
      const idMap = {};
      cartItems.forEach(item => {
        const qty = Number(item.quantity);
        const p = Number(item.price);
        count += qty;
        price += p * qty;
        qtyMap[item.productId] = qty;
        idMap[item.productId] = item.id;
      });
      const newList = this.data.categoryList.map(cat => {
        cat.products = cat.products.map(p => {
          p.cartNum = qtyMap[p.id] || 0;
          return p;
        });
        return cat;
      });
      this.setData({
        categoryList: newList,
        totalCount: count,
        totalPrice: price.toFixed(2),
        cartIdMap: idMap
      });
      // 一般高度变化不大，就不重新 calcSectionTops 了
    } catch (err) {
      console.error(err);
    }
  },

  async updateCart(e) {
    const { item, type } = e.currentTarget.dataset;
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    try {
      if (type === 'plus') {
        const stock = Number(item.stock);
        const currentNum = Number(item.cartNum);
        if (stock - currentNum <= 0) {
          return wx.showToast({ title: '库存不足', icon: 'none' });
        }
        await API.cart.add({
          userId: user.id,
          productId: item.id,
          quantity: 1,
          checked: 1,
          productName: item.name,
          price: item.price,
          cover: item.image
        });
        this.triggerCartAnimation();
      } else if (type === 'minus') {
        const cartId = this.data.cartIdMap[item.id];
        if (!cartId) return;
        if (item.cartNum === 1) {
          await API.cart.remove(cartId);
        } else {
          await API.cart.update(cartId, item.cartNum - 1);
        }
      }
      this.syncCartData();
    } catch (err) {
      console.error(err);
    }
  },

  triggerCartAnimation() {
    this.setData({ cartAnimation: true });
    setTimeout(() => {
      this.setData({ cartAnimation: false });
    }, 200);
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value });
    if (!e.detail.value) this.clearSearch();
  },

  onSearch() {
    const kw = this.data.keyword.trim();
    if (!kw) {
      this._currentScrollTop = 0;
      this.setData({
        categoryList: this.data.rawCategoryList,
        scrollTop: 0,
        activeCategoryId: null
      });
      wx.nextTick(() => {
        this.calcSectionTops();
      });
      return;
    }
    const filtered = this.data.rawCategoryList
      .map(cat => {
        const matchProducts = cat.products.filter(
          p => p.name.indexOf(kw) !== -1
        );
        return { ...cat, products: matchProducts };
      })
      .filter(c => c.products.length > 0);

    this._currentScrollTop = 0;
    this.setData({
      categoryList: filtered,
      scrollTop: 0,
      activeCategoryId: filtered.length ? filtered[0].id : null
    });
    wx.nextTick(() => {
      this.calcSectionTops();
    });
  },

  clearSearch() {
    this._currentScrollTop = 0;
    this.setData({
      keyword: '',
      categoryList: this.data.rawCategoryList,
      scrollTop: 0,
      activeCategoryId: null
    });
    wx.nextTick(() => {
      this.calcSectionTops();
    });
  },

  goDetail(e) {
    wx.navigateTo({
      url: `/pages/product-detail/product-detail?id=${e.currentTarget.dataset.id}`
    });
  },

  goCart() {
    wx.navigateTo({ url: '/pages/cart/cart' });
  },

  goCheckout() {
    if (this.data.totalCount > 0) {
      wx.navigateTo({
        url: '/pages/checkout/checkout?type=cart_checkout'
      });
    }
  },

  onRightScrollBottom() {},

  // 避免步进器区域继续冒泡
  preventBubble() {},

  requestPromise(url, method = 'GET', data = {}) {
    return new Promise(resolve => {
      let token = wx.getStorageSync('token');
      if (token && !token.startsWith('Bearer ')) {
        token = 'Bearer ' + token;
      }
      wx.request({
        url,
        method,
        data,
        header: {
          Authorization: token || ''
        },
        success: res => resolve(res.data),
        fail: () => resolve(null)
      });
    });
  },

  handleImageUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    return `${BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
  }
});
