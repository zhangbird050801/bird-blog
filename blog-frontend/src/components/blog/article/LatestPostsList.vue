<script setup lang="ts">
import type { HotArticleVO, LatestArticleVO } from '@/api'

const props = defineProps<{
  articles: (HotArticleVO | LatestArticleVO)[]
}>()

const fallbackImage = 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=160&q=60'
</script>

<template>
  <ul class="latest-posts" aria-label="最新文章列表">
    <li v-for="article in props.articles" :key="article.id">
      <a :href="`/article/${article.slug}`" class="latest-posts__item">
        <div class="latest-posts__thumb">
          <img :src="article.thumbnail ?? fallbackImage" :alt="`${article.title} 缩略图`" loading="lazy" />
        </div>
        <div class="latest-posts__meta">
          <span class="latest-posts__title">{{ article.title }}</span>
          <time v-if="'publishedTime' in article && article.publishedTime" :datetime="article.publishedTime" class="latest-posts__date">
            {{ new Date(article.publishedTime).toLocaleDateString() }}
          </time>
        </div>
      </a>
    </li>
  </ul>
</template>

<style scoped>
.latest-posts {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 12px;
}

.latest-posts__item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 5px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.18);
  color: inherit;
  text-decoration: none;
  transition:
    background-color 0.3s ease,
    color 0.3s ease,
    border-radius 0.3s ease,
    transform 0.2s ease,
    padding-left 0.3s ease,
    margin-left 0.3s ease;
}

body.dark .latest-posts__item {
  background: rgba(15, 23, 42, 0.4);
}

.latest-posts__item:hover {
  background-color: #50ccd5;
  color: #fff;
  border-radius: 8px;
  transform: scale(1.05);
  padding-left: 12px;
  margin-left: 4px;
  box-shadow: 0 0 0 3px #50ccd5;
}

.latest-posts__item:hover .latest-posts__date {
  color: #fff;
}

.latest-posts__thumb {
  flex: 0 0 52px;
  height: 52px;
  border-radius: 8px;
  overflow: hidden;
}

.latest-posts__thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.latest-posts__meta {
  display: grid;
  gap: 4px;
}

.latest-posts__title {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.3;
}

.latest-posts__date {
  font-size: 13px;
  color: rgba(15, 23, 42, 0.6);
}

body.dark .latest-posts__date {
  color: rgba(226, 232, 240, 0.7);
}
</style>
