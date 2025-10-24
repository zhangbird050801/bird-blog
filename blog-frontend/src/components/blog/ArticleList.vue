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
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 28px);
}
</style>
