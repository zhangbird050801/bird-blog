<script setup lang="ts">
import type { ArticleCardInfo } from '@/api'

interface RelatedArticle extends ArticleCardInfo {
  categoryName?: string
}

defineProps<{
  articles: RelatedArticle[]
}>()

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<template>
  <div class="related-articles" v-if="articles.length > 0">
    <h3 class="related-title">
      <i class="fa fa-thumbs-up"></i>
      相关推荐
    </h3>
    <div class="related-grid">
      <a
        v-for="article in articles"
        :key="article.slug"
        :href="`#/article/${article.slug}`"
        class="related-card"
      >
        <div class="related-thumb">
          <img 
            :src="article.thumbnail || 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=400&h=250&fit=crop'" 
            :alt="article.title" 
          />
          <div class="related-overlay"></div>
        </div>
        <div class="related-info">
          <h4 class="related-article-title">{{ article.title }}</h4>
          <div class="related-meta">
            <span v-if="article.categoryName" class="related-category">
              <i class="fa fa-folder-o"></i>
              {{ article.categoryName }}
            </span>
            <span v-if="article.publishedTime" class="related-date">
              <i class="fa fa-calendar-o"></i>
              {{ formatDate(article.publishedTime) }}
            </span>
          </div>
        </div>
      </a>
    </div>
  </div>
</template>

<style scoped>
.related-articles {
  margin: 40px 0;
}

.related-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--sg-primary);
}

.related-title i {
  color: var(--sg-primary);
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

@media (max-width: 768px) {
  .related-grid {
    grid-template-columns: 1fr;
  }
}

.related-card {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  overflow: hidden;
  text-decoration: none;
  color: var(--lg-text-primary);
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

body.dark .related-card {
  background: rgba(40, 42, 44, 0.6);
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(151, 223, 253, 0.3);
}

.related-thumb {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
}

.related-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.related-card:hover .related-thumb img {
  transform: scale(1.1);
}

.related-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent, rgba(0, 0, 0, 0.3));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.related-card:hover .related-overlay {
  opacity: 1;
}

.related-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.related-article-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin: 0;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: color 0.3s ease;
}

.related-card:hover .related-article-title {
  color: var(--sg-primary);
}

.related-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--lg-text-secondary);
}

.related-category,
.related-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.related-category i,
.related-date i {
  color: var(--sg-secondary);
}
</style>
