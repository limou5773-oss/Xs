<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">商家后台</div>

      <el-menu
        :default-active="activeMenu"
        class="menu"
        router
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/products">
          <el-icon><ShoppingCart /></el-icon>
          <span>商品管理</span>
        </el-menu-item>

        <el-menu-item index="/categories">
          <el-icon><Collection /></el-icon>
          <span>分类管理</span>
        </el-menu-item>

        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>

        <el-menu-item index="/reviews">
          <el-icon><Star /></el-icon>
          <span>评价管理</span>
        </el-menu-item>

        <el-menu-item index="/after-sale">
          <el-icon><ChatSquare /></el-icon>
          <span>售后管理</span>
        </el-menu-item>

        <el-menu-item index="/messages">
          <el-icon><Bell /></el-icon>
          <span>消息中心</span>
        </el-menu-item>

        <el-menu-item index="/notice">
          <el-icon><BellFilled /></el-icon>
          <span>公告管理</span>
        </el-menu-item>

      </el-menu>
    </aside>


    <div class="main">
      <header class="header">
        <div class="title">商家后台管理系统</div>

        <div class="user">
          <el-badge
            :value="unreadCount"
            type="danger"
            :hidden="unreadCount === 0"
            class="badge"
          >
            <el-icon class="msg-icon" @click="goMessage">
              <BellFilled />
            </el-icon>
          </el-badge>

          <el-button type="text" @click="logout">退出登录</el-button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
// 👇 注意这里引入了 Star 图标
import {
  Monitor,
  ShoppingCart,
  Document,
  ChatSquare,
  Collection,
  Bell,
  BellFilled,
  Star 
} from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/message'

const route = useRoute()
const router = useRouter()
// 修复 activeMenu 的响应式问题，刷新页面或跳转后菜单高亮才能正确
const activeMenu = ref(route.path)

const unreadCount = ref(0)

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

const goMessage = () => {
  router.push('/messages')
}

const loadUnread = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    console.error('获取未读消息失败', e)
  }
}

onMounted(() => {
  loadUnread()
  setInterval(loadUnread, 30000)
})
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 210px;
  background-color: #001529;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  font-size: 18px;
  font-weight: bold;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #002140;
  letter-spacing: 2px;
}

.menu {
  flex: 1;
  border-right: none;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 0 20px;
}

.title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.msg-icon {
  font-size: 22px;
  cursor: pointer;
  color: #666;
}

.msg-icon:hover {
  color: #409eff;
}

.content {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.badge {
  margin-right: 10px;
}
</style>