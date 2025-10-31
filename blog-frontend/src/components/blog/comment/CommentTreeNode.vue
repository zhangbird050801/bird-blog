<script setup lang="ts">
import { ref } from 'vue'
import type { CommentVO } from '@/api'
import CommentInput from './CommentInput.vue'

defineOptions({
  name: 'CommentTreeNode',
})

const props = defineProps<{
  comment: CommentVO
  articleId: number
}>()

const emit = defineEmits<{
  refresh: []
}>()

const showReplyInput = ref(false)

// 生成随机头像颜色
const getAvatarColor = (name: string) => {
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E2']
  const index = name.charCodeAt(0) % colors.length
  return colors[index]
}

// 获取头像首字母
const getInitial = (name: string) => {
  return name.charAt(0).toUpperCase()
}

// 获取头像 URL 或首字母
const getAvatarDisplay = (avatar: string | null, name: string) => {
  if (avatar && avatar.startsWith('http')) {
    return { type: 'image', value: avatar }
  }
  return { type: 'initial', value: getInitial(name) }
}

// 格式化时间
const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

// 点击回复按钮
function handleReply() {
  showReplyInput.value = !showReplyInput.value
}

// 回复成功
function handleReplySuccess() {
  showReplyInput.value = false
  emit('refresh')
}

// 取消回复
function handleReplyCancel() {
  showReplyInput.value = false
}

// 子评论刷新
function handleChildRefresh() {
  emit('refresh')
}
</script>

<template>
  <li class="comment-item">
    <div class="comment-main">
      <!-- 头像 -->
      <div 
        v-if="getAvatarDisplay(props.comment.fromUserAvatar, props.comment.fromUserName).type === 'initial'"
        class="comment-avatar" 
        :style="{ backgroundColor: getAvatarColor(props.comment.fromUserName) }"
      >
        {{ getAvatarDisplay(props.comment.fromUserAvatar, props.comment.fromUserName).value }}
      </div>
      <img 
        v-else
        :src="getAvatarDisplay(props.comment.fromUserAvatar, props.comment.fromUserName).value"
        :alt="`${props.comment.fromUserName}的头像`"
        class="comment-avatar comment-avatar--image"
      />
      
      <div class="comment-content">
        <!-- 用户信息栏 -->
        <div class="comment-header">
          <span class="comment-author">{{ props.comment.fromUserName }}</span>
          <template v-if="props.comment.toUserName">
            <span class="comment-reply-arrow">
              <i class="fa fa-angle-right"></i>
            </span>
            <span class="comment-author comment-author--to">{{ props.comment.toUserName }}</span>
          </template>
          <time class="comment-time" :datetime="props.comment.createTime">
            {{ formatTime(props.comment.createTime) }}
          </time>
        </div>
        
        <!-- 评论内容 -->
        <div class="comment-text">{{ props.comment.content }}</div>
        
        <!-- 操作按钮 -->
        <div class="comment-actions">
          <button class="comment-reply-btn" @click="handleReply">
            <i class="fa fa-reply"></i>
            {{ showReplyInput ? '取消回复' : '回复' }}
          </button>
        </div>

        <!-- 回复输入框 -->
        <div v-if="showReplyInput" class="comment-reply-input">
          <CommentInput
            :article-id="props.articleId"
            :root-id="props.comment.rootId || props.comment.id"
            :parent-id="props.comment.id"
            :to-user-id="props.comment.fromUserId"
            :to-user-name="props.comment.fromUserName"
            placeholder="写下你的回复..."
            @success="handleReplySuccess"
            @cancel="handleReplyCancel"
          />
        </div>
      </div>
    </div>
    
    <!-- 回复列表 -->
    <ol v-if="props.comment.children?.length" class="comment-replies">
      <CommentTreeNode 
        v-for="reply in props.comment.children" 
        :key="reply.id" 
        :comment="reply"
        :article-id="props.articleId"
        @refresh="handleChildRefresh"
      />
    </ol>
  </li>
</template>

<style scoped>
.comment-item {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-main {
  display: flex;
  gap: 16px;
  padding: 16px 0;
}

/* 圆形头像 */
.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-avatar--image {
  object-fit: cover;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

/* 用户信息栏 - 横排显示 */
.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--lg-text-primary);
}

.comment-author--to {
  color: var(--sg-primary);
}

.comment-reply-arrow {
  color: var(--lg-text-tertiary);
  font-size: 12px;
}

.comment-client {
  font-size: 12px;
  color: var(--lg-text-secondary);
  padding: 2px 8px;
  background: rgba(148, 163, 184, 0.1);
  border-radius: 4px;
}

.comment-time {
  font-size: 12px;
  color: var(--lg-text-secondary);
  margin-left: auto;
}

/* 评论内容 */
.comment-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--lg-text-primary);
  margin-bottom: 8px;
  word-wrap: break-word;
}

/* 操作按钮 */
.comment-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.comment-reply-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  font-size: 13px;
  color: var(--lg-text-secondary);
  background: transparent;
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.comment-reply-btn:hover {
  color: var(--sg-primary);
  border-color: var(--sg-primary);
  background: rgba(151, 223, 253, 0.05);
}

.comment-reply-btn i {
  font-size: 12px;
}

/* 回复输入框 */
.comment-reply-input {
  margin-top: 12px;
}

/* 回复列表 */
.comment-replies {
  margin-left: 64px;
  padding-left: 20px;
  border-left: 2px solid rgba(148, 163, 184, 0.15);
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0;
}

@media (max-width: 768px) {
  .comment-replies {
    margin-left: 32px;
    padding-left: 12px;
  }
  
  .comment-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .comment-header {
    gap: 8px;
  }
  
  .comment-time {
    margin-left: 0;
    width: 100%;
  }
}
</style>
