<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import LgCard from '@/components/base/LgCard.vue'
import LgBadge from '@/components/base/LgBadge.vue'
import LgButton from '@/components/base/LgButton.vue'
import CommentTree from '@/components/blog/CommentTree.vue'
import { fetchArticleDetail, fetchComments } from '@/api/client'
import type { ArticleDetail, CommentNode } from '@/api/types'
import { useAsyncData } from '@/composables/useAsyncData'

const props = defineProps<{ slug: string }>()
const route = useRoute()

const articleState = useAsyncData<ArticleDetail>()
const commentsState = useAsyncData<CommentNode[]>()

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
  <article class="article-view" v-if="articleState.data.value">
    <LgCard class="article-main" padding="lg">
      <figure class="article-main__cover" v-if="articleState.data.value.thumbnail">
        <img :src="articleState.data.value.thumbnail" :alt="`${articleState.data.value.title} 封面图`" />
        <div class="article-main__cover-glow" aria-hidden="true"></div>
      </figure>
      <div class="article-main__meta">
        <LgBadge tone="neutral">{{ articleState.data.value.categoryName ?? '未分类' }}</LgBadge>
        <h1>{{ articleState.data.value.title }}</h1>
        <p>{{ articleState.data.value.summary }}</p>
        <div class="article-main__info">
          <span aria-label="发布时间">{{ publishDate }}</span>
          <span>阅读 {{ articleState.data.value.viewCount ?? 0 }}</span>
          <span>点赞 {{ articleState.data.value.likeCount ?? 0 }}</span>
        </div>
        <div class="article-main__tags">
          <span v-for="tag in articleState.data.value.tags" :key="tag">#{{ tag }}</span>
        </div>
      </div>
      <div class="article-main__body" v-html="articleState.data.value.content"></div>
      <div class="article-main__footer">
        <div class="article-main__meta-note">
          <span>本文使用 BirdBlog 设计系统呈现。</span>
          <span>欢迎转载，注明出处即可。</span>
        </div>
        <div class="article-main__actions">
          <LgButton variant="primary" size="sm">赞赏文章</LgButton>
          <LgButton as="a" href="#comments" variant="ghost" size="sm">跳至评论</LgButton>
        </div>
        <LgButton as="a" href="/" variant="ghost" size="sm">返回首页</LgButton>
      </div>
    </LgCard>

    <LgCard class="article-comments" padding="lg" id="comments">
      <CommentTree :comments="commentsState.data.value ?? []" />
    </LgCard>
  </article>
  <div v-else class="article-loading" role="status" aria-live="polite">
    正在加载 BirdBlog 文章...
  </div>
</template>

<style scoped>
.article-view {
  display: grid;
  gap: clamp(24px, 3.5vw, 40px);
}

.article-view {
  display: grid;
  gap: clamp(24px, 3.5vw, 40px);
  padding-inline: clamp(18px, 4vw, 56px);
}

.article-main {
  display: grid;
  gap: clamp(20px, 3vw, 32px);
  padding: clamp(24px, 4vw, 36px);
  width: min(1200px, 80vw);
  margin: 0 auto;
}

.article-main__cover {
  position: relative;
  border-radius: var(--lg-radius-2xl);
  overflow: hidden;
  width: 100%;
  height: clamp(200px, 35vw, 320px);
  margin: 0;
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

.article-main__meta {
  display: grid;
  gap: 14px;
  text-align: center;
}

.article-main__meta h1 {
  font-size: clamp(30px, 4vw, 48px);
  margin: 0;
}

.article-main__meta p {
  margin: 0;
  font-size: 16px;
  color: var(--lg-text-secondary);
}

.article-main__info {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.article-main__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  font-size: 13px;
  color: var(--lg-accent);
}

.article-main__body {
  font-size: 16px;
  line-height: 1.8;
  display: grid;
  gap: 1.2em;
  text-align: left;
}

.article-main__body :deep(p) {
  margin: 0;
  color: var(--lg-text-primary);
}

.article-main__body :deep(h2),
.article-main__body :deep(h3) {
  margin: 1.6em 0 0.8em;
  color: var(--lg-text-primary);
}

.article-main__body :deep(code) {
  background: rgba(79, 124, 255, 0.2);
  border-radius: var(--lg-radius-2xl);
  padding: 3px 6px;
}


.article-main__footer {
  display: grid;
  gap: 16px;
  justify-items: center;
}

.article-main__meta-note {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: var(--lg-text-secondary);
  text-align: center;
}

.article-main__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.article-comments {
  padding-inline: clamp(16px, 3vw, 32px);
  width: min(1200px, 80vw);
  margin: 0 auto;
}

.article-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 240px;
  border-radius: var(--lg-radius-2xl);
  background: rgba(255, 255, 255, 0.2);
  font-size: 16px;
}
</style>
