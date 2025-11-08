<template>
  <el-dialog
    v-model="visible"
    :title="article?.title || '文章详情'"
    width="80%"
    :before-close="handleClose"
    class="article-view-dialog"
  >
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="article" class="article-content">
      <!-- 文章头部信息 -->
      <div class="article-header">
        <div class="article-meta">
          <el-tag v-if="article.categoryName" type="info" class="meta-item">
            {{ article.categoryName }}
          </el-tag>
          <el-tag :type="article.status === 0 ? 'success' : 'warning'" class="meta-item">
            {{ article.status === 0 ? '已发布' : '草稿' }}
          </el-tag>
          <el-tag v-if="article.isTop" type="danger" class="meta-item">置顶</el-tag>

          <div class="meta-info">
            <span v-if="article.authorName" class="meta-text">
              <el-icon><User /></el-icon>
              {{ article.authorName }}
            </span>
            <span v-if="article.publishedTime" class="meta-text">
              <el-icon><Calendar /></el-icon>
              {{ formatDate(article.publishedTime) }}
            </span>
            <span class="meta-text">
              <el-icon><View /></el-icon>
              {{ article.viewCount || 0 }} 浏览
            </span>
            <span class="meta-text">
              <el-icon><Star /></el-icon>
              {{ article.likeCount || 0 }} 点赞
            </span>
          </div>
        </div>

        <!-- 文章摘要 -->
        <div v-if="article.summary" class="article-summary">
          {{ article.summary }}
        </div>
      </div>

      <!-- 文章缩略图 -->
      <div v-if="article.thumbnail" class="article-thumbnail">
        <el-image
          :src="article.thumbnail"
          :alt="article.title"
          fit="cover"
          style="width: 100%; max-height: 400px; border-radius: 8px;"
        />
      </div>

      <!-- Markdown 渲染内容 -->
      <div class="markdown-body" v-html="renderedContent"></div>
    </div>

    <div v-else class="empty-state">
      <el-empty description="文章不存在或已被删除" />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit" :icon="Edit">
          编辑文章
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Calendar, View, Star, Edit } from '@element-plus/icons-vue'
import { useMarkdown } from '@/composables/useMarkdown'
import { getArticleDetail } from '@/api/article'
import type { Article } from '@/api/article'

interface Props {
  modelValue: boolean
  articleId?: number
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', article: Article): void
}

const props = withDefaults(defineProps<Props>(), {
  articleId: undefined
})

const emit = defineEmits<Emits>()

const { render } = useMarkdown()

// 响应式数据
const loading = ref(false)
const article = ref<Article | null>(null)

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const renderedContent = computed(() => {
  return article.value?.content ? render(article.value.content) : ''
})

// 监听文章ID变化
watch(() => props.articleId, (newId) => {
  if (newId && visible.value) {
    loadArticleDetail(newId)
  }
}, { immediate: true })

// 监听弹窗显示状态
watch(visible, (isVisible) => {
  if (isVisible && props.articleId) {
    loadArticleDetail(props.articleId)
  }
})

/**
 * 加载文章详情
 */
const loadArticleDetail = async (id: number) => {
  if (!id) return

  loading.value = true
  try {
    article.value = await getArticleDetail(id)
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
    article.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 关闭弹窗
 */
const handleClose = () => {
  visible.value = false
  article.value = null
}

/**
 * 编辑文章
 */
const handleEdit = () => {
  if (article.value) {
    emit('edit', article.value)
    handleClose()
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.article-view-dialog {
  --el-dialog-padding-primary: 24px;
}

.loading-container {
  padding: 20px;
}

.article-content {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 8px;
}

.article-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  margin-right: 8px;
}

.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-left: auto;
}

.meta-text {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.article-summary {
  color: var(--el-text-color-regular);
  font-size: 16px;
  line-height: 1.6;
  padding: 16px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 8px;
  border-left: 4px solid var(--el-color-primary);
}

.article-thumbnail {
  margin-bottom: 24px;
  text-align: center;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* Markdown 内容样式 */
.markdown-body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', SimSun, sans-serif;
  font-size: 16px;
  line-height: 1.8;
  color: var(--el-text-color-primary);
  word-wrap: break-word;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: var(--el-text-color-primary);
}

.markdown-body :deep(h1) {
  font-size: 2em;
  border-bottom: 1px solid var(--el-border-color-light);
  padding-bottom: 0.3em;
}

.markdown-body :deep(h2) {
  font-size: 1.5em;
  border-bottom: 1px solid var(--el-border-color-light);
  padding-bottom: 0.3em;
}

.markdown-body :deep(h3) {
  font-size: 1.25em;
}

.markdown-body :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(blockquote) {
  margin: 0;
  padding: 0 16px;
  color: var(--el-text-color-regular);
  border-left: 4px solid var(--el-border-color);
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 32px;
  margin-bottom: 16px;
}

.markdown-body :deep(li) {
  margin-bottom: 4px;
}

.markdown-body :deep(li > p) {
  margin-bottom: 0;
}

.markdown-body :deep(code) {
  padding: 2px 4px;
  font-size: 0.9em;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.markdown-body :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 0.9em;
  line-height: 1.45;
  background-color: #282c34;
  border-radius: 8px;
  margin-bottom: 16px;
}

.markdown-body :deep(pre code) {
  display: inline;
  padding: 0;
  background: none;
  font-size: 1em;
}

.markdown-body :deep(table) {
  border-collapse: collapse;
  border-spacing: 0;
  margin-bottom: 16px;
  width: 100%;
}

.markdown-body :deep(table th),
.markdown-body :deep(table td) {
  padding: 8px 12px;
  border: 1px solid var(--el-border-color-light);
  text-align: left;
}

.markdown-body :deep(table th) {
  font-weight: 600;
  background-color: var(--el-fill-color-lighter);
}

.markdown-body :deep(table tr:nth-child(2n)) {
  background-color: var(--el-fill-color-lighter);
}

.markdown-body :deep(a) {
  color: var(--el-color-primary);
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

.markdown-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

.markdown-body :deep(hr) {
  height: 1px;
  border: none;
  background-color: var(--el-border-color-light);
  margin: 24px 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .meta-info {
    margin-left: 0;
    gap: 12px;
  }

  .markdown-body {
    font-size: 14px;
  }
}
</style>