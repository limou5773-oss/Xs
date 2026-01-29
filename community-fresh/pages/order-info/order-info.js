// pages/order-info/order-info.js
const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080";

Page({
  data: {
    orderId: null,
    order: {},
    address: {},
    items: [],

    // 0:待支付 1:待发货 2:配送中 3:已完成 4:已取消 5:售后中（包括处理中/成功/拒绝/取消）
    statusText: ['待支付', '待发货', '配送中', '已完成', '已取消', '售后中'],
    statusColor: ['#fa5151', '#409eff', '#fa9d3b', '#07c160', '#999', '#fa5151'],

    countdownStr: '',
    showPayPopup: false,

    headerStatusText: '',   
    headerStatusDesc: '',   
    afterSale: null
  },

  timer: null,

  onLoad(options) {
    if (!options.id) {
      wx.showToast({ title: "订单ID异常", icon: "none" });
      return;
    }
    this.setData({ orderId: options.id });
  },

  onShow() {
    this.loadDetail();
  },

  onHide() {
    if (this.timer) clearInterval(this.timer);
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer);
  },

  // 加载订单详情 + 售后详情
  async loadDetail() {
    try {
      const order = await API.order.detail(this.data.orderId);
      if (!order) {
        wx.showToast({ title: "订单不存在", icon: "none" });
        return;
      }

      // 地址快照
      let address = {};
      try {
        const addrStr = order.addressSnapshot || order.address_snapshot || "{}";
        address = JSON.parse(addrStr);
      } catch (e) {
        address = {};
      }

      // 补全商品图片
      const items = (order.items || []).map(it => ({
        ...it,
        cover: it.cover && it.cover.startsWith("http")
          ? it.cover
          : BASE_URL + (it.cover || "")
      }));

      this.setData({
        order,
        address,
        items
      });

      // 待支付倒计时
      if (order.status === 0 && order.createTime) {
        this.startCountdown(order.createTime);
      } else {
        this.setData({ countdownStr: '' });
        if (this.timer) clearInterval(this.timer);
      }

      // ===== 关键：如果订单处于售后（5），再查一条售后记录 =====
      if (order.status === 5) {
        let afterSale = null;
        try {
          afterSale = await API.afterSale.detail(order.id);
        } catch (e) {
          afterSale = null;
        }

        let headerStatusText = '';
        let headerStatusDesc = '';

        if (afterSale) {
          // 根据售后状态，给顶部卡片设置不同文案
          switch (afterSale.status) {
            case 0: // 待处理
              headerStatusText = '售后中';
              headerStatusDesc = '售后处理中，请耐心等待';
              break;
            case 1: // 已同意 / 已退款
              headerStatusText = '售后成功';
              headerStatusDesc = '退款已原路退回，请注意查收';
              break;
            case 2: // 已拒绝
              headerStatusText = '售后已拒绝';
              headerStatusDesc = '商家已拒绝本次售后申请';
              break;
            case 3: // 用户取消
              headerStatusText = '售后已取消';
              headerStatusDesc = '本次售后申请已取消';
              break;
            default:
              headerStatusText = '售后中';
              headerStatusDesc = '售后处理中，请耐心等待';
          }
        }

        this.setData({
          afterSale,
          headerStatusText,
          headerStatusDesc
        });
      } else {
        // 非售后状态，清空售后信息
        this.setData({
          afterSale: null,
          headerStatusText: '',
          headerStatusDesc: ''
        });
      }

    } catch (err) {
      console.error(err);
      wx.showToast({ title: "加载失败", icon: "none" });
    }
  },

  // 3 分钟倒计时
  startCountdown(createTimeStr) {
    if (this.timer) clearInterval(this.timer);

    const TIME_LIMIT = 3 * 60 * 1000;
    const formattedTime = createTimeStr.replace(/-/g, '/').replace('T', ' ');
    const createTime = new Date(formattedTime).getTime();
    if (isNaN(createTime)) return;

    const endTime = createTime + TIME_LIMIT;
    this.calcTime(endTime);

    this.timer = setInterval(() => {
      this.calcTime(endTime);
    }, 1000);
  },

  calcTime(endTime) {
    const now = Date.now();
    const diff = endTime - now;

    if (diff <= 0) {
      clearInterval(this.timer);
      this.setData({ countdownStr: '订单已超时' });
      return;
    }

    const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const s = Math.floor((diff % (1000 * 60)) / 1000);
    const str = `${m < 10 ? '0' + m : m}:${s < 10 ? '0' + s : s}`;
    this.setData({ countdownStr: `剩余 ${str}` });
  },

  // 去支付
  onPay() {
    this.setData({ showPayPopup: true });
  },

  closePayPopup() {
    this.setData({ showPayPopup: false });
  },

  async executePay() {
    wx.showLoading({ title: '支付中...' });
    try {
      await API.order.pay(this.data.orderId);
      wx.hideLoading();
      this.setData({ showPayPopup: false });
      wx.showToast({ title: '支付成功', icon: 'success' });
      this.loadDetail();
    } catch (err) {
      wx.hideLoading();
      console.error(err);
      wx.showToast({ title: '支付失败', icon: 'none' });
    }
  },

  // 申请售后
  applyRefund() {
    const id = this.data.orderId;
    wx.navigateTo({
      url: `/pages/refund/refund?orderId=${id}`
    });
  },

  // 取消售后申请
  cancelAfterSale() {
    const user = wx.getStorageSync('user');
    if (!user || !user.id) {
      return wx.showToast({ title: '请先登录', icon: 'none' });
    }

    wx.showModal({
      title: '提示',
      content: '确定要取消本次售后申请吗？',
      success: async (res) => {
        if (!res.confirm) return;

        wx.showLoading({ title: '处理中...' });
        try {
          await API.afterSale.cancel({
            orderId: this.data.orderId,
            userId: user.id
          });
          wx.hideLoading();
          wx.showToast({ title: '已取消', icon: 'success' });
          this.loadDetail();
        } catch (err) {
          wx.hideLoading();
          console.error(err);
          wx.showToast({ title: '操作失败', icon: 'none' });
        }
      }
    });
  },

  // 再来一单
  buyAgain() {
    wx.switchTab({
      url: '/pages/product-list/product-list'
    });
  }
});
