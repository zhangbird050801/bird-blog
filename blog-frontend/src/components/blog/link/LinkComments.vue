<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { CommentVO } from '@/api'
import { fetchLinkComments, CommentType, addComment, type AddCommentRequest } from '@/api'
import { useAuth } from '@/composables/useAuth'
import CommentTree from '@/components/blog/comment/CommentTree.vue'

const props = defineProps<{
  linkId: number
}>()

const { isLoggedIn } = useAuth()

// 评论相关状态
const comments = ref<CommentVO[]>([])
const commentsLoading = ref(false)
const error = ref<string | null>(null)

// 评论输入状态
const commentContent = ref('')
const submitting = ref(false)

// 加载友链评论
async function loadLinkComments() {
  if (!props.linkId) return

  commentsLoading.value = true
  error.value = null

  try {
    comments.value = await fetchLinkComments(props.linkId)
  } catch (err) {
    console.error('加载友链评论失败:', err)
    error.value = '加载评论失败，请稍后重试'
  } finally {
    commentsLoading.value = false
  }
}

// 添加评论
async function handleAddComment(content: string) {
  if (!props.linkId || !content.trim()) return

  try {
    const commentRequest: AddCommentRequest = {
      linkId: props.linkId,
      type: CommentType.LINK,
      content: content.trim()
    }

    if (!isLoggedIn.value) {
      throw new Error('请先登录后再评论')
    }

    await addComment(commentRequest)

    // 重新加载评论列表
    await loadLinkComments()
  } catch (err: any) {
    console.error('添加友链评论失败:', err)
    throw err
  }
}

// 提交评论
async function handleSubmitComment() {
  if (!commentContent.value.trim()) return

  submitting.value = true
  try {
    await handleAddComment(commentContent.value)
    commentContent.value = '' // 清空输入框
  } catch (err: any) {
    console.error('发表评论失败:', err)
    alert(err.message || '发表评论失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 刷新评论列表
function handleRefresh() {
  loadLinkComments()
}

// 组件挂载时加载评论
onMounted(() => {
  loadLinkComments()
})
</script>

<template>
  <div class="link-comments">
    <div class="comments-header">
      <h3 class="comments-title">友链评论</h3>
      <p class="comments-subtitle">对此友链有什么想说的吗？</p>
    </div>

    <!-- 评论输入框 -->
    <div class="comments-input-section">
      <div class="comment-input-form">
        <textarea
          v-model="commentContent"
          class="comment-input-textarea"
          :placeholder="isLoggedIn ? '写下你对这个友链的看法...' : '请先登录后再评论'"
          :disabled="!isLoggedIn || submitting"
          rows="4"
        ></textarea>
        <div class="comment-input-actions">
          <span class="comment-count" :class="{ 'exceed': commentContent.length > 500 }">
            {{ commentContent.length }} / 500
          </span>
          <button
            class="submit-button"
            @click="handleSubmitComment"
            :disabled="!isLoggedIn || submitting || !commentContent.trim() || commentContent.length > 500"
          >
            {{ submitting ? '发表中...' : '发表评论' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list-section">
      <!-- 加载状态 -->
      <div v-if="commentsLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载评论中...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="retry-button" @click="loadLinkComments">重试</button>
      </div>

      <!-- 评论树 -->
      <CommentTree
        v-else
        :comments="comments"
        :article-id="0"
        @refresh="handleRefresh"
      />
    </div>
  </div>
</template>

<style scoped>
/* 友链评论区容器 */
.link-comments {
  margin-top: 40px;
  padding: 32px;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

body.dark .link-comments {
  background: #1e293b;
  border-color: #334155;
}

/* 评论区头部 */
.comments-header {
  text-align: center;
  margin-bottom: 32px;
}

.comments-title {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

body.dark .comments-title {
  color: #f1f5f9;
}

.comments-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

body.dark .comments-subtitle {
  color: #94a3b8;
}

/* 评论输入区域 */
.comments-input-section {
  margin-bottom: 32px;
}

.comment-input-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

body.dark .comment-input-form {
  background: #1f2937;
  border-color: #374151;
}

.comment-input-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #ffffff;
  color: #111827;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  transition: all 0.2s ease;
}

body.dark .comment-input-textarea {
  background: #374151;
  border-color: #4b5563;
  color: #f9fafb;
}

.comment-input-textarea:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.comment-input-textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.comment-input-textarea::placeholder {
  color: #9ca3af;
}

body.dark .comment-input-textarea::placeholder {
  color: #6b7280;
}

.comment-input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-count {
  font-size: 13px;
  color: var(--sg-text-secondary);
  transition: color 0.2s ease;
}

.comment-count.exceed {
  color: #ef4444;
  font-weight: 600;
}

.submit-button {
  padding: 8px 20px;
  border-radius: 8px;
  border: 1px solid #10b981;
  background: #10b981;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submit-button:hover:not(:disabled) {
  background: #059669;
  border-color: #059669;
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 评论列表区域 */
.comments-list-section {
  min-height: 200px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: var(--sg-text-secondary);
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--sg-border-lighter);
  border-top-color: #10b981;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
  color: var(--sg-text-secondary);
}

.error-state p {
  margin: 0 0 16px 0;
}

.retry-button {
  padding: 8px 20px;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .link-comments {
    margin-top: 24px;
    padding: 24px 20px;
  }

  .comments-title {
    font-size: 20px;
  }

  .comments-header {
    margin-bottom: 24px;
  }

  .comments-input-section {
    margin-bottom: 24px;
  }
}
</style>