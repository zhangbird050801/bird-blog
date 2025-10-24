<script setup lang="ts">
import type { CommentNode } from '@/api/types'

defineOptions({
  name: 'CommentTreeNode',
})

const props = defineProps<{
  comment: CommentNode
}>()
</script>

<template>
  <li class="comment-node">
    <article class="comment-node__card lg-glass">
      <header>
        <strong>{{ props.comment.author }}</strong>
        <time v-if="props.comment.createdAt" :datetime="props.comment.createdAt">
          {{ new Date(props.comment.createdAt).toLocaleString() }}
        </time>
      </header>
      <p>{{ props.comment.content }}</p>
    </article>
    <ol v-if="props.comment.replies?.length" class="comment-node__children">
      <CommentTreeNode v-for="reply in props.comment.replies" :key="reply.id" :comment="reply" />
    </ol>
  </li>
</template>

<style scoped>
.comment-node {
  display: grid;
  gap: 16px;
}

.comment-node__card {
  padding: 18px 22px;
  display: grid;
  gap: 8px;
}

.comment-node__card header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.comment-node__card p {
  margin: 0;
  font-size: 15px;
}

.comment-node__children {
  margin: 0 0 0 24px;
  padding-left: 16px;
  border-left: 1px solid rgba(148, 163, 184, 0.26);
  display: grid;
  gap: 14px;
  list-style: none;
}
</style>
