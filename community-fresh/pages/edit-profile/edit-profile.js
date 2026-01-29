const API = require('../../utils/api');
const BASE_URL = "http://localhost:8080"; 

Page({
  data: {
    avatarUrl: '', // 页面显示的头像链接
    nickname: '',  // 昵称
    isSaving: false // 保存按钮的 loading 状态
  },

  onLoad() {
    this.initData();
  },

  // 1. 初始化回显数据
  initData() {
    const user = wx.getStorageSync('user');
    if (user) {
      let showAvatar = user.avatar || '';
      if (showAvatar && !showAvatar.startsWith('http') && !showAvatar.startsWith('wxfile')) {
        showAvatar = BASE_URL + showAvatar;
      }

      this.setData({
        avatarUrl: showAvatar,
        nickname: user.nickname || ''
      });
    }
  },

  // 2. 监听头像选择
  onChooseAvatar(e) {
    const { avatarUrl } = e.detail;
    console.log("选择的新头像临时路径:", avatarUrl);
    // 这里只更新页面预览，此时还没上传到服务器
    this.setData({ avatarUrl });
  },

  // 3. 监听昵称输入
  onNicknameChange(e) {
    // type="nickname" 的 input 触发的是 change 事件
    const nickname = e.detail.value;
    this.setData({ nickname });
  },

  // 4. 保存资料 (核心逻辑)
  async saveProfile() {
    const { avatarUrl, nickname } = this.data;

    // 简单校验
    if (!nickname || nickname.trim() === '') {
      return wx.showToast({ title: '昵称不能为空', icon: 'none' });
    }
    if (!avatarUrl) {
      return wx.showToast({ title: '请设置头像', icon: 'none' });
    }

    this.setData({ isSaving: true });

    try {
      let finalAvatarPath = avatarUrl;
      const isLocalFile = avatarUrl.includes('wxfile') || avatarUrl.includes('tmp') || avatarUrl.startsWith('http://tmp');

      if (isLocalFile) {
        // --- A. 上传新图片 ---
        console.log("正在上传新头像...");
        finalAvatarPath = await this.uploadImage(avatarUrl);
      } else {
        finalAvatarPath = avatarUrl.replace(BASE_URL, '');
      }

      console.log("提交给后端的头像路径:", finalAvatarPath);

      const currentUser = wx.getStorageSync('user');
      if (!currentUser || !currentUser.id) {
        throw { msg: '用户未登录，无法保存' };
      }
      await API.user.update({
        id: currentUser.id,  
        nickname: nickname,
        avatar: finalAvatarPath
      });

      // --- D. 更新本地缓存 ---
      // 这一步很重要，否则返回上一页看到的还是旧数据
      // 使用 ...currentUser 确保不丢失 id, openid 等其他信息
      const newUser = { ...currentUser, nickname, avatar: finalAvatarPath };
      wx.setStorageSync('user', newUser);

      wx.showToast({ title: '保存成功', icon: 'success' });

      // 1.5秒后返回上一页
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);

    } catch (err) {
      console.error("保存失败:", err);
      const msg = err.msg || (err.data && err.data.msg) || '保存失败，请重试';
      wx.showToast({ title: msg, icon: 'none' });
    } finally {
      this.setData({ isSaving: false });
    }
  },

  // 辅助方法：上传图片
  uploadImage(filePath) {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token');
      
      wx.uploadFile({
        url: `${BASE_URL}/api/upload`, 
        filePath: filePath,
        name: 'file', 
        header: {
          'Authorization': token ? 'Bearer ' + token : '' // 带上 Token
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data);
            if (data.code === 200) {
              resolve(data.data); 
            } else {
              reject(data.msg || '上传接口报错');
            }
          } catch (e) {
            reject('解析响应失败');
          }
        },
        fail: (err) => {
          reject('网络请求失败');
        }
      });
    });
  }
});