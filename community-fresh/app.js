// app.js
App({
    // 全局数据
    globalData: {
      userInfo: null,
      token: null,
      // ⭐ 建议把 IP 放在这里，虽然你现在页面里写死了，
      // 以后想优化时，可以统一用 getApp().globalData.baseUrl
      baseUrl: 'http://localhost:8080' 
    },
  
    onLaunch() {
      // 1. 启动时，检查本地有没有缓存的 Token 和 用户信息
      const token = wx.getStorageSync('token');
      const user = wx.getStorageSync('user');
  
      if (token && user) {
        // 如果有缓存，同步到全局变量
        this.globalData.token = token;
        this.globalData.userInfo = user;
        console.log('App启动: 已恢复登录状态', user.nickname);
      } else {
        console.log('App启动: 未登录');
      }
    }
  })