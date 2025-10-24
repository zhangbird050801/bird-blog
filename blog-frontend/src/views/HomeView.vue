<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import BlogHero from '@/components/blog/BlogHero.vue'
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

const listArticles = computed(() => articleState.data.value?.items ?? [])

const latestSidebarArticles = computed(() => {
  const items = articleState.data.value?.items ?? []
  return items.slice(0, 5)
})

const categoriesList = computed(() => categoriesState.data.value ?? [])
const tagsList = computed(() => tagsState.data.value ?? [])

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
  // 并行加载所有数据，不需要等待
  Promise.all([
    articleState.run(() =>
      fetchArticles({
        page: 1,
        size: 10,
        q: route.query.q?.toString(),
        category: route.query.category ? Number(route.query.category) : undefined,
        tag: route.query.tag ? Number(route.query.tag) : undefined,
      })
    ),
    categoriesState.run(() => fetchCategories()),
    tagsState.run(() => fetchTags()),
  ]).catch(err => {
    console.error('数据加载失败:', err)
  })
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
    <section class="hero-section">
      <BlogHero
        backgroundImage="/src/assets/wallhaven-yqxqv7.jpg"
        title="BirdBlog"
        subtitle="探索技术，分享知识"
      />
    </section>

    <div class="container">
      <section class="main-grid">
        <!-- 个人信息卡片 -->
        <aside class="sidebar-left">
          <LgCard class="profile-card" padding="none">
            <AuthorCard />
          </LgCard>
          
          <!-- 公告卡片 -->
          <LgCard class="notice-card" padding="md">
            <div class="notice-header">
              <i class="fa fa-bullhorn"></i>
              公告
            </div>
            <p class="notice-content">大三在读萌新，欢迎一起学习</p>
            <p class="notice-contact">交流QQ: 2944006086</p>
          </LgCard>
          
          <!-- 最新文章 -->
          <LgCard class="sidebar-card" padding="md">
            <section class="sidebar-section">
              <h3 class="sidebar-title">最新文章</h3>
              <div v-if="articleState.loading.value" class="skeleton-sidebar">
                <div v-for="i in 3" :key="i" class="skeleton-item"></div>
              </div>
              <LatestPostsList v-else-if="latestSidebarArticles.length" :articles="latestSidebarArticles" />
            </section>
          </LgCard>
          
          <!-- 分类列表 -->
          <LgCard class="sidebar-card" padding="md">
            <section class="sidebar-section">
              <h3 class="sidebar-title">分类</h3>
              <div v-if="categoriesState.loading.value" class="skeleton-sidebar">
                <div v-for="i in 3" :key="i" class="skeleton-item"></div>
              </div>
              <CategoryList v-else-if="categoriesList.length" :categories="categoriesList" />
            </section>
          </LgCard>
          
          <!-- 标签云 -->
          <LgCard class="sidebar-card" padding="md">
            <section class="sidebar-section">
              <h3 class="sidebar-title">标签</h3>
              <div v-if="tagsState.loading.value" class="skeleton-sidebar">
                <div class="skeleton-tags">
                  <div v-for="i in 6" :key="i" class="skeleton-tag"></div>
                </div>
              </div>
              <TagCloud v-else-if="tagsList.length" :tags="tagsList" />
            </section>
          </LgCard>
        </aside>

        <div class="content-area">
          <p v-if="activeFilters.length" class="filter-info" role="status">
            当前筛选：{{ activeFilters.join(' · ') }}
          </p>

          <!-- 加载中显示骨架屏 -->
          <div v-if="articleState.loading.value" class="skeleton-list" aria-label="加载中">
            <div v-for="index in 3" :key="index" class="skeleton-card"></div>
          </div>
          <!-- 加载完成显示文章列表 -->
          <ArticleList v-else-if="listArticles.length" :articles="listArticles" />
          <!-- 无数据提示 -->
          <div v-else class="empty-state">
            <i class="fa fa-inbox"></i>
            <p>暂无文章</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home-view {
  background: transparent;
  min-height: 100vh;
}

.hero-section {
  margin-top: -60px;
  margin-bottom: 40px;
}

.container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 20px 120px;
}

.main-grid {
  display: grid;
  gap: 30px;
  grid-template-columns: 280px 1fr;
}

@media (max-width: 992px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  
  .sidebar-left {
    order: 2;
  }
  
  .content-area {
    order: 1;
  }
}

.sidebar-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: sticky;
  top: 80px;
  align-self: start;
}

.profile-card {
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  overflow: visible !important;
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.notice-card {
  background: var(--sg-card-bg);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  transition: all 0.3s ease;
}

.notice-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.notice-header {
  font-size: 18px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-header i {
  color: #f4692c;
}

.notice-content {
  font-size: 15px;
  color: var(--lg-text-secondary);
  margin: 0 0 8px;
  line-height: 1.6;
}

.notice-contact {
  font-size: 14px;
  color: var(--lg-text-secondary);
  margin: 0;
}

.sidebar-card {
  background: var(--sg-card-bg);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  box-shadow: var(--lg-shadow-soft);
  transition: all 0.3s ease;
}

.sidebar-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.sidebar-section {
  display: grid;
  gap: 16px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin: 0;
}

.content-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-info {
  margin: 4px 0 0;
  color: var(--lg-text-secondary);
  font-size: 15px;
  padding: 10px 15px;
  background: rgba(151, 223, 253, 0.1);
  border-left: 3px solid var(--sg-primary);
  border-radius: var(--lg-radius-sm);
  grid-column: 1 / -1;
}

.sidebar {
  display: grid;
  gap: 20px;
  align-content: start;
}

.sidebar-card {
  background: var(--sg-card-bg);
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  box-shadow: var(--lg-shadow-soft);
  transition: all 0.3s ease;
}

.sidebar-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.sidebar-section {
  display: grid;
  gap: 14px;
}

.sidebar-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--lg-text-primary);
}

.sidebar-subtitle {
  margin: 0;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.skeleton-list {
  display: grid;
  gap: 20px;
}

.skeleton-card {
  height: 200px;
  border-radius: var(--lg-radius-sm);
  background: linear-gradient(90deg, 
    rgba(255, 255, 255, 0.2), 
    rgba(255, 255, 255, 0.35), 
    rgba(255, 255, 255, 0.2));
  background-size: 200% 100%;
  animation: shimmer 1.6s ease-in-out infinite;
}

/* 侧边栏骨架屏 */
.skeleton-sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-item {
  height: 60px;
  border-radius: 8px;
  background: linear-gradient(90deg, 
    rgba(100, 100, 100, 0.1), 
    rgba(100, 100, 100, 0.2), 
    rgba(100, 100, 100, 0.1));
  background-size: 200% 100%;
  animation: shimmer 1.6s ease-in-out infinite;
}

.skeleton-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skeleton-tag {
  width: 80px;
  height: 28px;
  border-radius: 14px;
  background: linear-gradient(90deg, 
    rgba(255, 255, 255, 0.2), 
    rgba(255, 255, 255, 0.35), 
    rgba(255, 255, 255, 0.2));
  background-size: 200% 100%;
  animation: shimmer 1.6s ease-in-out infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--lg-text-secondary);
  text-align: center;
}

.empty-state i {
  font-size: 48px;
  color: var(--sg-primary);
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

/* 移动端适配 */
@media (max-width: 800px) {
  .container {
    max-width: 100% !important;
  }
  
  .main-grid {
    gap: 20px;
  }
}

@media (max-width: 500px) {
  .hero-section {
    margin-top: 0;
  }
  
  .container {
    padding: 0 5px;
  }
}
</style>
