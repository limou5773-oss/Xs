const request = require('../../utils/request.js')

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

  onSubmit() {
    const user = wx.getStorageSync('user')
    const { rating, content, orderId, merchantId } = this.data

    if (!content.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' })
      return
    }

    request.post('/review/add', {
      userId: user.id,
      merchantId,
      orderId,
      rating,
      content
    }).then(res => {
      if (res.code === 200) {
        wx.showToast({ title: '评价成功' })
        wx.navigateBack()
      } else {
        wx.showToast({ title: res.msg, icon: 'error' })
      }
    })
  }
})
