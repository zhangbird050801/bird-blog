<script setup lang="ts">
import { ref, reactive } from 'vue'
import { applyForLink, type LinkApplicationRequest } from '@/api'

const emit = defineEmits<{
  (e: 'success'): void
  (e: 'error', error: string): void
}>()

// 表单数据
const formData = reactive<LinkApplicationRequest>({
  name: '',
  logo: '',
  description: '',
  url: '',
  contactEmail: '',
  contactName: '',
  message: ''
})

// 表单状态
const submitting = ref(false)
const showForm = ref(false)

// 表单验证规则
const rules = {
  name: { required: true, message: '请输入网站名称' },
  url: { required: true, message: '请输入网站地址' },
  description: { required: true, message: '请输入网站描述' },
  contactEmail: { required: true, message: '请输入联系邮箱' },
  contactName: { required: true, message: '请输入联系人姓名' }
}

// 验证表单
function validateForm(): string[] {
  const errors: string[] = []

  if (!formData.name.trim()) {
    errors.push(rules.name.message)
  }

  if (!formData.url.trim()) {
    errors.push(rules.url.message)
  } else if (!isValidUrl(formData.url)) {
    errors.push('请输入有效的网站地址')
  }

  if (!formData.description.trim()) {
    errors.push(rules.description.message)
  }

  if (!formData.contactEmail.trim()) {
    errors.push(rules.contactEmail.message)
  } else if (!isValidEmail(formData.contactEmail)) {
    errors.push('请输入有效的邮箱地址')
  }

  if (!formData.contactName.trim()) {
    errors.push(rules.contactName.message)
  }

  // Logo URL 可选，但如果填写了就要验证
  if (formData.logo && !isValidUrl(formData.logo)) {
    errors.push('请输入有效的Logo地址')
  }

  return errors
}

// 验证URL格式
function isValidUrl(url: string): boolean {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

// 验证邮箱格式
function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 提交表单
async function handleSubmit() {
  const errors = validateForm()
  if (errors.length > 0) {
    emit('error', errors[0])
    return
  }

  submitting.value = true

  try {
    await applyForLink(formData)
    emit('success')
    resetForm()
  } catch (error: any) {
    console.error('申请友链失败:', error)
    const errorMessage = error.message || '申请失败，请稍后重试'
    emit('error', errorMessage)
  } finally {
    submitting.value = false
  }
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    name: '',
    logo: '',
    description: '',
    url: '',
    contactEmail: '',
    contactName: '',
    message: ''
  })
}

// 切换表单显示状态
function toggleForm() {
  showForm.value = !showForm.value
  if (!showForm.value) {
    resetForm()
  }
}
</script>

