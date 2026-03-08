const API = require('../../utils/api');

const wxLoginPromise = () => {
  return new Promise((resolve, reject) => {
    wx.login({ success: resolve, fail: reject });
  });
};

Page({
  data: { isLogging: false },

  async onLogin() {
    if (this.data.isLogging) return;
    this.setData({ isLogging: true });

    try {
      // 1. 直接获取 Code (不需要弹窗授权)
      const loginRes = await wxLoginPromise();
      if (!loginRes.code) throw new Error('获取Code失败');

      // 2. 调用后端登录
      const res = await API.auth.wxLogin({
        code: loginRes.code
      });

      // 3. 存 Token
      const token = res.token || (res.data && res.data.token);
      const user = res.user || (res.data && res.data.user);

      if (!token) throw new Error('未获取到Token');

      wx.setStorageSync('token', token);

      // 如果后端返回的用户信息里没有头像(是新用户)，给个默认的
      if (!user.avatar) {
        user.avatar = '/assets/default-avatar.png'; // 你的本地默认头像
        user.nickname = '微信用户';
      }
      wx.setStorageSync('user', user);

      wx.showToast({ title: '登录成功', icon: 'success' });

      // 4. 判断：如果是新用户（昵称是默认的），跳转去编辑资料页
      // 否则返回上一页
      setTimeout(() => {
        if (user.nickname === '微信用户' || !user.nickname) {
          wx.redirectTo({ url: '/pages/edit-profile/edit-profile' });
        } else {
          wx.navigateBack();
        }
      }, 1500);

    } catch (err) {
      console.error('登录报错:', err);
      wx.showToast({ title: '登录失败', icon: 'none' });
    } finally {
      this.setData({ isLogging: false });
    }
  }
});