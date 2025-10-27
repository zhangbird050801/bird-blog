<script setup lang="ts">
import type { ArticleSummary } from '@/api'
import LgCard from '@/components/base/LgCard.vue'
import LgBadge from '@/components/base/LgBadge.vue'
import LgButton from '@/components/base/LgButton.vue'
import { computed } from 'vue'

const props = defineProps<{
  article: ArticleSummary
}>()

const publishedLabel = computed(() => {
  if (!props.article.publishedAt) return '最新发布'
  const date = new Date(props.article.publishedAt)
  return date.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
  })
})
</script>

<template>
  <LgCard class="hero-card" padding="lg" interactive>
    <div class="hero-card__grid">
      <div class="hero-card__meta">
        <div class="hero-card__badge">
          <LgBadge tone="accent">置顶推荐</LgBadge>
          <span class="hero-card__date">{{ publishedLabel }}</span>
        </div>
        <h1>{{ article.title }}</h1>
        <p>{{ article.summary }}</p>
        <div class="hero-card__actions">
          <LgButton :as="'a'" :href="`/article/${article.slug}`" variant="primary" size="lg">
            阅读文章
          </LgButton>
          <LgButton as="a" href="#archive" variant="ghost">
            查看归档
          </LgButton>
        </div>
        <div class="hero-card__tags" aria-label="文章标签">
          <span v-for="tag in article.tags" :key="tag" class="hero-card__tag">
            #{{ tag }}
          </span>
        </div>
      </div>
      <div class="hero-card__visual">
        <img :src="article.thumbnail" :alt="`${article.title} 封面图`" />
        <div class="hero-card__glow" aria-hidden="true"></div>
      </div>
    </div>
  </LgCard>
</template>

<style scoped>
.hero-card__grid {
  display: grid;
  gap: clamp(18px, 3vw, 32px);
}

@media (min-width: 1024px) {
  .hero-card__grid {
    grid-template-columns: 1fr 1fr;
    align-items: center;
  }
}

.hero-card__meta {
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 2vw, 18px);
}

.hero-card__badge {
  display: flex;
  align-items: center;
  gap: 12px;
}

.hero-card__date {
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.hero-card__visual {
  position: relative;
  border-radius: var(--lg-radius-2xl);
  overflow: hidden;
  aspect-ratio: 4 / 3;
}

.hero-card__visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-card__glow {
  position: absolute;
  inset: auto 0 0;
  height: 60%;
  background: linear-gradient(180deg, transparent, rgba(12, 17, 28, 0.4));
}

.hero-card__actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  color: var(--lg-accent);
  font-size: 13px;
}

.hero-card h1 {
  font-size: clamp(28px, 4vw, 44px);
  line-height: 1.1;
}

.hero-card p {
  font-size: clamp(15px, 2vw, 18px);
  color: var(--lg-text-secondary);
}
</style>
