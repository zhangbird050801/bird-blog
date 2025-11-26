<script setup lang="ts">
import { ref, reactive } from 'vue'
import { changePassword, type ChangePasswordRequest } from '@/api/auth'
import LgInput from '@/components/base/LgInput.vue'
import LgButton from '@/components/base/LgButton.vue'
import LgToast from '@/components/base/LgToast.vue'

interface Props {
  show: boolean
}

interface Emits {
  (e: 'update:show', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 表单数据
const formData = reactive<ChangePasswordRequest>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载状态
const loading = ref(false)

// Toast 通知
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref<'success' | 'error'>('success')

// 验证错误
const errors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  general: ''
})

// 清除错误
function clearErrors() {
  errors.oldPassword = ''
  errors.newPassword = ''
  errors.confirmPassword = ''
  errors.general = ''
}

// 验证表单
function validateForm(): boolean {
  clearErrors()

  let isValid = true

  // 验证旧密码
  if (!formData.oldPassword.trim()) {
    errors.oldPassword = '请输入旧密码'
    isValid = false
  }

  // 验证新密码
  if (!formData.newPassword.trim()) {
    errors.newPassword = '请输入新密码'
    isValid = false
  } else if (formData.newPassword.length < 6) {
    errors.newPassword = '密码不能少于6位'
    isValid = false
  }

  // 验证确认密码
  if (!formData.confirmPassword.trim()) {
    errors.confirmPassword = '请重复输入新密码'
    isValid = false
  } else if (formData.newPassword !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }

  return isValid
}

// 重置表单
function resetForm() {
  formData.oldPassword = ''
  formData.newPassword = ''
  formData.confirmPassword = ''
  clearErrors()
}

// 提交表单
async function handleSubmit() {
  if (!validateForm()) {
    return
  }

  loading.value = true
  try {
    const result = await changePassword(formData)

    // 显示成功 Toast 提示（和登录页一致的小提示框）
    toastMessage.value = '密码修改成功！'
    toastType.value = 'success'
    showToast.value = true

    emit('success')
    closeModal()
  } catch (error: any) {
    console.error('修改密码失败:', error)

    // 根据错误信息显示不同的错误
    const errorMessage = error.message || '修改密码失败，请稍后重试'

    if (errorMessage.includes('旧密码错误')) {
      errors.oldPassword = '旧密码错误'
    } else if (errorMessage.includes('两次输入的密码不一致')) {
      errors.confirmPassword = '两次输入的密码不一致'
    } else if (errorMessage.includes('密码长度不能少于')) {
      errors.newPassword = errorMessage
    } else if (errorMessage.includes('新密码不能与旧密码相同')) {
      errors.newPassword = '新密码不能与旧密码相同'
    } else {
      errors.general = errorMessage
    }
  } finally {
    loading.value = false
  }
}

// 关闭弹窗
function closeModal() {
  emit('update:show', false)
  resetForm()
}

// 点击背景关闭
function handleBackdropClick(event: MouseEvent) {
  if (event.target === event.currentTarget) {
    closeModal()
  }
}
</script>

<template>
  <teleport to="body">
    <!-- 修改密码弹窗 -->
    <div
      v-if="show"
      class="modal-backdrop"
      @click="handleBackdropClick"
    >
      <div class="change-password-modal" @click.stop>
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-btn" @click="closeModal">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path
                fill="currentColor"
                d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
              />
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- 通用错误提示 -->
          <div v-if="errors.general" class="error-message general-error">
            {{ errors.general }}
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- 旧密码 -->
            <div class="form-group">
              <label for="old-password">旧密码</label>
              <LgInput
                id="old-password"
                v-model="formData.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                :error="!!errors.oldPassword"
              />
              <div v-if="errors.oldPassword" class="error-message">
                {{ errors.oldPassword }}
              </div>
            </div>

            <!-- 新密码 -->
            <div class="form-group">
              <label for="new-password">新密码</label>
              <LgInput
                id="new-password"
                v-model="formData.newPassword"
                type="password"
                placeholder="请输入新密码（不少于6位）"
                :error="!!errors.newPassword"
              />
              <div v-if="errors.newPassword" class="error-message">
                {{ errors.newPassword }}
              </div>
            </div>

            <!-- 确认新密码 -->
            <div class="form-group">
              <label for="confirm-password">重复输一遍新密码</label>
              <LgInput
                id="confirm-password"
                v-model="formData.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                :error="!!errors.confirmPassword"
              />
              <div v-if="errors.confirmPassword" class="error-message">
                {{ errors.confirmPassword }}
              </div>
            </div>
          </form>
        </div>

        <div class="modal-footer">
          <LgButton variant="secondary" @click="closeModal">
            取消
          </LgButton>
          <LgButton @click="handleSubmit" :loading="loading">
            确认修改
          </LgButton>
        </div>
      </div>
    </div>

    <!-- 成功提示 Toast（独立于弹窗显示控制） -->
    <LgToast
      v-model:show="showToast"
      :message="toastMessage"
      :type="toastType"
      :duration="3000"
    />
  </teleport>
</template>

<style scoped>
/* 背景遮罩 */
.modal-backdrop {
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
  padding: 16px;
}

/* 弹窗主体 */
.change-password-modal {
  background: rgba(40, 42, 44, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1);
  width: 100%;
  max-width: 420px;
  max-height: 90vh;
  overflow: hidden;
  animation: modalSlideIn 0.3s ease;
}

body.dark .change-password-modal {
  background: rgba(26, 26, 46, 0.95);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

/* 弹窗内容 */
.modal-body {
  padding: 24px;
}

/* 通用错误提示 */
.general-error {
  margin-bottom: 20px;
  padding: 12px 16px;
  background: rgba(231, 76, 60, 0.15);
  border: 1px solid rgba(231, 76, 60, 0.3);
  border-radius: 8px;
  color: #ff6b6b;
  font-size: 14px;
}

/* 表单组 */
.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.form-group :deep(.lg-input__wrapper) {
  width: 100%;
}

/* 错误信息 */
.error-message {
  margin-top: 6px;
  font-size: 13px;
  color: #ff6b6b;
  line-height: 1.4;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-footer :deep(.lg-button) {
  min-width: 80px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .modal-backdrop {
    padding: 12px;
  }

  .change-password-modal {
    max-width: 100%;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }

  .modal-footer {
    flex-direction: column;
    gap: 8px;
  }

  .modal-footer :deep(.lg-button) {
    width: 100%;
  }
}
</style>