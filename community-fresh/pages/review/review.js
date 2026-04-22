// 引入写好的接口统一管理文件
const API = require('../../utils/api.js')

Page({
  data: {
    rating: 5,
    content: '',
    orderId: null,
    merchantId: null
  },

  onLoad(options) {
    this.setData({
      orderId: options.orderId,
      merchantId: options.merchantId
    })
  },

  setRating(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ rating: index + 1 })
  },

  onInput(e) {
    this.setData({ content: e.detail.value })
  },

  async onSubmit() {
    const user = wx.getStorageSync('user')
    const { rating, content, orderId, merchantId } = this.data

    if (!content.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' })
      return
    }

    wx.showLoading({ title: '提交中...' })

    try {
      const res = await API.review.add({
        userId: user.id,
        merchantId: merchantId,
        orderId: orderId,
        rating: rating,
        content: content
      })

      wx.hideLoading()
      
      // 适配你的 Result 格式
      wx.showToast({ title: '评价成功', icon: 'success' })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)

    } catch (err) {
      wx.hideLoading()
      console.error(err)
      wx.showToast({ title: '提交失败，请重试', icon: 'none' })
    }
  }
})