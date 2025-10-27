<script setup lang="ts">
interface Article {
  slug: string
  title: string
  thumbnail?: string
}

defineProps<{
  prevArticle?: Article
  nextArticle?: Article
}>()
</script>

<template>
  <nav class="article-navigation">
    <a 
      v-if="prevArticle" 
      :href="`#/article/${prevArticle.slug}`" 
      class="nav-item nav-prev"
    >
      <div class="nav-icon">
        <i class="fa fa-chevron-left"></i>
      </div>
      <div class="nav-content">
        <span class="nav-label">上一篇</span>
        <span class="nav-title">{{ prevArticle.title }}</span>
      </div>
      <div v-if="prevArticle.thumbnail" class="nav-thumb">
        <img :src="prevArticle.thumbnail" :alt="prevArticle.title" />
      </div>
    </a>
    <div v-else class="nav-item nav-disabled">
      <span class="nav-label">没有更早的文章了</span>
    </div>

    <a 
      v-if="nextArticle" 
      :href="`#/article/${nextArticle.slug}`" 
      class="nav-item nav-next"
    >
      <div v-if="nextArticle.thumbnail" class="nav-thumb">
        <img :src="nextArticle.thumbnail" :alt="nextArticle.title" />
      </div>
      <div class="nav-content">
        <span class="nav-label">下一篇</span>
        <span class="nav-title">{{ nextArticle.title }}</span>
      </div>
      <div class="nav-icon">
        <i class="fa fa-chevron-right"></i>
      </div>
    </a>
    <div v-else class="nav-item nav-disabled">
      <span class="nav-label">没有更新的文章了</span>
    </div>
  </nav>
</template>

<style scoped>
.article-navigation {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 40px 0;
}

@media (max-width: 768px) {
  .article-navigation {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  text-decoration: none;
  color: var(--lg-text-primary);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

body.dark .nav-item {
  background: rgba(40, 42, 44, 0.6);
}

.nav-item:not(.nav-disabled):hover {
  background: rgba(151, 223, 253, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(151, 223, 253, 0.3);
}

.nav-item:not(.nav-disabled):hover .nav-icon {
  color: var(--sg-primary);
  transform: scale(1.2);
}

.nav-disabled {
  opacity: 0.4;
  cursor: not-allowed;
  justify-content: center;
}

.nav-prev {
  justify-content: flex-start;
}

.nav-next {
  justify-content: flex-end;
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(151, 223, 253, 0.2);
  color: var(--sg-secondary);
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.nav-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.nav-prev .nav-content {
  text-align: left;
}

.nav-next .nav-content {
  text-align: right;
}

.nav-label {
  font-size: 12px;
  color: var(--lg-text-secondary);
  font-weight: 600;
  text-transform: uppercase;
}

.nav-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--lg-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.nav-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.nav-item:not(.nav-disabled):hover .nav-thumb img {
  transform: scale(1.1);
}

@media (max-width: 768px) {
  .nav-thumb {
    width: 50px;
    height: 50px;
  }
  
  .nav-title {
    font-size: 13px;
  }
}
</style>
