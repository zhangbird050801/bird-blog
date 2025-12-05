<template>
  <article class="article-card">
    <div class="article-thumbnail" v-if="article.thumbnail">
      <img :src="article.thumbnail" :alt="article.title" />
    </div>
    
    <div class="article-content">
      <div class="article-meta">
        <span class="category" v-if="article.categoryName">{{ article.categoryName }}</span>
        <span class="date">{{ formatDate(article.publishedTime) }}</span>
        <span class="badge top" v-if="article.isTop">置顶</span>
      </div>
      
      <h3 class="article-title">
        <router-link :to="`/article/${article.slug || article.id}`">
          {{ article.title }}
        </router-link>
      </h3>
      
      <p class="article-summary" v-if="article.summary">
        {{ article.summary }}
      </p>
      
      <div class="article-stats">
        <span class="stat-item">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
          </svg>
          {{ article.viewCount || 0 }}
        </span>
        
        <span class="stat-item">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          {{ article.likeCount || 0 }}
        </span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Article } from '@/api/types'

interface Props {
  article: Article
}

defineProps<Props>()

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

<style scoped>
.article-card {
  display: flex;
  gap: 1rem;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  background: white;
  transition: all 0.2s ease;
  margin-bottom: 1rem;
}

.article-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #d1d5db;
}

.article-thumbnail {
  flex-shrink: 0;
  width: 120px;
  height: 80px;
  border-radius: 0.5rem;
  overflow: hidden;
}

.article-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.category {
  background: #f3f4f6;
  color: #374151;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
}

.badge.top {
  background: #fef3c7;
  color: #d97706;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
}

.article-title {
  margin: 0 0 0.75rem 0;
  font-size: 1.125rem;
  font-weight: 600;
  line-height: 1.4;
}

.article-title a {
  color: #1f2937;
  text-decoration: none;
  transition: color 0.2s ease;
}

.article-title a:hover {
  color: #3b82f6;
}

.article-summary {
  margin: 0 0 1rem 0;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-stats {
  display: flex;
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.stat-item svg {
  width: 1rem;
  height: 1rem;
}

@media (max-width: 640px) {
  .article-card {
    flex-direction: column;
    padding: 1rem;
  }
  
  .article-thumbnail {
    width: 100%;
    height: 160px;
  }
}
</style>