<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchCaptcha, login, type LoginRequest } from '@/api'
import { useAuth } from '@/composables/useAuth'
import LgInput from '@/components/base/LgInput.vue'
import LgButton from '@/components/base/LgButton.vue'
import LgToast from '@/components/base/LgToast.vue'

const router = useRouter()
const { setAuth } = useAuth()

// 表单数据
const formData = ref<LoginRequest>({
  userName: '',
  password: '',
  code: '',
  uuid: '',
})

// 验证码相关
const captchaImg = ref('')
const loadingCaptcha = ref(false)

// 登录状态
const loading = ref(false)
const errorMsg = ref('')

// Toast 通知状态
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref<'success' | 'error'>('success')

// 重置表单
function resetForm() {
  formData.value = {
    userName: '',
    password: '',
    code: '',
    uuid: '',
  }
  errorMsg.value = ''
}

// 加载验证码
async function loadCaptcha() {
  loadingCaptcha.value = true
  errorMsg.value = ''

  try {
    const data = await fetchCaptcha()
    captchaImg.value = data.img
    formData.value.uuid = data.uuid
  } catch (error) {
    errorMsg.value = '获取验证码失败,请重试'
    console.error('获取验证码失败:', error)
  } finally {
    loadingCaptcha.value = false
  }
}

