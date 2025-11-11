<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchLinks, type Link } from '@/api'
import LinkApplicationForm from '@/components/blog/link/LinkApplicationForm.vue'
import LinkComments from '@/components/blog/link/LinkComments.vue'

const links = ref<Link[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

// 消息提示
const message = ref<{
  type: 'success' | 'error'
  text: string
} | null>(null)

// 显示消息
function showMessage(type: 'success' | 'error', text: string) {
  message.value = { type, text }
  setTimeout(() => {
    message.value = null
  }, 5000)
}

// 友链申请成功处理
function handleApplicationSuccess() {
  showMessage('success', '友链申请提交成功！我们会尽快审核您的申请。')
}

// 友链申请错误处理
function handleApplicationError(errorText: string) {
  showMessage('error', errorText)
}

// 展开的评论区
const expandedComments = ref<number[]>([])

// 切换评论区展开状态
function toggleComments(linkId: number) {
  const index = expandedComments.value.indexOf(linkId)
  if (index > -1) {
    expandedComments.value.splice(index, 1)
  } else {
    expandedComments.value.push(linkId)
  }
}

// 检查评论区是否展开
function isCommentsExpanded(linkId: number): boolean {
  return expandedComments.value.includes(linkId)
}

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

// 组件挂载时加载数据
onMounted(() => {
  loadLinks()
})
</script>

<template>
  <main class="links-page">
    <div class="container">
      <!-- 页面头部 -->
      <header class="page-header">
        <h1 class="page-title">友情链接</h1>
        <p class="page-subtitle">发现更多有趣的博客和网站</p>
      </header>

      <!-- 友链内容区域 -->
      <div class="links-content">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button class="retry-button" @click="loadLinks">重试</button>
        </div>

        <!-- 友链列表 -->
        <div v-else-if="links.length > 0" class="link-list">
          <div
            v-for="link in links"
            :key="link.id"
            class="link-item"
          >
            <div
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

            <!-- 评论区切换按钮 -->
            <div class="link-comments-toggle">
              <button
                class="comments-toggle-button"
                @click="toggleComments(link.id)"
                :aria-expanded="isCommentsExpanded(link.id)"
              >
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                  <path d="M20 2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
                </svg>
                {{ isCommentsExpanded(link.id) ? '收起评论' : '查看评论' }}
              </button>
            </div>

            <!-- 评论区 -->
            <Transition name="comments-expand">
              <div v-if="isCommentsExpanded(link.id)" class="link-comments-section">
                <LinkComments :link-id="link.id" />
              </div>
            </Transition>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" width="64" height="64" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <h3>暂无友情链接</h3>
          <p>站长还没有添加任何友情链接</p>
        </div>
      </div>

      <!-- 友链申请区域 -->
      <div class="link-application">
        <h2>申请友链</h2>
        <p>欢迎优秀的博客和网站申请友情链接！</p>

        <!-- 友链申请表单 -->
        <LinkApplicationForm
          @success="handleApplicationSuccess"
          @error="handleApplicationError"
        />
      </div>

      <!-- 消息提示 -->
      <Transition name="message">
        <div v-if="message" class="message-toast" :class="message.type">
          <div class="message-content">
            <svg v-if="message.type === 'success'" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
            <span>{{ message.text }}</span>
          </div>
          <button class="message-close" @click="message = null" aria-label="关闭消息">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
      </Transition>
    </div>
  </main>
</template>

<style scoped>
/* 页面容器 */
.links-page {
  min-height: calc(100vh - 60px);
  margin-top: 60px;
  padding: 40px 20px;
  background: #f8fafc;
}

body.dark .links-page {
  background: #0f172a;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  color: #10b981;
  margin: 0 0 16px 0;
}

.page-subtitle {
  font-size: 18px;
  color: #64748b;
  margin: 0;
}

body.dark .page-subtitle {
  color: #94a3b8;
}

/* 友链内容区域 */
.links-content {
  margin-bottom: 80px;
}

/* 友链列表 */
.link-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 40px;
}

