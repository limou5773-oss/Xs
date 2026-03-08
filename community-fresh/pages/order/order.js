// pages/order/order.js
const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080";

// ⭐ 售后状态文案映射
// 0待处理 1退款成功 2拒绝 3用户取消
const AFTER_SALE_TEXT = {
  0: '售后处理中',
  1: '售后成功',
  2: '售后已拒绝',
  3: '售后已取消'
};

Page({
  data: {
    tabs: [
      { text: '全部', status: -1 },
      { text: '待支付', status: 0 },
      { text: '待发货', status: 1 },
      { text: '待收货', status: 2 },
      { text: '已完成', status: 3 },
      { text: '售后', status: 5 }
    ],
    activeStatus: -1,
    orders: [],
    filtered: [],
    userId: null,
    
    // 👇 新增了状态 6：已评价
    statusText: ['待支付', '待发货', '配送中', '已完成', '已取消', '售后中', '已评价'],
    statusColor: ['#fa5151', '#07c160', '#fa9d3b', '#07c160', '#999', '#fa5151', '#fa9d3b'],
    statusBg: ['#fee2e2', '#e1f3d8', '#fdf6ec', '#e1f3d8', '#f4f4f5', '#ffe5e5', '#fdf6ec'],

    // 支付弹窗控制
    showPayPopup: false,
    curPayOrder: null // 当前选中的要支付的订单
  },

  timer: null,

  onLoad(options) {
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    let status = options.status !== undefined && options.status !== ''
      ? Number(options.status)
      : -1;

    this.setData({
      userId: user.id,
      activeStatus: status
    });

    this.fetchOrders();
  },

  onShow() {
    if (this.data.userId) this.fetchOrders();
  },

  onHide() {
    clearInterval(this.timer);
  },

  onUnload() {
    clearInterval(this.timer);
  },

  // 拉取订单列表
  async fetchOrders() {
    if (this.data.orders.length === 0) {
      wx.showLoading({ title: '加载中' });
    }

    try {
      let list = await API.order.list(this.data.userId) || [];

      // 数据预处理
      list.forEach(o => {
        // 总价容错
        o.total = o.totalPrice || o.payPrice || 0;

        // 创建时间 & 时间戳
        if (o.createTime) {
          const timeStr = o.createTime.replace('T', ' ').replace(/-/g, '/');
          o.createTs = new Date(timeStr).getTime();
          o.createTimeDisplay = o.createTime.replace('T', ' ');
        }

        // 图片补全域名
        if (o.items && o.items.length > 0) {
          o.items.forEach(item => {
            if (item.cover && !item.cover.startsWith('http')) {
              item.cover = BASE_URL + item.cover;
            }
          });
        }
      });

      // 给 status=5 的订单补充售后状态文案
      await Promise.all(
        list.map(async (o) => {
          if (Number(o.status) === 5) {
            try {
              const as = await API.afterSale.detail(o.id); 
              if (as) {
                o.afterSaleStatus = as.status;                          // 0/1/2/3
                o.afterSaleText = AFTER_SALE_TEXT[as.status] || '售后'; // 给列表显示用
              }
            } catch (e) {
              console.error('加载售后信息失败', e);
            }
          }
        })
      );

      this.setData({ orders: list });
      this.applyFilter();   // 根据当前 tab 过滤
      this.startTimer();    // 开始倒计时
    } catch (err) {
      console.error(err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  // 👇 按状态过滤订单（全部 / 各个状态）
  applyFilter() {
    const { orders } = this.data;
    const activeStatus = Number(this.data.activeStatus); // 统一转成数字

    const filtered = activeStatus === -1
      ? orders
      : orders.filter(o => {
          // ⭐ 核心修改：如果当前选中了"已完成"(3)的Tab，那么"已评价"(6)的订单也显示出来
          if (activeStatus === 3) {
            return Number(o.status) === 3 || Number(o.status) === 6;
          }
          return Number(o.status) === activeStatus;
        });

    this.setData({ filtered });
    this.updateCountdown();
  },

  // 开启倒计时定时器
  startTimer() {
    if (this.timer) clearInterval(this.timer);
    this.updateCountdown();

    this.timer = setInterval(() => {
      this.updateCountdown();
    }, 1000);
  },

  // 更新倒计时显示
  updateCountdown() {
    const now = new Date().getTime();
    const TIME_LIMIT = 3 * 60 * 1000; // 3 分钟

    const newList = this.data.filtered.map(order => {
      if (order.status === 0 && order.createTs) {
        const endTime = order.createTs + TIME_LIMIT;
        const diff = endTime - now;
        if (diff > 0) {
          const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
          const s = Math.floor((diff % (1000 * 60)) / 1000);
          order.countdownStr = `剩余 ${m}:${s < 10 ? '0' + s : s}`;
        } else {
          order.countdownStr = '即将取消';
        }
      } else {
        order.countdownStr = '';
      }
      return order;
    });

    this.setData({ filtered: newList });
  },

  // 顶部 tab 点击
  onTabTap(e) {
    const status = Number(e.currentTarget.dataset.status);
    this.setData(
      { activeStatus: status },
      () => this.applyFilter()
    );
  },

  // 去支付按钮（列表里的）
  onPay(e) {
    const item = e.currentTarget.dataset.item;
    this.setData({
      curPayOrder: item,
      showPayPopup: true
    });
  },

  // 关闭支付弹窗
  closePayPopup() {
    this.setData({ showPayPopup: false });
  },

  // 确认支付
  async executePay() {
    const order = this.data.curPayOrder;
    if (!order) return;

    wx.showLoading({ title: '支付中...' });
    try {
      await API.order.pay(order.id);

      wx.hideLoading();
      this.setData({ showPayPopup: false });
      wx.showToast({ title: '支付成功', icon: 'success' });

      // 支付成功后刷新列表
      setTimeout(() => {
        this.fetchOrders();
      }, 1000);
    } catch (err) {
      console.error(err);
      wx.hideLoading();
      wx.showToast({ title: '支付失败', icon: 'none' });
    }
  },

  // 跳转订单详情
  goDetail(e) {
    wx.navigateTo({
      url: `/pages/order-info/order-info?id=${e.currentTarget.dataset.id}`
    });
  },

  // 跳转去评价页面
  goReview(e) {
    const item = e.currentTarget.dataset.item;
    wx.navigateTo({
      // 携带 orderId 和 merchantId 跳转
      url: `/pages/review/review?orderId=${item.id}&merchantId=${item.merchantId}`
    });
  }
});