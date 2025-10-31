<script setup lang="ts">
import { ref, computed } from 'vue'
import type { CommentVO } from '@/api'
import CommentInput from './CommentInput.vue'
import { useMarkdown } from '@/composables/useMarkdown'
import '../../../assets/atom-one-dark.css'

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
const { render: renderMarkdown } = useMarkdown()

// 渲染评论内容为 Markdown
const renderedContent = computed(() => {
  if (!props.comment.content) return ''
  return renderMarkdown(props.comment.content)
})

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
        <div class="comment-text markdown-body" v-html="renderedContent"></div>
        
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

/* Markdown 内容样式（评论专用，更紧凑） */
.comment-text.markdown-body :deep(p) {
  margin: 8px 0;
  line-height: 1.6;
}

.comment-text.markdown-body :deep(p:first-child) {
  margin-top: 0;
}

.comment-text.markdown-body :deep(p:last-child) {
  margin-bottom: 0;
}

.comment-text.markdown-body :deep(h1),
.comment-text.markdown-body :deep(h2),
.comment-text.markdown-body :deep(h3),
.comment-text.markdown-body :deep(h4),
.comment-text.markdown-body :deep(h5),
.comment-text.markdown-body :deep(h6) {
  margin: 12px 0 8px;
  font-weight: 600;
  color: var(--lg-text-primary);
}

.comment-text.markdown-body :deep(h1) {
  font-size: 18px;
}

.comment-text.markdown-body :deep(h2) {
  font-size: 16px;
}

.comment-text.markdown-body :deep(h3),
.comment-text.markdown-body :deep(h4),
.comment-text.markdown-body :deep(h5),
.comment-text.markdown-body :deep(h6) {
  font-size: 14px;
}

.comment-text.markdown-body :deep(a) {
  color: var(--sg-primary);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all 0.2s ease;
}

.comment-text.markdown-body :deep(a:hover) {
  color: var(--sg-secondary);
  border-bottom-color: var(--sg-secondary);
}

.comment-text.markdown-body :deep(code) {
  background: rgba(151, 223, 253, 0.15);
  border-radius: 3px;
  padding: 2px 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  color: var(--sg-secondary);
}

.comment-text.markdown-body :deep(pre) {
  background: #282c34;
  border-radius: 12px;
  padding: 20px;
  overflow-x: auto;
  margin: 20px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.comment-text.markdown-body :deep(pre::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 40px;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), transparent);
  border-radius: 12px 12px 0 0;
  pointer-events: none;
}

/* 代码块装饰点（模拟 macOS 窗口控制按钮） */
.comment-text.markdown-body :deep(pre::after) {
  content: '';
  position: absolute;
  top: 12px;
  left: 16px;
  width: 12px;
  height: 12px;
  background: #ff5f56;
  border-radius: 50%;
  box-shadow: 
    20px 0 0 #ffbd2e,
    40px 0 0 #27c93f;
}

body.dark .comment-text.markdown-body :deep(pre) {
  background: #1e1e1e;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.05);
}

.comment-text.markdown-body :deep(pre code) {
  background: transparent;
  padding: 0;
  color: #abb2bf;
  display: block;
  line-height: 1.6;
  font-size: 14px;
  padding-top: 12px; /* 为装饰点留出空间 */
}

/* 代码高亮特殊样式 */
.comment-text.markdown-body :deep(.hljs) {
  padding: 0;
  background: transparent;
}

.comment-text.markdown-body :deep(pre.hljs) {
  padding: 20px;
}

.comment-text.markdown-body :deep(ul),
.comment-text.markdown-body :deep(ol) {
  margin: 8px 0;
  padding-left: 24px;
}

.comment-text.markdown-body :deep(li) {
  margin: 4px 0;
}

.comment-text.markdown-body :deep(blockquote) {
  margin: 8px 0;
  padding-left: 16px;
  border-left: 3px solid var(--sg-primary);
  color: var(--lg-text-secondary);
  font-style: italic;
}

.comment-text.markdown-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 8px 0;
}

.comment-text.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 8px 0;
  font-size: 13px;
}

.comment-text.markdown-body :deep(th),
.comment-text.markdown-body :deep(td) {
  padding: 6px 8px;
  border: 1px solid var(--lg-border);
}

.comment-text.markdown-body :deep(th) {
  background: var(--lg-surface);
  font-weight: 600;
}

.comment-text.markdown-body :deep(hr) {
  margin: 12px 0;
  border: none;
  border-top: 1px solid var(--lg-border);
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
