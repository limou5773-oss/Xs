<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="brand">
        <div class="logo-circle">M</div>
        <div class="brand-text">
          <h1>商家后台管理</h1>
          <p>Merchant Admin System</p>
        </div>
      </div>

      <el-form :model="form" class="login-form" @keyup.enter="onLogin">
        <el-form-item>
          <el-input
            v-model="form.username"
            placeholder="请输入账号"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-input
            v-model="form.password"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <div class="login-actions">
          <el-checkbox v-model="remember">记住我</el-checkbox>
        </div>

        <el-button
          type="primary"
          size="large"
          class="login-btn"
          :loading="loading"
          @click="onLogin"
        >
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginApi } from '@/api/auth'

const router = useRouter()

const form = ref({
  username: '',
  password: ''
})
const remember = ref(false)
const loading = ref(false)

// 自动回填账号
onMounted(() => {
  const savedUser = localStorage.getItem('lastUsername')
  if (savedUser) {
    form.value.username = savedUser
    remember.value = true
  }
})

const onLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    const res = await loginApi(form.value)

    if (res.code === 200) {
      const {
        accessToken,
        refreshToken,
        username,
        merchantUserId,
        merchantId
      } = res.data

      // ⭐⭐⭐ 必须保存，否则导致消息中心 receiverId=null
      localStorage.setItem("merchantUserId", merchantUserId)
      localStorage.setItem("merchantId", merchantId)

      // token 信息
      localStorage.setItem("accessToken", accessToken)
      localStorage.setItem("refreshToken", refreshToken)
      localStorage.setItem("username", username || form.value.username)

      // 记住账号
      if (remember.value) {
        localStorage.setItem("lastUsername", form.value.username)
      } else {
        localStorage.removeItem("lastUsername")
      }

      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }

  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #3b82f6 0, #111827 50%, #020617 100%);
}

.login-panel {
  width: 380px;
  padding: 30px 32px 26px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.96);
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.5);
  color: #e5e7eb;
}

.brand {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.logo-circle {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #22c55e);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-right: 12px;
}

.brand-text h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.brand-text p {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
}

.login-form {
  margin-top: 10px;
}

.login-actions {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 12px;
  font-size: 12px;
  color: #9ca3af;
}

.login-btn {
  width: 100%;
  border-radius: 999px;
}
</style>