// 提交登录
async function handleLogin() {
  // 表单验证
  if (!formData.value.userName.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }

  if (!formData.value.password) {
    errorMsg.value = '请输入密码'
    return
  }

  if (!formData.value.code?.trim()) {
    errorMsg.value = '请输入验证码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const response = await login(formData.value)

    console.log('登录 API 响应:', response)
    console.log('response.token:', response.token)
    console.log('response.refreshToken:', response.refreshToken)
    console.log('response.userInfo:', response.userInfo)

    // 检查响应结构
    if (!response.token) {
      console.error('登录响应中没有token字段')
      throw new Error('登录响应格式错误：缺少token')
    }

    // 保存登录信息（包含 token、refreshToken 和 userInfo）
    setAuth(response.token, response.refreshToken, response.userInfo)

    // 显示成功提示
    toastMessage.value = '登录成功！'
    toastType.value = 'success'
    showToast.value = true

    // 跳转到首页
    setTimeout(() => {
      router.push('/')
    }, 1000)
  } catch (error: any) {
    console.error('====== 登录失败调试信息 ======')
    console.error('错误对象:', error)
    console.error('错误消息:', error.message)
    console.error('错误类型:', typeof error)
    console.error('=============================')

    // 提取错误消息
    let message = '登录失败，请检查用户名和密码'

    if (error.message) {
      // 从错误对象中提取消息
      message = error.message

      // 如果消息包含 "API 错误" 前缀，提取实际错误信息
      const match = error.message.match(/API 错误 \[\d+\]: (.+)/)
      if (match) {
        message = match[1]
        console.log('提取到的错误消息:', message)
      }
    }

    console.log('最终显示的错误消息:', message)
    errorMsg.value = message

    // 使用 Toast 显示错误消息
    toastMessage.value = message
    toastType.value = 'error'
    showToast.value = true

    // 登录失败后刷新验证码
    loadCaptcha()
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
function goToRegister() {
  router.push('/register')
}

// 页面加载时获取验证码
onMounted(() => {
  loadCaptcha()
})
</script>

<template>
  <div class="login-page">
    <!-- 左侧图片面板 -->
    <div class="login-image-panel">
      <div class="image-content">
        <div class="brand-logo">
          <span class="brand-text">BirdBlog 博客系统</span>
        </div>
        <div class="auth-divider">
          <span class="divider-line"></span>
          <span class="divider-text">登录</span>
          <span class="divider-line"></span>
        </div>
        <div class="image-decoration">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区域 -->
    <div class="login-form-panel">
      <!-- 标题 -->
      <div class="page-header">
        <h1 class="page-title">登录</h1>
        <p class="page-subtitle">请输入您的账号信息</p>
      </div>

      <!-- 表单 -->
      <form class="login-form" @submit.prevent="handleLogin">
        <!-- 错误提示 -->
        <div v-if="errorMsg" class="error-message">
          {{ errorMsg }}
        </div>

        <!-- 用户名 -->
        <div class="form-group">
          <label for="username" class="form-label">用户名</label>
          <LgInput
            id="username"
            v-model="formData.userName"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
            :disabled="loading"
          />
        </div>

        <!-- 密码 -->
        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <LgInput
            id="password"
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            :disabled="loading"
          />
        </div>

        <!-- 验证码 -->
        <div class="form-group">
          <label for="captcha" class="form-label">验证码</label>
          <div class="captcha-group">
            <LgInput
              id="captcha"
              v-model="formData.code"
              type="text"
              placeholder="请输入验证码"
              autocomplete="off"
              :disabled="loading"
              maxlength="4"
            />
            <div class="captcha-image" @click="loadCaptcha">
              <img
                v-if="captchaImg && !loadingCaptcha"
                :src="captchaImg"
                alt="验证码"
              />
              <div v-else class="captcha-loading">
                {{ loadingCaptcha ? '加载中...' : '点击刷新' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <LgButton
          type="submit"
          variant="primary"
          :disabled="loading"
          class="login-button"
        >
          {{ loading ? '登录中...' : '登录' }}
        </LgButton>

        <!-- 切换到注册 -->
        <div class="switch-mode">
          还没有账号？
          <button type="button" class="switch-link" @click="goToRegister">
            立即注册
          </button>
        </div>
      </form>
    </div>

    <!-- Toast 通知 -->
    <LgToast
      v-model:show="showToast"
      :message="toastMessage"
      :type="toastType"
      :duration="3000"
    />
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  background: #f8f9fa;
  padding: 60px 0 0;
}

body.dark .login-page {
  background: #0d1117;
}

/* 左侧图片面板 */
.login-image-panel {
  flex: 1.2;
  background: url('@/assets/wallhaven-yqxqv7.jpg') center center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-image-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(40, 42, 44, 0.2);
  z-index: 1;
}

body.dark .login-image-panel::before {
  background: rgba(26, 26, 46, 0.85);
}

.image-content {
  text-align: center;
  color: #fff;
  z-index: 2;
  padding: 40px;
}

/* 品牌标识 */
.brand-logo {
  margin-bottom: 40px;
}

.brand-text {
  font-size: 64px;
  font-weight: 800;
  letter-spacing: -2px;
  background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 认证分割线 */
.auth-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  gap: 20px;
}

.divider-line {
  flex: 1;
  height: 2px;
  background: rgba(255, 255, 255, 0.5);
  max-width: 100px;
}

.divider-text {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  padding: 0 10px;
  text-transform: uppercase;
  letter-spacing: 2px;
}

/* 装饰圆圈 */
.image-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: -50px;
  left: -50px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 50px;
  right: 50px;
  background: rgba(255, 255, 255, 0.08);
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 100px;
  right: -30px;
  background: rgba(255, 255, 255, 0.06);
}

/* 右侧表单面板 */
.login-form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px 80px;
  max-width: 600px;
  background: #fff;
}

body.dark .login-form-panel {
  background: #1a1a2e;
  color: #fff;
}


/* 页面标题 */
.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

body.dark .page-title {
  background: linear-gradient(135deg, #7aa2ff 0%, #50ccd5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

body.dark .page-subtitle {
  color: #999;
}

/* 表单 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

body.dark .form-label {
  color: #ddd;
}

/* 错误提示 */
.error-message {
  padding: 14px 16px;
  margin-bottom: 16px;
  background: #fee;
  border-left: 4px solid #e74c3c;
  border-radius: 6px;
  color: #c0392b;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  animation: shake 0.3s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-8px); }
  75% { transform: translateX(8px); }
}

body.dark .error-message {
  background: rgba(231, 76, 60, 0.15);
  border-left-color: #ff6b6b;
  color: #ff8787;
}

/* 验证码组 */
.captcha-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.captcha-group :deep(.lg-input__wrapper) {
  flex: 1;
}

.captcha-image {
  width: 130px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e0e0;
  transition: all 0.2s ease;
}

.captcha-image:hover {
  border-color: var(--sg-primary);
}

body.dark .captcha-image {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-loading {
  font-size: 12px;
  color: #999;
  text-align: center;
}

/* 登录按钮 */
.login-button {
  margin-top: 8px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

/* 切换模式 */
.switch-mode {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

body.dark .switch-mode {
  color: #999;
}

.switch-link {
  background: none;
  border: none;
  color: var(--sg-primary);
  cursor: pointer;
  font-weight: 600;
  padding: 0;
  text-decoration: underline;
  transition: opacity 0.2s ease;
}

.switch-link:hover {
  opacity: 0.8;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }

  .login-image-panel {
    flex: none;
    min-height: 200px;
  }

  .login-form-panel {
    padding: 40px 24px;
  }

  .brand-text {
    font-size: 36px;
  }

  .divider-text {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .login-form-panel {
    padding: 32px 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .captcha-group {
    flex-direction: column;
  }

  .captcha-image {
    width: 100%;
  }
}
</style>