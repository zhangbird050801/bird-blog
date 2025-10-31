<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { addComment, type AddCommentRequest } from '@/api'
import LgButton from '@/components/base/LgButton.vue'
import EmojiPicker from './EmojiPicker.vue'

const props = defineProps<{
  articleId: number
  rootId?: number | null
  parentId?: number | null
  toUserId?: number | null
  toUserName?: string | null
  placeholder?: string
}>()

const emit = defineEmits<{
  success: []
  cancel: []
}>()

const { isLoggedIn } = useAuth()
const content = ref('')
const isSubmitting = ref(false)
const errorMessage = ref('')
const textareaRef = ref<HTMLTextAreaElement | null>(null)

/**
 * 提交评论
 */
async function handleSubmit() {
  // 检查登录状态
  if (!isLoggedIn.value) {
    errorMessage.value = '请先登录后再评论'
    return
  }

  // 验证评论内容
  const trimmedContent = content.value.trim()
  if (!trimmedContent) {
    errorMessage.value = '评论内容不能为空'
    return
  }

  if (trimmedContent.length < 2) {
    errorMessage.value = '评论内容至少需要2个字符'
    return
  }

  if (trimmedContent.length > 500) {
    errorMessage.value = '评论内容不能超过500个字符'
    return
  }

  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const request: AddCommentRequest = {
      articleId: props.articleId,
      content: trimmedContent,
      rootId: props.rootId || null,
      parentId: props.parentId || null,
      toUserId: props.toUserId || null,
    }

    await addComment(request)
    
    // 重置表单
    content.value = ''
    emit('success')
  } catch (error: any) {
    console.error('发表评论失败:', error)
    errorMessage.value = error?.message || '发表评论失败，请稍后重试'
  } finally {
    isSubmitting.value = false
  }
}

/**
 * 取消回复
 */
function handleCancel() {
  content.value = ''
  errorMessage.value = ''
  emit('cancel')
}

/**
 * 聚焦输入框
 */
function focus() {
  textareaRef.value?.focus()
}

/**
 * 插入表情到光标位置
 */
function insertEmoji(emoji: string) {
  const textarea = textareaRef.value
  if (!textarea) return

  const start = textarea.selectionStart || 0
  const end = textarea.selectionEnd || 0
  const textBefore = content.value.substring(0, start)
  const textAfter = content.value.substring(end)
  
  content.value = textBefore + emoji + textAfter
  
  // 设置光标位置
  nextTick(() => {
    textarea.focus()
    const newPosition = start + emoji.length
    textarea.setSelectionRange(newPosition, newPosition)
  })
}

// 暴露方法给父组件
defineExpose({
  focus
})
</script>

<template>
  <div class="comment-input">
    <!-- 回复提示 -->
    <div v-if="props.toUserName" class="comment-input__reply-tip">
      回复给 <strong>{{ props.toUserName }}</strong>
      <button class="comment-input__cancel-btn" @click="handleCancel" title="取消回复">
        <i class="fa fa-times"></i>
      </button>
    </div>

    <!-- 未登录提示 -->
    <div v-if="!isLoggedIn" class="comment-input__login-tip">
      <i class="fa fa-info-circle"></i>
      请先<router-link to="/login" class="comment-input__login-link">登录</router-link>后再评论
    </div>

    <!-- 输入区域 -->
    <div class="comment-input__input-wrapper">
      <!-- 评论输入框 -->
      <textarea
        ref="textareaRef"
        v-model="content"
        class="comment-input__textarea"
        :placeholder="props.placeholder || '说点什么吧...（支持 Markdown 语法）'"
        :disabled="!isLoggedIn || isSubmitting"
        rows="4"
      ></textarea>
      
      <!-- 表情选择器 -->
      <div v-if="isLoggedIn" class="comment-input__toolbar">
        <EmojiPicker @select="insertEmoji" />
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="comment-input__error">
      <i class="fa fa-exclamation-circle"></i>
      {{ errorMessage }}
    </div>

    <!-- 操作栏 -->
    <div class="comment-input__actions">
      <div class="comment-input__tips">
        <span class="comment-input__count" :class="{ 'is-exceed': content.length > 500 }">
          {{ content.length }} / 500
        </span>
      </div>
      <div class="comment-input__buttons">
        <LgButton
          v-if="props.toUserName"
          variant="ghost"
          size="sm"
          @click="handleCancel"
          :disabled="isSubmitting"
        >
          取消
        </LgButton>
        <LgButton
          variant="primary"
          size="sm"
          @click="handleSubmit"
          :disabled="!isLoggedIn || isSubmitting || !content.trim()"
          :loading="isSubmitting"
        >
          {{ isSubmitting ? '发表中...' : '发表评论' }}
        </LgButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.comment-input {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: var(--lg-surface);
  border-radius: var(--lg-radius-lg);
  border: 1px solid var(--lg-border);
  transition: all 0.3s ease;
}

.comment-input:focus-within {
  border-color: var(--sg-primary);
  box-shadow: 0 0 0 3px rgba(151, 223, 253, 0.1);
}

/* 回复提示 */
.comment-input__reply-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: rgba(16, 185, 129, 0.1);
  border-radius: var(--lg-radius-md);
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.comment-input__reply-tip strong {
  color: var(--sg-primary);
  font-weight: 600;
}

.comment-input__cancel-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: transparent;
  border: none;
  border-radius: 50%;
  color: var(--lg-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.comment-input__cancel-btn:hover {
  background: rgba(0, 0, 0, 0.1);
  color: var(--lg-text-primary);
}

/* 未登录提示 */
.comment-input__login-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(251, 191, 36, 0.1);
  border-radius: var(--lg-radius-md);
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.comment-input__login-tip i {
  color: #f59e0b;
}

.comment-input__login-link {
  color: var(--sg-primary);
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s ease;
}

.comment-input__login-link:hover {
  color: var(--sg-secondary);
  text-decoration: underline;
}

/* 输入区域 */
.comment-input__input-wrapper {
  position: relative;
}

/* 工具栏 */
.comment-input__toolbar {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  z-index: 10;
}

/* 输入框 */
.comment-input__textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px 16px;
  padding-right: 48px; /* 为表情按钮留出空间 */
  font-size: 14px;
  line-height: 1.6;
  color: var(--lg-text-primary);
  background: var(--lg-background);
  border: 1px solid var(--lg-border);
  border-radius: var(--lg-radius-md);
  resize: vertical;
  transition: all 0.3s ease;
  font-family: inherit;
}

.comment-input__textarea:focus {
  outline: none;
  border-color: var(--sg-primary);
  background: var(--lg-surface);
}

.comment-input__textarea:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.comment-input__textarea::placeholder {
  color: var(--lg-text-tertiary);
}

/* 错误提示 */
.comment-input__error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: var(--lg-radius-md);
  font-size: 13px;
  color: #ef4444;
}

.comment-input__error i {
  font-size: 14px;
}

/* 操作栏 */
.comment-input__actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.comment-input__tips {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-input__count {
  font-size: 13px;
  color: var(--lg-text-secondary);
  transition: color 0.2s ease;
}

.comment-input__count.is-exceed {
  color: #ef4444;
  font-weight: 600;
}

.comment-input__buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .comment-input {
    padding: 16px;
  }

  .comment-input__actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .comment-input__tips {
    justify-content: center;
  }

  .comment-input__buttons {
    width: 100%;
    justify-content: stretch;
  }

  .comment-input__buttons button {
    flex: 1;
  }
}
</style>

