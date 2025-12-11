<template>
  <div class="user-profile">
    <div class="container">
      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img 
                :src="userInfo?.avatar || '/default-avatar.png'" 
                :alt="userInfo?.nickName"
                class="avatar"
              >
              <button @click="openAvatarUpload" class="avatar-edit-btn">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                </svg>
              </button>
            </div>
          </div>
          
          <div class="user-info">
            <h1 class="username">{{ userInfo?.nickName || userInfo?.username }}</h1>
            <p class="join-date">加入时间：{{ formatDate(userInfo?.createTime) }}</p>
            
            <div class="stats">
              <div class="stat-item">
                <span class="stat-number">{{ userInfo?.favoriteCount || 0 }}</span>
                <span class="stat-label">收藏</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo?.likeCount || 0 }}</span>
                <span class="stat-label">点赞</span>
              </div>
            </div>
          </div>
        </div>

        <div class="profile-actions">
          <button @click="startEdit" class="btn-edit">编辑资料</button>
        </div>
      </div>

      <!-- 标签页 -->
      <div class="tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.key"
          @click="activeTab = tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 内容区域 -->
      <div class="tab-content">
        <!-- 收藏的文章 -->
        <div v-if="activeTab === 'favorites'" class="article-list">
          <h2>我的收藏</h2>
          <div v-if="favoriteArticles.length === 0 && !loadingFavorites" class="empty-state">
            <p>还没有收藏任何文章</p>
          </div>
          <div v-else>
            <article-card 
              v-for="article in favoriteArticles" 
              :key="article.id" 
              :article="article"
            />
            <div v-if="loadingFavorites" class="loading">加载中...</div>
          </div>
        </div>

        <!-- 点赞的文章 -->
        <div v-if="activeTab === 'likes'" class="article-list">
          <h2>我的点赞</h2>
          <div v-if="likedArticles.length === 0 && !loadingLikes" class="empty-state">
            <p>还没有点赞任何文章</p>
          </div>
          <div v-else>
            <article-card 
              v-for="article in likedArticles" 
              :key="article.id" 
              :article="article"
            />
            <div v-if="loadingLikes" class="loading">加载中...</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
          <div class="modal-content" @click.stop>
            <div class="modal-header">
              <h3>编辑个人资料</h3>
              <button class="modal-close" @click="showEditModal = false" aria-label="关闭">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
              </button>
            </div>
            
            <form @submit.prevent="handleUpdateProfile" class="modal-body">
              <div class="form-group">
                <label>昵称</label>
                <input 
                  v-model="editForm.nickName" 
                  type="text" 
                  placeholder="请输入昵称"
                  required
                >
              </div>
              
              <div class="form-group">
                <label>邮箱</label>
                <input 
                  v-model="editForm.email" 
                  type="email" 
                  placeholder="请输入邮箱"
                >
              </div>
              
              <div class="form-group">
                <label>手机号</label>
                <input 
                  v-model="editForm.phone" 
                  type="tel" 
                  placeholder="请输入手机号"
                >
              </div>
              
              <div class="form-group">
                <label>性别</label>
                <select v-model="editForm.sex">
                  <option :value="0">男</option>
                  <option :value="1">女</option>
                  <option :value="2">保密</option>
                </select>
              </div>
              
              <div class="modal-footer">
                <button type="button" @click="cancelEdit" class="btn-secondary">取消</button>
                <button type="submit" :disabled="updating" class="btn-primary">
                  {{ updating ? '保存中...' : '保存' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 头像上传弹窗 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showAvatarModal" class="modal-overlay" @click="showAvatarModal = false">
          <div class="modal-content modal-small" @click.stop>
            <div class="modal-header">
              <h3>上传头像</h3>
              <button class="modal-close" @click="showAvatarModal = false" aria-label="关闭">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
              </button>
            </div>
            
            <div class="modal-body">
              <input 
                ref="avatarInput"
                type="file" 
                accept="image/*" 
                @change="handleAvatarUpload"
                style="display: none"
              >
              <div class="upload-area" @click="($refs.avatarInput as HTMLInputElement)?.click()">
                <svg viewBox="0 0 24 24" width="48" height="48" fill="currentColor">
                  <path d="M19 7v2.99s-1.99.01-2 0V7h-3s.01-1.99 0-2h3V2h2v3h3v2h-3zm-3 4V8h-3V5H5c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2v-8h-3zM5 19l3-4 2 3 3-4 4 5H5z"/>
                </svg>
                <p>点击选择图片</p>
                <span class="upload-hint">支持 JPG、PNG 格式，最大 10MB</span>
              </div>
              
              <div class="modal-footer">
                <button type="button" @click="showAvatarModal = false" class="btn-secondary" :disabled="uploading">
                  取消
                </button>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Toast 消息提示 -->
    <LgToast
      v-model:show="showToast"
      :message="toastMessage"
      :type="toastType"
      :duration="3000"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { userApi, type UserProfile, type UpdateUserInfoRequest } from '@/api/user'
import type { Article } from '@/api/types'
import ArticleCard from '@/components/ArticleCard.vue'
import LgToast from '@/components/base/LgToast.vue'
import { useAuth } from '@/composables/useAuth'

// 响应式数据
const { userInfo, setUserInfo } = useAuth()
const showEditModal = ref(false)
const showAvatarModal = ref(false)
const updating = ref(false)
const uploading = ref(false)
const activeTab = ref('favorites')
const favoriteArticles = ref<Article[]>([])
const likedArticles = ref<Article[]>([])
const loadingFavorites = ref(false)
const loadingLikes = ref(false)

// Toast 状态
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref<'success' | 'error'>('success')

// 编辑表单
const editForm = reactive<UpdateUserInfoRequest>({
  nickName: '',
  email: '',
  phone: '',
  sex: 2
})

// 标签页配置
const tabs = [
  { key: 'favorites', label: '我的收藏' },
  { key: 'likes', label: '我的点赞' }
]

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await userApi.getUserInfo()
    if (response.code === 200) {
      setUserInfo(response.data)
      // 初始化编辑表单
      editForm.nickName = response.data.nickName
      editForm.email = response.data.email || ''
      editForm.phone = response.data.phone || ''
      editForm.sex = response.data.sex ?? 2
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取收藏文章
const fetchFavoriteArticles = async () => {
  loadingFavorites.value = true
  try {
    // 调试：检查token
    const token = localStorage.getItem('blog_token')
    console.log('获取收藏文章 - Token存在:', !!token)
    if (token) {
      console.log('Token前20字符:', token.substring(0, 20))
    }
    
    const response = await userApi.getFavoriteArticles(1, 20)
    console.log('收藏文章响应:', response)
    if (response.code === 200) {
      favoriteArticles.value = response.data.rows
    }
  } catch (error: any) {
    console.error('获取收藏文章失败:', error)
    if (error.message?.includes('403')) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('获取收藏文章失败')
    }
  } finally {
    loadingFavorites.value = false
  }
}

// 获取点赞文章
const fetchLikedArticles = async () => {
  loadingLikes.value = true
  try {
    // 调试：检查token
    const token = localStorage.getItem('blog_token')
    console.log('获取点赞文章 - Token存在:', !!token)
    if (token) {
      console.log('Token前20字符:', token.substring(0, 20))
    }
    
    const response = await userApi.getLikedArticles(1, 20)
    console.log('点赞文章响应:', response)
    if (response.code === 200) {
      likedArticles.value = response.data.rows
    }
  } catch (error: any) {
    console.error('获取点赞文章失败:', error)
    if (error.message?.includes('403')) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('获取点赞文章失败')
    }
  } finally {
    loadingLikes.value = false
  }
}

// 显示Toast消息
const showMessage = (type: 'success' | 'error', message: string) => {
  toastType.value = type
  toastMessage.value = message
  showToast.value = true
}

// 开始编辑
const startEdit = () => {
  // 初始化编辑表单
  if (userInfo.value) {
    editForm.nickName = userInfo.value.nickName
    editForm.email = userInfo.value.email || ''
    editForm.phone = userInfo.value.phone || ''
    editForm.sex = userInfo.value.sex ?? 2
  }
  showEditModal.value = true
}

// 取消编辑
const cancelEdit = () => {
  showEditModal.value = false
}

// 更新个人信息
const handleUpdateProfile = async () => {
  updating.value = true
  try {
    const response = await userApi.updateUserInfo(editForm)
    if (response.code === 200) {
      await fetchUserInfo()
      showEditModal.value = false
      showMessage('success', '个人信息更新成功')
    }
  } catch (error: any) {
    console.error('更新个人信息失败:', error)
    const match = error.message?.match(/API 错误 \[\d+\]: (.+)/)
    const message = match ? match[1] : '更新失败，请稍后重试'
    showMessage('error', message)
  } finally {
    updating.value = false
  }
}

// 打开头像上传
const openAvatarUpload = () => {
  showAvatarModal.value = true
}

// 上传头像
const handleAvatarUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    showMessage('error', '请选择图片文件')
    return
  }

  // 验证文件大小（最大10MB）
  if (file.size > 10 * 1024 * 1024) {
    showMessage('error', '图片大小不能超过10MB')
    return
  }

  uploading.value = true
  try {
    const response = await userApi.uploadAvatar(file)
    if (response.code === 200) {
      await fetchUserInfo()
      showAvatarModal.value = false
      showMessage('success', '头像上传成功')
      // 重置文件输入
      if (target) {
        target.value = ''
      }
    }
  } catch (error: any) {
    console.error('头像上传失败:', error)
    const match = error.message?.match(/API 错误 \[\d+\]: (.+)/)
    const message = match ? match[1] : '头像上传失败，请稍后重试'
    showMessage('error', message)
  } finally {
    uploading.value = false
  }
}

