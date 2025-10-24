<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import LgCard from '@/components/base/LgCard.vue'
import LgBadge from '@/components/base/LgBadge.vue'
import LgButton from '@/components/base/LgButton.vue'
import CommentTree from '@/components/blog/CommentTree.vue'
import ArticleToc from '@/components/blog/ArticleToc.vue'
import ReadingProgress from '@/components/blog/ReadingProgress.vue'
import ArticleNavigation from '@/components/blog/ArticleNavigation.vue'
import RelatedArticles from '@/components/blog/RelatedArticles.vue'
import { fetchArticleDetail, fetchComments } from '@/api/client'
import type { ArticleDetail, CommentNode } from '@/api/types'
import { useAsyncData } from '@/composables/useAsyncData'

const props = defineProps<{ slug: string }>()
const route = useRoute()

const articleState = useAsyncData<ArticleDetail>()
const commentsState = useAsyncData<CommentNode[]>()
const showDonate = ref(false)

// Mock数据：上一篇/下一篇
const prevArticle = ref({
  slug: 'ubuntu-redis-install',
  title: 'Ubuntu | Redis | 安装配置与密码验证',
  thumbnail: 'https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=400&h=250&fit=crop'
})

const nextArticle = ref({
  slug: 'wsl2-clash',
  title: 'wsl2 | 如何使用本机 clash',
  thumbnail: 'https://images.unsplash.com/photo-1640158615573-cd28feb1bf4e?w=400&h=250&fit=crop'
})

// Mock数据：相关推荐
const relatedArticles = ref([
  {
    slug: 'linux-bandit',
    title: 'Linux | Bandit 练习',
    thumbnail: 'https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=400&h=250&fit=crop',
    categoryName: 'Linux',
    publishedAt: '2025-04-13'
  },
  {
    slug: 'linux-debian-gnome',
    title: 'Linux | Debian | 更换 Gnome 至 Xfce4',
    thumbnail: 'https://images.unsplash.com/photo-1640158615573-cd28feb1bf4e?w=400&h=250&fit=crop',
    categoryName: 'Linux',
    publishedAt: '2024-08-23'
  },
  {
    slug: 'linux-ubuntu-lts',
    title: 'Linux | 安装并配置 Ubuntu 22.04 LTS',
    thumbnail: 'https://images.unsplash.com/photo-1640552435388-a54879e72b28?w=400&h=250&fit=crop',
    categoryName: 'Linux',
    publishedAt: '2024-05-24'
  }
])

const publishDate = computed(() => {
  const value = articleState.data.value?.publishedAt
  if (!value) return ''
  return new Date(value).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
})

async function load(slug: string) {
  const detail = await articleState.run(() => fetchArticleDetail(slug))
  if (detail) {
    await commentsState.run(() => fetchComments(detail.id))
  }
}

onMounted(() => load(props.slug))

watch(
  () => route.params.slug,
  (newSlug) => {
    if (typeof newSlug === 'string') {
      load(newSlug)
    }
  }
)
</script>

<template>
  <!-- 阅读进度条 -->
  <ReadingProgress v-if="articleState.data.value" />
  
  <article class="article-view" v-if="articleState.data.value">
    <!-- 文章标题区（卡片外） -->
    <header class="article-header">
      <h1 class="article-title">{{ articleState.data.value.title }}</h1>
      <div class="article-meta-bar">
        <span class="meta-item">
          <i class="fa fa-calendar-o"></i>
          发表于 {{ publishDate }}
        </span>
        <span class="meta-item">
          <i class="fa fa-eye"></i>
          {{ articleState.data.value.viewCount ?? 0 }} 次围观
        </span>
        <span class="meta-item">
          <i class="fa fa-heart-o"></i>
          {{ articleState.data.value.likeCount ?? 0 }} 次点赞
        </span>
        <span class="meta-item" v-if="articleState.data.value.categoryName">
          <i class="fa fa-folder-o"></i>
          {{ articleState.data.value.categoryName }}
        </span>
      </div>
    </header>

    <div class="article-container">
      <div class="article-content-wrapper">
        <LgCard class="article-unified-card" padding="lg">
          <figure class="article-main__cover" v-if="articleState.data.value.thumbnail">
            <img :src="articleState.data.value.thumbnail" :alt="`${articleState.data.value.title} 封面图`" />
            <div class="article-main__cover-glow" aria-hidden="true"></div>
          </figure>

          <!-- 文章内容 -->
          <div class="article-main__body markdown-body" v-html="articleState.data.value.content"></div>
          
          <!-- 标签 -->
          <div class="article-main__tags" v-if="articleState.data.value.tags?.length">
            <span v-for="tag in articleState.data.value.tags" :key="tag">#{{ tag }}</span>
          </div>

          <!-- 版权声明 -->
          <div class="article-main__footer">
            <div class="article-main__meta-note">
              <span>本文使用 BirdBlog 设计系统呈现。</span>
              <span>欢迎转载，注明出处即可。</span>
            </div>
          </div>

          <!-- 分割线 -->
          <hr class="section-divider" />

          <!-- 上一篇/下一篇导航 -->
          <ArticleNavigation 
            :prev-article="prevArticle" 
            :next-article="nextArticle" 
          />

          <!-- 分割线 -->
          <hr class="section-divider" />

          <!-- 相关推荐 -->
          <RelatedArticles :articles="relatedArticles" />

          <!-- 分割线 -->
          <hr class="section-divider" />

          <!-- 评论区 -->
          <div id="comments" class="comments-section">
            <h3 class="comments-title">
              <i class="fa fa-comments-o"></i>
              评论
            </h3>
            <CommentTree :comments="commentsState.data.value ?? []" />
          </div>
        </LgCard>
      </div>
      
      <!-- 侧边栏：目录导航 -->
      <aside class="article-sidebar">
        <ArticleToc :content="articleState.data.value.content" />
      </aside>
    </div>
  </article>
