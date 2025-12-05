<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { userApi, type UserProfile, type UpdateUserInfoRequest } from '@/api/user'
import type { Article } from '@/api/types'
import ArticleCard from '@/components/ArticleCard.vue'
import LgButton from '@/components/base/LgButton.vue'
import LgToast from '@/components/base/LgToast.vue'

interface Props {
  show: boolean
}

interface Emits {
  (e: 'update:show', value: boolean): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const userInfo = ref<UserProfile | null>(null)
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

// 监听弹窗显示状态
watch(() => props.show, (newVal) => {
  if (newVal) {
    fetchUserInfo()
    fetchFavoriteArticles()
  }
})

// 显示Toast消息
const showMessage = (type: 'success' | 'error', message: string) => {
  toastType.value = type
  toastMessage.value = message
  showToast.value = true
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await userApi.getUserInfo()
    if (response.code === 200) {
      userInfo.value = response.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取收藏文章
const fetchFavoriteArticles = async () => {
  loadingFavorites.value = true
  try {
    const response = await userApi.getFavoriteArticles(1, 20)
    if (response.code === 200) {
      favoriteArticles.value = response.data.rows
    }
  } catch (error) {
    console.error('获取收藏文章失败:', error)
  } finally {
    loadingFavorites.value = false
  }
}

// 获取点赞文章
const fetchLikedArticles = async () => {
  loadingLikes.value = true
  try {
    const response = await userApi.getLikedArticles(1, 20)
    if (response.code === 200) {
      likedArticles.value = response.data.rows
    }
  } catch (error) {
    console.error('获取点赞文章失败:', error)
  } finally {
    loadingLikes.value = false
  }
}

// 开始编辑
const startEdit = () => {
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

  if (!file.type.startsWith('image/')) {
    showMessage('error', '请选择图片文件')
    return
  }

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

// 切换标签页
const handleTabChange = (tab: string) => {
  activeTab.value = tab
  if (tab === 'likes' && likedArticles.value.length === 0) {
    fetchLikedArticles()
  }
}

// 关闭弹窗
function closeModal() {
  emit('update:show', false)
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
    <!-- 个人中心弹窗 -->
    <div
      v-if="show"
      class="modal-backdrop"
      @click="handleBackdropClick"
    >
      <div class="user-profile-modal" @click.stop>
        <div class="modal-header">
          <h3>个人中心</h3>
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
                <h2 class="username">{{ userInfo?.nickName || userInfo?.username }}</h2>
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
              <LgButton @click="startEdit">编辑资料</LgButton>
            </div>
          </div>

          <!-- 标签页 -->
          <div class="tabs">
            <button 
              v-for="tab in tabs" 
              :key="tab.key"
              @click="handleTabChange(tab.key)"
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
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <div
      v-if="showEditModal"
      class="modal-backdrop"
      @click="showEditModal = false"
    >
      <div class="edit-modal" @click.stop>
        <div class="modal-header">
          <h3>编辑个人资料</h3>
          <button class="close-btn" @click="showEditModal = false">
            <svg viewBox="0 0 24 24" width="20" height="20">
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
            <LgButton variant="secondary" @click="cancelEdit">取消</LgButton>
            <LgButton type="submit" :loading="updating">保存</LgButton>
          </div>
        </form>
      </div>
    </div>

    <!-- 头像上传弹窗 -->
    <div
      v-if="showAvatarModal"
      class="modal-backdrop"
      @click="showAvatarModal = false"
    >
      <div class="avatar-modal" @click.stop>
        <div class="modal-header">
          <h3>上传头像</h3>
          <button class="close-btn" @click="showAvatarModal = false">
            <svg viewBox="0 0 24 24" width="20" height="20">
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
            <LgButton variant="secondary" @click="showAvatarModal = false" :disabled="uploading">
              取消
            </LgButton>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast 消息提示 -->
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

/* 主弹窗 */
.user-profile-modal {
  background: rgba(40, 42, 44, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1);
  width: 100%;
  max-width: 900px;
  max-height: 90vh;
  overflow: hidden;
  animation: modalSlideIn 0.3s ease;
}

body.dark .user-profile-modal,
body.dark .edit-modal,
body.dark .avatar-modal {
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
  max-height: calc(90vh - 80px);
  overflow-y: auto;
}

/* 用户信息卡片 */
.profile-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.2);
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #3b82f6;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-edit-btn svg {
  width: 14px;
  height: 14px;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
  color: #fff;
}

.join-date {
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 1rem 0;
  font-size: 0.875rem;
}

.stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 1.25rem;
  font-weight: bold;
  color: #3b82f6;
}

.stat-label {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.875rem;
}

.profile-actions {
  display: flex;
  justify-content: flex-end;
}

/* 标签页 */
.tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 20px;
}

.tab-btn {
  flex: 1;
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  font-size: 14px;
}

.tab-btn.active {
  background: #3b82f6;
  color: white;
}

/* 内容区域 */
.tab-content {
  min-height: 200px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: rgba(255, 255, 255, 0.4);
}

.loading {
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.6);
}

/* 编辑和头像弹窗 */
.edit-modal,
.avatar-modal {
  background: rgba(40, 42, 44, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.1);
  width: 100%;
  max-width: 420px;
  animation: modalSlideIn 0.3s ease;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  background: rgba(255, 255, 255, 0.08);
}

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* 上传区域 */
.upload-area {
  border: 2px dashed rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  color: rgba(255, 255, 255, 0.6);
}

.upload-area:hover {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.upload-area svg {
  margin-bottom: 12px;
  opacity: 0.6;
}

.upload-area p {
  margin: 0 0 8px 0;
  font-weight: 500;
  font-size: 16px;
}

.upload-hint {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.4);
}

/* 响应式 */
@media (max-width: 768px) {
  .user-profile-modal {
    max-width: 100%;
  }

  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .stats {
    justify-content: center;
  }
}
</style>
