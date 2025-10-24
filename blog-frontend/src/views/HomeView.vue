<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import FeaturedHero from '@/components/blog/FeaturedHero.vue'
import ArticleList from '@/components/blog/ArticleList.vue'
import CategoryList from '@/components/blog/CategoryList.vue'
import TagCloud from '@/components/blog/TagCloud.vue'
import AuthorCard from '@/components/blog/AuthorCard.vue'
import LatestPostsList from '@/components/blog/LatestPostsList.vue'
import LgCard from '@/components/base/LgCard.vue'
import { fetchArticles, fetchCategories, fetchTags } from '@/api/client'
import type { ArticleSummary, Category, Tag } from '@/api/types'
import { useAsyncData } from '@/composables/useAsyncData'

const route = useRoute()

const articleState = useAsyncData<{ total: number; items: ArticleSummary[] }>()
const categoriesState = useAsyncData<Category[]>()
const tagsState = useAsyncData<Tag[]>()

const heroArticle = computed(() => articleState.data.value?.items?.[0] ?? null)
const listArticles = computed(() => {
  const items = articleState.data.value?.items ?? []
  return heroArticle.value ? items.slice(1) : items
})

const latestSidebarArticles = computed(() => {
  const items = articleState.data.value?.items ?? []
  return items.slice(0, 5)
})

const activeFilters = computed(() => {
  const filters: string[] = []
  const q = route.query.q?.toString()
  const category = route.query.category?.toString()
  const tag = route.query.tag?.toString()
  if (q) filters.push(`关键词: ${q}`)
  if (category) filters.push(`分类: ${category}`)
  if (tag) filters.push(`标签: ${tag}`)
  return filters
})

async function load() {
  await Promise.all([
    articleState.run(() =>
      fetchArticles({
        page: 1,
        size: 6,
        q: route.query.q?.toString(),
        category: route.query.category ? Number(route.query.category) : undefined,
        tag: route.query.tag ? Number(route.query.tag) : undefined,
      })
    ),
    categoriesState.run(() => fetchCategories()),
    tagsState.run(() => fetchTags()),
  ])
}

onMounted(load)

watch(
  () => route.fullPath,
  () => {
    load()
  }
)
</script>

<template>
  <div class="home-view">
    <section v-if="heroArticle">
      <FeaturedHero :article="heroArticle" />
    </section>

    <section class="main-grid" id="archive">
      <div class="content-column">
        <p v-if="activeFilters.length" class="section-header__filters" role="status">
          当前筛选：{{ activeFilters.join(' · ') }}
        </p>

        <div v-if="articleState.loading.value" class="skeleton-list" aria-label="加载中">
          <div v-for="index in 3" :key="index" class="skeleton-card"></div>
        </div>
        <ArticleList v-else :articles="listArticles" />
      </div>

      <aside class="sidebar">
        <LgCard
          v-if="latestSidebarArticles.length"
          class="sidebar-card"
          padding="lg"
        >
          <section class="sidebar-section">
            <h3>最新文章</h3>
            <p class="sidebar-section__subtitle">即时掌握 BirdBlog 动态</p>
            <LatestPostsList :articles="latestSidebarArticles" />
          </section>
        </LgCard>

        <LgCard class="sidebar-card" padding="lg">
          <section class="sidebar-section">
            <h3>作者</h3>
            <p class="sidebar-section__subtitle">探索 BirdBlog 背后的灵感</p>
            <AuthorCard />
          </section>
        </LgCard>

        <LgCard class="sidebar-card" padding="lg">
          <section class="sidebar-section">
            <h3>分类导航</h3>
            <p class="sidebar-section__subtitle">按主题快速定位文章</p>
            <CategoryList :categories="categoriesState.data.value ?? []" />
          </section>
        </LgCard>

        <LgCard class="sidebar-card" padding="lg">
          <section class="sidebar-section">
            <h3>标签云</h3>
            <p class="sidebar-section__subtitle">BirdBlog 关键词地图</p>
            <div class="glass-scroll">
              <TagCloud :tags="tagsState.data.value ?? []" />
            </div>
          </section>
        </LgCard>
      </aside>
    </section>
  </div>
</template>

<style scoped>
.home-view {
  display: grid;
  gap: clamp(32px, 4vw, 44px);
  padding-inline: clamp(18px, 4vw, 56px);
}

.section-header {
  display: grid;
  gap: 8px;
}

.section-header__filters {
  margin: 4px 0 0;
  color: var(--lg-text-secondary);
  font-size: 14px;
}

.content-column {
  display: grid;
  gap: clamp(24px, 3vw, 32px);
}

.sidebar {
  display: grid;
  gap: clamp(12px, 1.8vw, 20px);
}

.sidebar-card {
  display: grid;
  gap: 14px;
  transition: box-shadow 0.3s ease;
}

.sidebar-card:hover {
  box-shadow: 0 0 0 3px #50ccd5;
}

.sidebar-section {
  display: grid;
  gap: 14px;
}

.sidebar-section h3 {
  margin: 0;
  font-size: 16px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--lg-text-secondary);
}

.sidebar-section__subtitle {
  margin: 0;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.hero-note__points {
  display: grid;
  gap: 18px;
}

@media (min-width: 768px) {
  .hero-note__points {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.hero-note h2 {
  margin-top: 0;
}

.hero-note p {
  margin: 0;
  color: var(--lg-text-secondary);
  font-size: 15px;
}

.skeleton-list {
  display: grid;
  gap: 20px;
}

.skeleton-card {
  height: 200px;
  border-radius: var(--lg-radius-2xl);
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.2));
  animation: pulse 1.6s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
}
</style>
