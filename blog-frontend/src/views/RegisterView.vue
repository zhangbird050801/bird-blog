<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchCaptcha, register, type RegisterRequest } from '@/api'
import LgInput from '@/components/base/LgInput.vue'
import LgButton from '@/components/base/LgButton.vue'
import LgToast from '@/components/base/LgToast.vue'

const router = useRouter()

// 表单数据
const formData = ref<RegisterRequest>({
  userName: '',
  email: '',
  password: '',
  code: '',
  uuid: '',
})

// 确认密码
const confirmPassword = ref('')

// 验证码相关
const captchaImg = ref('')
const loadingCaptcha = ref(false)

// 注册状态
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
    email: '',
    password: '',
    code: '',
    uuid: '',
  }
  confirmPassword.value = ''
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

// 提交注册
async function handleRegister() {
  // 表单验证
  if (!formData.value.userName.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }

  if (formData.value.userName.length < 3 || formData.value.userName.length > 20) {
    errorMsg.value = '用户名长度必须在3-20个字符之间'
    return
  }

  if (!formData.value.email.trim()) {
    errorMsg.value = '请输入邮箱'
    return
  }

  // 简单的邮箱格式验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    errorMsg.value = '邮箱格式不正确'
    return
  }

  if (!formData.value.password) {
    errorMsg.value = '请输入密码'
    return
  }

  if (formData.value.password.length < 6 || formData.value.password.length > 20) {
    errorMsg.value = '密码长度必须在6-20个字符之间'
    return
  }

  if (formData.value.password !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  if (!formData.value.code?.trim()) {
    errorMsg.value = '请输入验证码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const response = await register(formData.value)

    console.log('注册 API 响应:', response)

    // 显示成功提示
    toastMessage.value = '注册成功！请登录'
    toastType.value = 'success'
    showToast.value = true

    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error: any) {
    console.error('====== 注册失败调试信息 ======')
    console.error('错误对象:', error)
    console.error('错误消息:', error.message)
    console.error('错误类型:', typeof error)
    console.error('=============================')

    // 提取错误消息
    let message = '注册失败，请稍后重试'

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

    // 注册失败后刷新验证码
    loadCaptcha()
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
function goToLogin() {
  router.push('/login')
}

// 页面加载时获取验证码
onMounted(() => {
  loadCaptcha()
})
</script>

<template>
  <div class="register-page">
    <!-- 左侧图片面板 -->
    <div class="register-image-panel">
      <div class="image-content">
        <div class="brand-logo">
          <span class="brand-text">BirdBlog 博客系统</span>
        </div>
        <div class="auth-divider">
          <span class="divider-line"></span>
          <span class="divider-text">注册</span>
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
    <div class="register-form-panel">
      <!-- 标题 -->
      <div class="page-header">
        <h1 class="page-title">注册</h1>
        <p class="page-subtitle">填写信息，创建新账号</p>
      </div>

      <!-- 表单 -->
      <form class="register-form" @submit.prevent="handleRegister">
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
            placeholder="3-20个字符"
            autocomplete="username"
            :disabled="loading"
          />
        </div>

        <!-- 邮箱 -->
        <div class="form-group">
          <label for="email" class="form-label">邮箱</label>
          <LgInput
            id="email"
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱地址"
            autocomplete="email"
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
            placeholder="6-20个字符"
            autocomplete="new-password"
            :disabled="loading"
          />
        </div>

        <!-- 确认密码 -->
        <div class="form-group">
          <label for="confirm-password" class="form-label">确认密码</label>
          <LgInput
            id="confirm-password"
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            autocomplete="new-password"
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
          class="register-button"
        >
          {{ loading ? '注册中...' : '注册' }}
        </LgButton>

        <!-- 切换到登录 -->
        <div class="switch-mode">
          已有账号？
          <button type="button" class="switch-link" @click="goToLogin">
            立即登录
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
.register-page {
  min-height: 100vh;
  display: flex;
  background: #f8f9fa;
  padding: 60px 0 0;
}

body.dark .register-page {
  background: #0d1117;
}

/* 左侧图片面板 */
.register-image-panel {
  flex: 1.2;
  background: url('@/assets/wallhaven-yqxqv7.jpg') center center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.register-image-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(40, 42, 44, 0.2);
  z-index: 1;
}

body.dark .register-image-panel::before {
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
.register-form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px 80px;
  max-width: 600px;
  background: #fff;
}

body.dark .register-form-panel {
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
.register-form {
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

/* 注册按钮 */
.register-button {
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
  .register-page {
    flex-direction: column;
  }

  .register-image-panel {
    flex: none;
    min-height: 200px;
  }

  .register-form-panel {
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
  .register-form-panel {
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