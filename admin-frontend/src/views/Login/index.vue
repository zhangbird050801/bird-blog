<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>BirdBlog 管理后台</h2>
        </div>
      </template>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="userName">
          <el-input
            v-model="loginForm.userName"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="code">
          <div class="captcha-wrapper">
            <el-input
              v-model="loginForm.code"
              placeholder="请输入验证码"
              prefix-icon="Key"
              size="large"
              style="flex: 1"
            />
            <img
              v-if="captchaImage"
              :src="captchaImage"
              class="captcha-image"
              alt="验证码"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'
import type { LoginRequest } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用
const loginFormRef = ref<FormInstance>()

// 登录表单数据
const loginForm = ref<LoginRequest>({
  userName: '',
  password: '',
  code: '',
  uuid: ''
})

// 验证码图片
const captchaImage = ref<string>('')

// 加载状态
const loading = ref<boolean>(false)

// 表单验证规则
const rules: FormRules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

/**
 * 获取验证码
 */
async function getCaptcha() {
  try {
    const data = await authApi.getCaptcha()
    // 后端返回的 img 已经包含 data:image/png;base64, 前缀，直接使用
    captchaImage.value = data.img
    loginForm.value.uuid = data.uuid
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败')
  }
}

/**
 * 刷新验证码
 */
function refreshCaptcha() {
  loginForm.value.code = ''
  getCaptcha()
}

/**
 * 处理登录
 */
async function handleLogin() {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true

    try {
      await userStore.login(loginForm.value)
      ElMessage.success('登录成功')

      // 跳转到重定向页面或首页
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } catch (error: any) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败,请检查用户名和密码')
      // 登录失败后刷新验证码
      refreshCaptcha()
    } finally {
      loading.value = false
    }
  })
}

// 组件挂载时获取验证码
onMounted(() => {
  getCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 25%, #dfe6ed 50%, #d5dce4 75%, #cbd3dc 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 50%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-card {
  width: 450px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  position: relative;
  z-index: 1;
}

:deep(.el-card__header) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 30px 20px;
}

:deep(.el-card__body) {
  background: transparent;
  padding: 30px 40px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.login-form {
  margin-top: 20px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 12px rgba(44, 62, 80, 0.1);
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-image {
  height: 40px;
  width: 120px;
  cursor: pointer;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
}

.captcha-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(44, 62, 80, 0.15);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #5a6c7d 0%, #3d4f5f 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(58, 76, 95, 0.25);
  font-weight: 500;
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #4a5c6d 0%, #2d3f4f 100%);
  box-shadow: 0 6px 16px rgba(58, 76, 95, 0.35);
  transform: translateY(-2px);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

</style>