// 格式化日期
const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 监听标签页切换
const handleTabChange = (tab: string) => {
  activeTab.value = tab
  if (tab === 'favorites' && favoriteArticles.value.length === 0) {
    fetchFavoriteArticles()
  } else if (tab === 'likes' && likedArticles.value.length === 0) {
    fetchLikedArticles()
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchFavoriteArticles() // 默认加载收藏文章
})
</script>

<style scoped>
.user-profile {
  min-height: 100vh;
  background: var(--sg-background, #efefef);
  padding: 80px 0 40px;
}

body.dark .user-profile {
  background: var(--sg-background, #1a1a2e);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.profile-card {
  background: var(--sg-card-bg, #ffffff);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: var(--lg-shadow-soft);
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.profile-header {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--sg-card-bg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(80, 204, 213, 0.4);
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #50ccd5;
  color: white;
  border: 2px solid var(--sg-card-bg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.avatar-edit-btn:hover {
  transform: scale(1.1);
  background: #40b8c5;
  box-shadow: 0 4px 12px rgba(80, 204, 213, 0.4);
}

.avatar-edit-btn svg {
  width: 16px;
  height: 16px;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  color: var(--sg-text, #444444);
}

body.dark .username {
  background: linear-gradient(135deg, #7aa2ff 0%, #50ccd5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.join-date {
  color: var(--sg-text-light, #555555);
  margin: 0 0 1rem 0;
  font-size: 0.875rem;
}

.stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  text-align: center;
  padding: 12px 24px;
  background: rgba(80, 204, 213, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  background: rgba(80, 204, 213, 0.15);
  box-shadow: 0 4px 12px rgba(80, 204, 213, 0.2);
}

.stat-number {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #50ccd5;
}

.stat-label {
  color: var(--sg-text-light, #555555);
  font-size: 0.875rem;
  margin-top: 4px;
}

.btn-edit {
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #50ccd5;
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(80, 204, 213, 0.3);
}

.btn-edit:hover {
  transform: translateY(-2px);
  background: #40b8c5;
  box-shadow: 0 6px 20px rgba(80, 204, 213, 0.4);
}

.tabs {
  display: flex;
  background: var(--sg-card-bg, #ffffff);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  padding: 6px;
  margin-bottom: 24px;
  box-shadow: var(--lg-shadow-soft);
  transition: all 0.3s ease;
}

.tabs:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.tab-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
  font-weight: 500;
  color: var(--sg-text-light, #555555);
}

.tab-btn.active {
  background: #50ccd5;
  color: white;
  box-shadow: 0 4px 12px rgba(80, 204, 213, 0.3);
}

.tab-content {
  background: var(--sg-card-bg, #ffffff);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  padding: 32px;
  box-shadow: var(--lg-shadow-soft);
  transition: all 0.3s ease;
}

.tab-content:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.article-list h2 {
  margin: 0 0 24px 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--sg-text, #444444);
}

body.dark .article-list h2 {
  background: linear-gradient(135deg, #7aa2ff 0%, #50ccd5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--sg-text-light, #555555);
}

.empty-state p {
  font-size: 1.125rem;
  margin: 0;
}

.loading {
  text-align: center;
  padding: 32px 0;
  color: var(--sg-text-light, #555555);
  font-size: 1rem;
}

/* 弹窗样式 */
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
  z-index: 9999;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

.modal-small {
  max-width: 400px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 2rem;
  max-height: calc(90vh - 140px);
  overflow-y: auto;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  font-size: 0.875rem;
  color: #374151;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.modal-footer {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  padding-top: 1.5rem;
  margin-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

/* 上传区域 */
.upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  padding: 3rem 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.upload-area:hover {
  border-color: #3b82f6;
  background: #f0f9ff;
  color: #3b82f6;
}

.upload-area svg {
  margin-bottom: 1rem;
  opacity: 0.6;
}

.upload-area p {
  margin: 0 0 0.5rem 0;
  font-weight: 500;
  font-size: 1rem;
}

.upload-hint {
  font-size: 0.875rem;
  color: #9ca3af;
}

/* 动画 */
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

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: translateY(-20px) scale(0.95);
}
</style>