<!--  <div v-else class="article-loading" role="status" aria-live="polite">-->
<!--    <i class="fa fa-spinner fa-spin"></i>-->
<!--    正在加载 BirdBlog 文章...-->
<!--  </div>-->
</template>

<style scoped>
html {
  scroll-behavior: smooth;
}

.article-view {
  padding-inline: clamp(18px, 4vw, 56px);
  padding-top: 20px;
  padding-bottom: 60px;
}

.article-header {
  text-align: center;
  max-width: 900px;
  margin: 0 auto 40px;
  padding: 20px;
}

.article-title {
  font-size: clamp(28px, 5vw, 48px);
  font-weight: 700;
  color: var(--lg-text-primary);
  margin: 0 0 20px;
  line-height: 1.3;
  background: linear-gradient(135deg, var(--sg-primary), var(--sg-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.article-meta-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item i {
  color: var(--sg-primary);
}

.article-container {
  /* display: grid; */
  /* grid-template-columns: minmax(0, 1fr) 280px; */
  gap: 40px;
  max-width: 1800px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
}

@media (max-width: 300px) {
  .article-container {
    grid-template-columns: 1fr;
  }
  
  .article-sidebar {
    display: none;
  }
}

.article-content-wrapper {
  min-width: 0;
  width: 100%;
  display: flex;
  justify-content: center;
}

.article-unified-card {
  position: relative;
  width: 95% !important;
  max-width: 1200px;
  padding: clamp(32px, 5vw, 48px) !important;
  border-radius: var(--lg-radius-2xl) var(--lg-radius-2xl) 0 0 !important;
}

/* 侧边栏样式 */
.article-sidebar {
  min-width: 0;
}

/* 分割线样式 */
.section-divider {
  margin: 40px 0;
  border: none;
  border-top: 1px dashed rgba(148, 163, 184, 0.3);
}

/* 评论区域 */
.comments-section {
  margin-top: 20px;
}

.comments-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comments-title i {
  color: var(--sg-primary);
}

.article-main__cover {
  position: relative;
  border-radius: 0;
  overflow: hidden;
  width: calc(100% + 2 * clamp(32px, 5vw, 48px));
  height: clamp(200px, 35vw, 320px);
  margin: calc(-1 * clamp(32px, 5vw, 48px)) calc(-1 * clamp(32px, 5vw, 48px)) 24px;
  background: rgba(255, 255, 255, 0.2);
}

.article-main__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-main__cover-glow {
  position: absolute;
  inset: auto 0 0;
  height: 45%;
  background: linear-gradient(180deg, transparent, rgba(8, 12, 28, 0.45));
}

@media (max-width: 768px) {
  .article-main__cover {
    width: calc(100% + 2 * clamp(20px, 5vw, 24px));
    margin: calc(-1 * clamp(20px, 5vw, 24px)) calc(-1 * clamp(20px, 5vw, 24px)) 20px;
  }
  
  .article-container {
    padding: 0 12px;
  }
  
  .article-content-wrapper {
    justify-content: stretch;
  }
  
  .article-unified-card {
    width: 100% !important;
    max-width: 100%;
    padding: clamp(20px, 5vw, 24px) !important;
  }
  
  .article-header {
    margin-bottom: 24px;
    padding: 12px;
  }
  
  .article-title {
    font-size: clamp(24px, 6vw, 32px);
  }
  
  .article-meta-bar {
    gap: 12px;
    font-size: 13px;
  }
}

.article-main__meta {
  display: grid;
  gap: 14px;
  text-align: left;
  padding: 20px 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
}

.article-main__meta h1 {
  font-size: clamp(28px, 4vw, 40px);
  margin: 0;
  line-height: 1.3;
}

.article-main__meta h1 a {
  color: var(--lg-text-primary);
  text-decoration: none;
  transition: color 0.3s ease;
}

.article-main__meta h1 a:hover {
  color: var(--sg-primary);
}

.article-subtitle {
  font-size: 14px;
  color: var(--lg-text-secondary);
  font-weight: 400;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.article-subtitle i {
  color: var(--sg-secondary);
}

.article-main__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 20px 0;
  font-size: 13px;
}

.article-main__tags span {
  color: var(--sg-primary);
  background: rgba(151, 223, 253, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.article-main__tags span:hover {
  background: var(--sg-primary);
  color: #fff;
  transform: translateY(-2px);
}

/* Markdown内容样式（融合sg-blog-vue风格） */
.article-main__body {
  font-size: 16px;
  line-height: 1.8;
  color: var(--lg-text-primary);
  text-align: left;
  padding: 20px 0;
}

.article-main__body :deep(p) {
  margin: 16px 0;
  line-height: 1.8;
  word-wrap: break-word;
}

.article-main__body :deep(h1),
.article-main__body :deep(h2) {
  margin: 32px 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  font-weight: 600;
  color: var(--lg-text-primary);
}

.article-main__body :deep(h1) {
  font-size: 32px;
}

.article-main__body :deep(h2) {
  font-size: 24px;
}

.article-main__body :deep(h3) {
  margin: 24px 0 12px;
  font-size: 20px;
  font-weight: 600;
  color: var(--lg-text-primary);
}

.article-main__body :deep(h4),
.article-main__body :deep(h5),
.article-main__body :deep(h6) {
  margin: 20px 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--lg-text-primary);
}

.article-main__body :deep(a) {
  color: var(--sg-primary);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}

.article-main__body :deep(a:hover) {
  color: var(--sg-secondary);
  border-bottom-color: var(--sg-secondary);
}

.article-main__body :deep(code) {
  background: rgba(151, 223, 253, 0.15);
  border-radius: 4px;
  padding: 2px 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  color: var(--sg-secondary);
}

.article-main__body :deep(pre) {
  background: #f6f8fa;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
  margin: 16px 0;
}

body.dark .article-main__body :deep(pre) {
  background: rgba(255, 255, 255, 0.05);
}

.article-main__body :deep(pre code) {
  background: transparent;
  padding: 0;
  color: inherit;
}

.article-main__body :deep(blockquote) {
  margin: 16px 0;
  padding: 0 1em;
  color: var(--lg-text-secondary);
  border-left: 4px solid var(--sg-primary);
  background: rgba(151, 223, 253, 0.05);
  border-radius: 4px;
}

.article-main__body :deep(ul),
.article-main__body :deep(ol) {
  padding-left: 40px;
  margin: 16px 0;
}

.article-main__body :deep(li) {
  margin: 8px 0;
}

.article-main__body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 16px 0;
  overflow-x: auto;
  display: block;
}

.article-main__body :deep(table th),
.article-main__body :deep(table td) {
  padding: 12px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  text-align: left;
}

.article-main__body :deep(table th) {
  background: rgba(151, 223, 253, 0.1);
  font-weight: 600;
}

.article-main__body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.article-main__body :deep(hr) {
  margin: 32px 0;
  border: none;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

/* 赞赏区域（sg-blog-vue风格） */
.donate {
  margin: 40px 0;
  padding: 24px 0;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.donate-word {
  text-align: center;
  margin-bottom: 20px;
}

.donate-word span {
  display: inline-block;
  width: 100px;
  height: 40px;
  line-height: 40px;
  color: #fff;
  background: linear-gradient(135deg, #f4692c, #e26d6d);
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(242, 105, 44, 0.3);
}

.donate-word span:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(242, 105, 44, 0.4);
}

.donate-body {
  display: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.donate-body-show {
  display: block;
  opacity: 1;
}

.donate-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  max-width: 400px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .donate-row {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

.donate-item {
  text-align: center;
}

.donate-tip {
  display: inline-block;
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.donate-tip:hover {
  transform: scale(1.05);
}

.donate-tip img {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  display: block;
}

.donate-tip span {
  display: block;
  margin-top: 12px;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.donate-row .donate-item:first-child .donate-tip span {
  color: #44b549;
  font-weight: 600;
}

.donate-row .donate-item:last-child .donate-tip span {
  color: #00a0e9;
  font-weight: 600;
}

.article-main__footer {
  display: grid;
  gap: 16px;
  justify-items: center;
  padding: 24px 0;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.article-main__meta-note {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: var(--lg-text-secondary);
  text-align: center;
}

.article-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  height: 240px;
  border-radius: var(--lg-radius-2xl);
  background: rgba(255, 255, 255, 0.2);
  font-size: 16px;
  backdrop-filter: blur(10px);
}

.article-loading i {
  color: var(--sg-primary);
  font-size: 20px;
}

body.dark .article-loading {
  background: rgba(40, 42, 44, 0.6);
}
</style>
