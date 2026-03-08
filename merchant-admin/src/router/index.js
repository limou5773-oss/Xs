// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 页面组件导入
import Login from '@/views/Login.vue'
import Layout from '@/layout/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'

// 商品管理页面
import ProductList from '@/views/product/ProductList.vue'
import ProductEdit from '@/views/product/ProductEdit.vue'

// 订单管理
import OrderList from '@/views/order/OrderList.vue'

// 售后管理页面
import AfterSaleList from '@/views/afterSale/AfterSaleList.vue'
import AfterSaleDetail from '@/views/afterSale/AfterSaleDetail.vue'

// 分类管理页面
import CategoryList from '@/views/category/CategoryList.vue'

// 消息中心
import MessageCenter from '@/views/message/MessageCenter.vue'
// 公告管理
import NoticeList from "@/views/notice/NoticeList.vue";
import NoticeEdit from "@/views/notice/NoticeEdit.vue";

// 👇 新增：评价管理页面导入
import ReviewList from "@/views/review/ReviewList.vue";

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard },

      { path: 'products', name: 'ProductList', component: ProductList },
      { path: 'product/edit/:id?', name: 'ProductEdit', component: ProductEdit },

      { path: 'orders', name: 'OrderList', component: OrderList },

      { path: 'after-sale', name: 'AfterSaleList', component: AfterSaleList },
      { path: 'after-sale/:id', name: 'AfterSaleDetail', component: AfterSaleDetail },

      { path: 'categories', name: 'CategoryList', component: CategoryList },

      // 👇 新增：评价管理路由
      { path: 'reviews', name: 'ReviewList', component: ReviewList },

      // 新增：消息中心
      { path: 'messages', name: 'MessageCenter', component: MessageCenter },
      // 公告管理
      { path: "notice", component: NoticeList },
      { path: "notice/edit/:id?", component: NoticeEdit }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')

  if (!token && to.path !== '/login') {
    return next('/login')
  }
  if (token && to.path === '/login') {
    return next('/')
  }
  next()
})

export default router