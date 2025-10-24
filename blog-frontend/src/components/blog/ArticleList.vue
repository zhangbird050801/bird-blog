<script setup lang="ts">
import type { ArticleSummary } from '@/api/types'
import ArticleCard from './ArticleCard.vue'

defineProps<{
  articles: ArticleSummary[]
}>()
</script>

<template>
  <div class="article-list">
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
  </div>
</template>

<style scoped>
.article-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  column-gap: 20px;
  row-gap: 20px; 
  align-items: stretch;
}

@media (max-width: 768px) {
  .article-list {
    grid-template-columns: 1fr;
  }
}
</style>
