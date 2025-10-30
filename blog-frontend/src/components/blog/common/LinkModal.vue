<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchLinks, type Link } from '@/api'

// 弹窗显示状态
const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

// 友链列表
const links = ref<Link[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

// 加载友链数据
async function loadLinks() {
  loading.value = true
  error.value = null
  try {
    links.value = await fetchLinks()
  } catch (err) {
    console.error('加载友链失败:', err)
    error.value = '加载友链失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 关闭弹窗
function handleClose() {
  emit('close')
}

// 打开友链
function openLink(url: string) {
  window.open(url, '_blank', 'noopener,noreferrer')
}

// 检查URL是否无效
function isInvalidUrl(url: string): boolean {
  return !url || url.includes('example.com') || url === ''
}

// 图片加载失败处理
function handleImageError(event: Event) {
  const img = event.target as HTMLImageElement
  const parent = img.parentElement
  if (parent) {
    // 创建备用图标
    img.style.display = 'none'
    const fallback = document.createElement('div')
    fallback.className = 'link-logo-fallback'
    fallback.innerHTML = `
      <svg viewBox="0 0 24 24" width="32" height="32" fill="currentColor">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
      </svg>
    `
    parent.appendChild(fallback)
  }
}

// 点击遮罩关闭
function handleMaskClick(event: MouseEvent) {
  if (event.target === event.currentTarget) {
    handleClose()
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (props.show) {
    loadLinks()
  }
})

// 监听 show 变化，重新加载数据
import { watch } from 'vue'
watch(() => props.show, (newVal) => {
  if (newVal && links.value.length === 0) {
    loadLinks()
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="show" class="link-modal-overlay" @click="handleMaskClick">
        <div class="link-modal">
          <div class="link-modal-header">
            <h2 class="link-modal-title">友情链接</h2>
            <button class="link-modal-close" @click="handleClose" aria-label="关闭">
              <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="link-modal-body">
            <!-- 加载状态 -->
            <div v-if="loading" class="link-modal-loading">
              <div class="loading-spinner"></div>
              <p>加载中...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="link-modal-error">
              <p>{{ error }}</p>
              <button class="retry-button" @click="loadLinks">重试</button>
            </div>

            <!-- 友链列表 -->
            <div v-else-if="links.length > 0" class="link-grid">
              <div
                v-for="link in links"
                :key="link.id"
                class="link-card"
                @click="openLink(link.url)"
              >
                <div class="link-logo">
                  <img 
                    v-if="!isInvalidUrl(link.logo)"
                    :src="link.logo" 
                    :alt="link.name"
                    @error="handleImageError"
                  />
                  <div v-else class="link-logo-fallback">
                    <svg viewBox="0 0 24 24" width="32" height="32" fill="currentColor">
                      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                    </svg>
                  </div>
                </div>
                <div class="link-info">
                  <h3 class="link-name">{{ link.name }}</h3>
                  <p class="link-description">{{ link.description }}</p>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="link-modal-empty">
              <p>暂无友链</p>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* 遮罩层 */
.link-modal-overlay {
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
  z-index: 9999;
  padding: 20px;
}

/* 弹窗主体 */
.link-modal {
  background: var(--sg-bg-primary);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 900px;
  width: 100%;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 弹窗头部 */
.link-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
  border-bottom: 1px solid var(--sg-border-lighter);
}

.link-modal-title {
  font-size: 24px;
  font-weight: 600;
  /* color: var(--sg-text-primary); */
  color: #10b981;
  margin: 0;
}

.link-modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--sg-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.link-modal-close:hover {
  background: var(--sg-bg-secondary);
  color: var(--sg-text-primary);
}

/* 弹窗主体内容 */
.link-modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 32px;
}

/* 友链网格 */
.link-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

/* 友链卡片 */
.link-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid var(--sg-border-lighter);
  background: var(--sg-bg-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.link-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: var(--sg-primary);
}

/* 友链Logo */
.link-logo {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--sg-border-lighter);
}

.link-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 4px;
}

/* 图片加载失败处理 */
.link-logo img[src=""],
.link-logo img:not([src]) {
  display: none;
}

/* 备用图标 */
.link-logo-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: rgba(255, 255, 255, 0.9);
}

.link-logo-fallback svg {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

/* 友链信息 */
.link-info {
  flex: 1;
  min-width: 0;
}

.link-name {
  font-size: 16px;
  font-weight: 600;
  color: #10b981;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.link-description {
  font-size: 14px;
  color: #34d399;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 加载状态 */
.link-modal-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--sg-text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--sg-border-lighter);
  border-top-color: var(--sg-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误状态 */
.link-modal-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--sg-text-secondary);
}

.link-modal-error p {
  margin: 0 0 16px 0;
}

.retry-button {
  padding: 8px 20px;
  border-radius: 8px;
  border: 1px solid var(--sg-primary);
  background: transparent;
  color: var(--sg-primary);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.retry-button:hover {
  background: var(--sg-primary);
  color: #fff;
}

/* 空状态 */
.link-modal-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--sg-text-secondary);
}

/* 弹窗动画 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-active .link-modal,
.modal-fade-leave-active .link-modal {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .link-modal {
  transform: scale(0.9);
}

.modal-fade-leave-to .link-modal {
  transform: scale(0.9);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .link-modal {
    max-width: 100%;
    max-height: 90vh;
    margin: 0;
    border-radius: 16px 16px 0 0;
  }

  .link-modal-header {
    padding: 20px;
  }

  .link-modal-title {
    font-size: 20px;
  }

  .link-modal-body {
    padding: 20px;
  }

  .link-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .link-card {
    padding: 16px;
  }

  .link-logo {
    width: 50px;
    height: 50px;
  }
}
</style>

