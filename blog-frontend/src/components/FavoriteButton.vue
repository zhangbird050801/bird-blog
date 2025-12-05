<template>
  <button
    @click="handleToggleFavorite"
    :disabled="loading"
    class="favorite-btn"
    :class="{ 'favorited': isFavorited, 'loading': loading }"
  >
    <svg
      class="icon"
      :class="{ 'favorited': isFavorited }"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
        :fill="isFavorited ? '#ff6b6b' : 'none'"
        :stroke="isFavorited ? '#ff6b6b' : 'currentColor'"
        stroke-width="2"
      />
    </svg>
    <span class="text">{{ isFavorited ? '已收藏' : '收藏' }}</span>
  </button>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { favoriteApi } from '@/api/favorite'
import { useAuth } from '@/composables/useAuth'

interface Props {
  articleId: number
}

const props = defineProps<Props>()

const { isLoggedIn } = useAuth()
const isFavorited = ref(false)
const loading = ref(false)

// 检查收藏状态
const checkFavoriteStatus = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const response = await favoriteApi.checkFavoriteStatus(props.articleId)
    if (response.code === 200) {
      isFavorited.value = response.data.isFavorited
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const handleToggleFavorite = async () => {
  if (!isLoggedIn.value) {
    // 提示用户登录
    alert('请先登录')
    return
  }

  loading.value = true
  try {
    const response = await favoriteApi.toggleFavorite(props.articleId)
    if (response.code === 200) {
      isFavorited.value = response.data.isFavorited
      // 可以添加成功提示
      console.log(response.data.message)
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkFavoriteStatus()
})
</script>

<style scoped>
.favorite-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background: white;
  color: #64748b;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.favorite-btn:hover {
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.favorite-btn.favorited {
  border-color: #ff6b6b;
  background: #fff5f5;
  color: #ff6b6b;
}

.favorite-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.icon {
  width: 1rem;
  height: 1rem;
  transition: all 0.2s ease;
}

.icon.favorited {
  animation: heartbeat 0.6s ease-in-out;
}

@keyframes heartbeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.text {
  font-weight: 500;
}

.loading .icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>