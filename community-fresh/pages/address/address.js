// pages/address/address.js
const BASE_URL = "http://localhost:8080";

Page({
  data: {
    addresses: [],
    type: ""
  },

  onLoad(options) {
    if (options.type) {
      this.setData({ type: options.type });
    }
  },

  onShow() {
    this.loadAddressList();
  },

  /** 加载用户全部地址 */
  loadAddressList() {
    const user = wx.getStorageSync("user");
    const token = wx.getStorageSync("token");

    if (!user || !user.id) {
      wx.showToast({ title: "请先登录", icon: "none" });
      return;
    }

    wx.request({
      url: `${BASE_URL}/api/address/list/${user.id}`,
      method: "GET",
      header: {
        Authorization: token.startsWith("Bearer") ? token : "Bearer " + token
      },
      success: (res) => {
        if (res.data.code === 200) {
          const list = res.data.data || [];

          list.forEach(a => {
            a.isDefault = a.isDefault == 1 || a.is_default == 1;
          });

          this.setData({ addresses: list });
        } else {
          wx.showToast({ title: res.data.msg || "加载失败", icon: "none" });
        }
      }
    });
  },

  /** 新增地址（⚠ 修正路径） */
  goAdd() {
    wx.navigateTo({
      url: "/pages/address-edit/address-edit"
    });
  },

  /** 编辑地址（⚠ 修正路径） */
  goEdit(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/address-edit/address-edit?id=${id}`
    });
  },

  /** 删除地址 */
  deleteAddress(e) {
    const id = e.currentTarget.dataset.id;
    const token = wx.getStorageSync("token");

    wx.showModal({
      title: "提示",
      content: "确定删除此地址吗？",
      success: (res) => {
        if (res.confirm) {
          wx.request({
            url: `${BASE_URL}/api/address/delete/${id}`,
            method: "DELETE",
            header: {
              Authorization: token.startsWith("Bearer") ? token : "Bearer " + token
            },
            success: (r) => {
              wx.showToast({ title: "删除成功", icon: "success" });
              this.loadAddressList();
            }
          });
        }
      }
    });
  },

  /** 订单页选择地址 */
  chooseAddress(e) {
    if (this.data.type !== "select") return;

    const item = e.currentTarget.dataset.item;
    wx.setStorageSync("selectAddress", item);
    wx.navigateBack();
  }
});