.link-item {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 友链卡片 */
.link-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

body.dark .link-card {
  background: #1e293b;
  border-color: #334155;
}

.link-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  border-color: #10b981;
}

/* 友链Logo */
.link-logo {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 16px;
  overflow: hidden;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

body.dark .link-logo {
  background: #1e293b;
  border-color: #334155;
}

.link-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 8px;
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
  font-size: 18px;
  font-weight: 600;
  color: #10b981;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.link-description {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

body.dark .link-description {
  color: #94a3b8;
}

/* 评论切换按钮 */
.link-comments-toggle {
  display: flex;
  justify-content: center;
}

.comments-toggle-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: 20px;
  color: #10b981;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.comments-toggle-button:hover {
  background: rgba(16, 185, 129, 0.2);
  border-color: #10b981;
}

.comments-toggle-button svg {
  flex-shrink: 0;
}

/* 评论区区域 */
.link-comments-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--sg-border-lighter);
}

/* 评论区展开动画 */
.comments-expand-enter-active,
.comments-expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.comments-expand-enter-from,
.comments-expand-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-20px);
}

.comments-expand-enter-to,
.comments-expand-leave-from {
  max-height: 1000px;
  opacity: 1;
  transform: translateY(0);
}

/* 状态样式 */
.loading-state,
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  color: #64748b;
}

body.dark .loading-state,
body.dark .error-state,
body.dark .empty-state {
  color: #94a3b8;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 20px;
}

body.dark .loading-spinner {
  border-color: #374151;
  border-top-color: #10b981;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state p {
  margin: 0 0 20px 0;
  font-size: 16px;
}

.retry-button {
  padding: 12px 24px;
  border-radius: 8px;
  border: 1px solid #10b981;
  background: transparent;
  color: #10b981;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.retry-button:hover {
  background: #10b981;
  color: #fff;
}

.empty-icon {
  margin-bottom: 20px;
  opacity: 0.3;
}

.empty-state h3 {
  font-size: 20px;
  color: #1e293b;
  margin: 0 0 8px 0;
}

body.dark .empty-state h3 {
  color: #f1f5f9;
}

.empty-state p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

body.dark .empty-state p {
  color: #94a3b8;
}

/* 友链申请区域 */
.link-application {
  margin-bottom: 40px;
}

.link-application h2 {
  font-size: 28px;
  color: #1e293b;
  margin: 0 0 12px 0;
}

body.dark .link-application h2 {
  color: #f1f5f9;
}

.link-application > p {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 24px 0;
}

body.dark .link-application > p {
  color: #94a3b8;
}

/* 消息提示样式 */
.message-toast {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 300px;
  max-width: 500px;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid transparent;
  transition: all 0.3s ease;
}

.message-toast.success {
  background: #f0f9ff;
  border-color: #10b981;
  color: #065f46;
}

.message-toast.error {
  background: #fef2f2;
  border-color: #ef4444;
  color: #991b1b;
}

.message-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.message-content svg {
  flex-shrink: 0;
}

.message-content span {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
}

.message-close {
  flex-shrink: 0;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.message-close:hover {
  opacity: 1;
}

/* 消息提示动画 */
.message-enter-active {
  transition: all 0.3s ease;
}

.message-leave-active {
  transition: all 0.3s ease;
}

.message-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.message-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .links-page {
    padding: 20px 16px;
  }

  .page-title {
    font-size: 32px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .link-list {
    gap: 20px;
  }

  .link-item {
    gap: 12px;
  }

  .link-card {
    padding: 20px;
  }

  .link-logo {
    width: 60px;
    height: 60px;
  }

  .link-name {
    font-size: 16px;
  }

  .link-application {
    padding: 24px;
  }

  .link-application h2 {
    font-size: 20px;
  }

  .message-toast {
    right: 16px;
    left: 16px;
    min-width: auto;
    max-width: none;
  }
}

@media (max-width: 480px) {
  .link-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .link-logo {
    width: 80px;
    height: 80px;
  }
}
</style>