<template>
  <div class="link-application-form">
    <div class="form-header">
      <h3>申请友情链接</h3>
      <button
        class="toggle-button"
        @click="toggleForm"
        :aria-expanded="showForm"
      >
        {{ showForm ? '收起' : '展开申请表单' }}
        <svg
          class="toggle-icon"
          :class="{ 'toggle-icon--open': showForm }"
          viewBox="0 0 20 20"
          fill="currentColor"
          width="20"
          height="20"
        >
          <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
        </svg>
      </button>
    </div>

    <Transition name="form-expand">
      <div v-if="showForm" class="form-content">
        <form @submit.prevent="handleSubmit" class="application-form">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">网站名称 *</label>
              <input
                v-model="formData.name"
                type="text"
                class="form-input"
                placeholder="请输入网站名称"
                required
                :disabled="submitting"
              />
            </div>
            <div class="form-group">
              <label class="form-label">网站地址 *</label>
              <input
                v-model="formData.url"
                type="url"
                class="form-input"
                placeholder="https://example.com"
                required
                :disabled="submitting"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Logo地址</label>
              <input
                v-model="formData.logo"
                type="url"
                class="form-input"
                placeholder="https://example.com/logo.png"
                :disabled="submitting"
              />
            </div>
            <div class="form-group">
              <label class="form-label">联系邮箱 *</label>
              <input
                v-model="formData.contactEmail"
                type="email"
                class="form-input"
                placeholder="your@email.com"
                required
                :disabled="submitting"
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">联系人姓名 *</label>
            <input
              v-model="formData.contactName"
              type="text"
              class="form-input"
              placeholder="请输入您的姓名"
              required
              :disabled="submitting"
            />
          </div>

          <div class="form-group">
            <label class="form-label">网站描述 *</label>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              placeholder="请简要描述您的网站内容和特色..."
              rows="3"
              required
              :disabled="submitting"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">留言信息</label>
            <textarea
              v-model="formData.message"
              class="form-textarea"
              placeholder="有什么想对站长说的话吗？（选填）"
              rows="2"
              :disabled="submitting"
            ></textarea>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="form-button form-button--secondary"
              @click="resetForm"
              :disabled="submitting"
            >
              重置
            </button>
            <button
              type="submit"
              class="form-button form-button--primary"
              :disabled="submitting"
            >
              {{ submitting ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </form>

        <div class="form-tips">
          <h4>温馨提示：</h4>
          <ul>
            <li>申请后我们会尽快审核，通常在1-3个工作日内完成</li>
            <li>审核通过后，您的网站将出现在友情链接列表中</li>
            <li>请确保网站可以正常访问，内容健康向上</li>
            <li>建议先添加本站友情链接，以提高审核通过率</li>
          </ul>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
/* 表单容器 */
.link-application-form {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

body.dark .link-application-form {
  background: #1e293b;
  border-color: #334155;
}

/* 表单头部 */
.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

body.dark .form-header {
  background: #0f172a;
  border-bottom-color: #334155;
}

.form-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

body.dark .form-header h3 {
  color: #f1f5f9;
}

.toggle-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--sg-bg-primary);
  border: 1px solid var(--sg-border-lighter);
  border-radius: 8px;
  color: var(--sg-text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.toggle-button:hover {
  border-color: #10b981;
  color: #10b981;
}

.toggle-icon {
  transition: transform 0.3s ease;
}

.toggle-icon--open {
  transform: rotate(180deg);
}

/* 表单内容 */
.form-content {
  padding: 24px;
}

/* 表单展开动画 */
.form-expand-enter-active,
.form-expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.form-expand-enter-from,
.form-expand-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px);
}

.form-expand-enter-to,
.form-expand-leave-from {
  max-height: 800px;
  opacity: 1;
  transform: translateY(0);
}

/* 表单样式 */
.application-form {
  margin-bottom: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

body.dark .form-label {
  color: #d1d5db;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #ffffff;
  color: #111827;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.2s ease;
}

body.dark .form-input,
body.dark .form-textarea {
  background: #1f2937;
  border-color: #4b5563;
  color: #f9fafb;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.form-input:disabled,
.form-textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #9ca3af;
}

body.dark .form-input::placeholder,
body.dark .form-textarea::placeholder {
  color: #6b7280;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--sg-border-lighter);
}

.form-button {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.form-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-button--secondary {
  background: transparent;
  border: 1px solid var(--sg-border-lighter);
  color: var(--sg-text-secondary);
}

.form-button--secondary:hover:not(:disabled) {
  border-color: #10b981;
  color: #10b981;
}

.form-button--primary {
  background: #10b981;
  border: 1px solid #10b981;
  color: #fff;
}

.form-button--primary:hover:not(:disabled) {
  background: #059669;
  border-color: #059669;
}

/* 提示信息 */
.form-tips {
  background: rgba(16, 185, 129, 0.05);
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: 8px;
  padding: 16px 20px;
}

.form-tips h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #10b981;
}

.form-tips ul {
  margin: 0;
  padding-left: 16px;
}

.form-tips li {
  font-size: 13px;
  color: var(--sg-text-secondary);
  line-height: 1.6;
  margin-bottom: 6px;
}

.form-tips li:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-content {
    padding: 20px 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-button {
    width: 100%;
  }
}
</style>