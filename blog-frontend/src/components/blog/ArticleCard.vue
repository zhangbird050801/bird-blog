<script setup lang="ts">
import type { ArticleSummary } from '@/api/types'
import LgCard from '@/components/base/LgCard.vue'
import LgBadge from '@/components/base/LgBadge.vue'
import LgButton from '@/components/base/LgButton.vue'
import { computed } from 'vue'

const props = defineProps<{
  article: ArticleSummary
}>()

const publishedLabel = computed(() => {
  if (!props.article.publishedAt) return '最新'
  return new Date(props.article.publishedAt).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
})
</script>

<template>
  <LgCard class="article-card" padding="sm" interactive>
    <div class="article-card__layout">
      <div class="article-card__media" v-if="article.thumbnail">
        <img :src="article.thumbnail" :alt="`${article.title} 缩略图`" loading="lazy" />
      </div>
      <div class="article-card__meta">
        <div class="article-card__topline">
          <LgBadge tone="neutral">{{ article.categoryName ?? '未分类' }}</LgBadge>
          <span class="article-card__date">{{ publishedLabel }}</span>
        </div>
        <a class="article-card__title" :href="`/article/${article.slug}`">
          {{ article.title }}
        </a>
        <p class="article-card__summary">{{ article.summary }}</p>
        <div class="article-card__footer">
          <div class="article-card__tags" aria-label="标签">
            <span v-for="tag in article.tags" :key="tag">#{{ tag }}</span>
          </div>
          <div class="article-card__metrics">
            <span aria-label="阅读量"><svg viewBox="0 0 20 20" aria-hidden="true"><path fill="currentColor" d="M10 4.5c4.5 0 8.5 3.229 9.5 7.5-1 4.271-5 7.5-9.5 7.5S1.5 16.271.5 12c1-4.271 5-7.5 9.5-7.5zm0 12a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9zm0-2.5a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"/></svg>{{ article.viewCount ?? 0 }}</span>
            <span aria-label="点赞"><svg viewBox="0 0 20 20" aria-hidden="true"><path fill="currentColor" d="M9.999 5.889 9.518 5.04C8.523 3.316 6.659 2.222 4.64 2.222 2.074 2.222 0 4.295 0 6.862c0 1.247.495 2.445 1.373 3.323L10 18l8.628-7.815A4.704 4.704 0 0 0 20 6.862c0-2.567-2.074-4.64-4.641-4.64-2.02 0-3.883 1.094-4.878 2.818l-.482.849z"/></svg>{{ article.likeCount ?? 0 }}</span>
          </div>
        </div>
        <div class="article-card__action">
          <LgButton :as="'a'" :href="`/article/${article.slug}`" variant="ghost" size="sm">
            阅读更多
          </LgButton>
        </div>
      </div>
    </div>
  </LgCard>
</template>

<style scoped>
.article-card {
  transition: box-shadow 0.3s ease;
}

.article-card:hover {
  box-shadow: 0 0 0 3px #50ccd5;
}

.article-card__media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.article-card:hover .article-card__media img {
  transform: scale(1.05);
}

.article-card__layout {
  display: grid;
  gap: clamp(16px, 2.2vw, 24px);
}

@media (min-width: 900px) {
  .article-card__layout {
    grid-template-columns: minmax(0, 1.1fr) minmax(0, 2fr);
  }
}

.article-card__media {
  border-radius: var(--lg-radius-2xl);
  overflow: hidden;
  aspect-ratio: 4 / 3;
  border: 1px solid rgba(255, 255, 255, 0.32);
}

.article-card__meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-card__topline {
  display: flex;
  align-items: center;
  gap: 12px;
}

.article-card__date {
  font-size: 12px;
  color: var(--lg-text-secondary);
}

.article-card__title {
  font-size: clamp(20px, 2.6vw, 26px);
  font-weight: 600;
  color: var(--lg-text-primary);
}

.article-card__summary {
  margin: 0;
  color: var(--lg-text-secondary);
  font-size: 15px;
}

.article-card__footer {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.article-card__tags {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-card__metrics {
  display: inline-flex;
  gap: 14px;
  align-items: center;
}

.article-card__metrics svg {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

.article-card__action {
  margin-top: auto;
}
</style>
