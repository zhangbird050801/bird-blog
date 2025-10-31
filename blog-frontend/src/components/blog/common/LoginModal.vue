<script setup lang="ts">
import { ref, watch } from 'vue'
import { fetchCaptcha, login, type LoginRequest } from '@/api'
import { useAuth } from '@/composables/useAuth'
import LgInput from '@/components/base/LgInput.vue'
import LgButton from '@/components/base/LgButton.vue'
import LgToast from '@/components/base/LgToast.vue'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
  (e: 'success'): void
}>()

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

// 关闭弹窗
function closeModal() {
  emit('update:show', false)
  resetForm()
}

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
    
    // 保存登录信息（包含 token、refreshToken 和 userInfo）
    setAuth(response.token, response.refreshToken, response.userInfo)
    
    // 显示成功提示
    toastMessage.value = '登录成功！'
    toastType.value = 'success'
    showToast.value = true
    
    // 触发成功事件
    emit('success')
    
    // 关闭弹窗
    closeModal()
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
    
    // 登录失败后刷新验证码
    loadCaptcha()
  } finally {
    loading.value = false
  }
}

// 弹窗显示时加载验证码
watch(() => props.show, (newVal) => {
  if (newVal) {
    loadCaptcha()
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="show" class="modal-overlay" @click.self="closeModal">
        <div class="modal-container">
          <!-- 关闭按钮 -->
          <button class="modal-close" @click="closeModal" aria-label="关闭">
            <svg viewBox="0 0 24 24" width="24" height="24">
              <path
                fill="currentColor"
                d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
              />
            </svg>
          </button>

          <!-- 标题 -->
          <div class="modal-header">
            <h2 class="modal-title">登录</h2>
            <p class="modal-subtitle">欢迎回来</p>
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
          </form>
        </div>
      </div>
    </Transition>
    
    <!-- Toast 通知 -->
    <LgToast
      v-model:show="showToast"
      :message="toastMessage"
      :type="toastType"
      :duration="3000"
    />
  </Teleport>
</template>

<style scoped>
/* 弹窗遮罩 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

/* 弹窗容器 */
.modal-container {
  position: relative;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 420px;
  padding: 32px;
  animation: slideUp 0.3s ease;
}

body.dark .modal-container {
  background: #1a1a2e;
  color: #fff;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 关闭按钮 */
.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

body.dark .modal-close {
  background: rgba(255, 255, 255, 0.05);
  color: #ccc;
}

body.dark .modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* 标题区 */
.modal-header {
  margin-bottom: 32px;
  text-align: center;
}

.modal-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

body.dark .modal-title {
  background: linear-gradient(135deg, #7aa2ff 0%, #50ccd5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

body.dark .modal-subtitle {
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

/* 过渡动画 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: translateY(30px);
}

/* 移动端适配 */
@media (max-width: 480px) {
  .modal-container {
    padding: 24px;
  }

  .modal-title {
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
