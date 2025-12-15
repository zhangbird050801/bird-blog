<script setup lang="ts">
import type { ArticleVO } from '@/api'
import LgCard from '@/components/base/LgCard.vue'
import LgBadge from '@/components/base/LgBadge.vue'
import { computed, onMounted, ref } from 'vue'
import { fetchArticleTags, type Tag } from '@/api'

const props = defineProps<{
  article: ArticleVO
}>()

const tags = ref<Tag[]>([])
const tagsLoading = ref(false)

/**
 * 格式化日期
 */
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

/**
 * 发布时间标签
 */
const publishedLabel = computed(() => {
  const { publishedTime } = props.article
  if (publishedTime) {
    return `发布于 ${formatDate(publishedTime)}`
  }
  return ''
})

/**
 * 更新时间标签
 */
const updatedLabel = computed(() => {
  const { updateTime, publishedTime } = props.article
  
  // 如果有更新时间且与发布时间不同，则显示更新时间
  if (updateTime && publishedTime) {
    const updateDate = new Date(updateTime).getTime()
    const publishDate = new Date(publishedTime).getTime()
    
    // 如果更新时间比发布时间晚超过1分钟，则显示更新时间
    if (updateDate - publishDate > 60000) {
      return `更新于 ${formatDate(updateTime)}`
    }
  }
  
  return ''
})

// 加载文章标签
async function loadTags() {
  if (props.article.tags) {
    tags.value = props.article.tags
    return
  }
  
  tagsLoading.value = true
  try {
    tags.value = await fetchArticleTags(props.article.id)
  } catch (error) {
    console.error('加载文章标签失败:', error)
  } finally {
    tagsLoading.value = false
  }
}

onMounted(() => {
  loadTags()
})
</script>

<template>
  <a :href="`/article/${article.slug}`" class="article-card-link">
    <LgCard class="article-card" padding="none">
      <div class="article-card__media" v-if="article.thumbnail">
        <img :src="article.thumbnail" :alt="`${article.title} 缩略图`" loading="lazy" />
      </div>
      <div class="article-card__content">
        <div class="article-card__meta">
          <div class="article-card__topline">
            <div class="article-card__badges">
              <LgBadge v-if="article.isTop" tone="accent" class="top-badge" title="置顶">
                <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                  <path d="M16,12V4H17V2H7V4H8V12L6,14V16H11.2V22H12.8V16H18V14L16,12Z" />
                </svg>
              </LgBadge>
              <LgBadge tone="primary">{{ article.categoryName ?? '未分类' }}</LgBadge>
            </div>
            <div class="article-card__dates">
              <span class="article-card__date" v-if="publishedLabel">{{ publishedLabel }}</span>
              <span class="article-card__date article-card__date--updated" v-if="updatedLabel">{{ updatedLabel }}</span>
            </div>
          </div>
          <h2 class="article-card__title">
            {{ article.title }}
          </h2>
          <p class="article-card__summary">{{ article.summary }}</p>
          <div class="article-card__footer">
            <!-- 标签区域 -->
            <div class="article-card__tags">
              <template v-if="!tagsLoading && tags.length > 0">
                <span 
                  v-for="tag in tags" 
                  :key="tag.id" 
                  class="article-tag"
                  :title="tag.remark || tag.name"
                >
                  #{{ tag.name }}
                </span>
              </template>
              <span v-else-if="tagsLoading" class="article-tags-loading">加载中...</span>
            </div>
            <!-- 浏览量和点赞 -->
            <div class="article-card__metrics">
              <span aria-label="阅读量"><svg viewBox="0 0 20 20" aria-hidden="true"><path fill="currentColor" d="M10 4.5c4.5 0 8.5 3.229 9.5 7.5-1 4.271-5 7.5-9.5 7.5S1.5 16.271.5 12c1-4.271 5-7.5 9.5-7.5zm0 12a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9zm0-2.5a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"/></svg>{{ article.viewCount ?? 0 }}</span>
              <span aria-label="点赞"><svg viewBox="0 0 20 20" aria-hidden="true"><path fill="currentColor" d="M9.999 5.889 9.518 5.04C8.523 3.316 6.659 2.222 4.64 2.222 2.074 2.222 0 4.295 0 6.862c0 1.247.495 2.445 1.373 3.323L10 18l8.628-7.815A4.704 4.704 0 0 0 20 6.862c0-2.567-2.074-4.64-4.641-4.64-2.02 0-3.883 1.094-4.878 2.818l-.482.849z"/></svg>{{ article.likeCount ?? 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </LgCard>
  </a>
</template>

<style scoped>
.article-card-link {
  display: flex;
  flex-direction: column;
  height: 100%;
  text-decoration: none;
  color: inherit;
}

.article-card {
  position: relative;
  background: var(--sg-card-bg, #fff);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  box-shadow: var(--lg-shadow-soft);
  margin-bottom: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
  display: flex;
  flex-direction: column; /* 垂直排列 */
  height: 100%;
}

.article-card-link:hover .article-card {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5);
}

.article-card__media {
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  overflow: hidden;
  height: 180px;
  text-align: center;
  margin: 0;
  padding: 0;
  width: 100%;
  line-height: 0;
  background: #f5f5f5;
}

.article-card__media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  display: block;
  margin: 0;
  padding: 0;
}

.article-card-link:hover .article-card__media img {
  transform: scale(1.05);
}

.article-card__content {
  padding: 24px 36px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.article-card__meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.article-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 15px;
}

.article-card__badges {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-badge {
  background: transparent !important;
  color: #f87171 !important;
  padding: 0 4px !important;
  border: none !important;
  box-shadow: none !important;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: auto;
}

.top-badge svg {
  display: block;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(248, 113, 113, 0.4));
}

.article-card__dates {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.article-card__date {
  font-size: 14px;
  color: var(--lg-text-secondary);
  white-space: nowrap;
}

.article-card__date--updated {
  font-size: 13px;
  color: var(--lg-text-tertiary);
}

.article-card__title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--lg-text-primary);
  text-align: left;
  margin: 6px 0;
  transition: color 0.2s ease;
}

.article-card-link:hover .article-card__title {
  color: var(--sg-secondary);
}

/* 摘要样式 */
.article-card__summary {
  margin: 6px 0;
  line-height: 1.6;
  color: var(--lg-text-secondary);
  font-size: 15px;
  text-indent: 2em;
  text-align: justify;
  display: -webkit-box;
  -webkit-line-clamp: 4; /* 限制为4行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 14px;
  color: var(--lg-text-secondary);
  margin-top: auto;
}

.article-card__tags {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  flex: 1;
}

.article-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: var(--lg-radius-full);
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: default;
}

.article-tag:hover {
  background: rgba(16, 185, 129, 0.2);
  transform: translateY(-1px);
}

.article-tags-loading {
  color: var(--lg-text-secondary);
  font-size: 13px;
  font-style: italic;
}

.article-card__metrics {
  display: inline-flex;
  gap: 14px;
  align-items: center;
  margin-left: auto;
  flex-shrink: 0;
}

.article-card__metrics span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--lg-text-secondary);
  font-size: 14px;
  transition: color 0.2s ease;
}

.article-card__metrics span:hover {
  color: var(--sg-secondary);
}

.article-card__metrics svg {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}
</style>
