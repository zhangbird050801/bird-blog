<script setup lang="ts">
import type { CommentNode } from '@/api/types'
import CommentTreeNode from './CommentTreeNode.vue'

const props = defineProps<{
  comments: CommentNode[]
}>()
</script>

<template>
  <section class="comment-tree">
    <h2>评论</h2>
    <p v-if="!props.comments.length" class="comment-tree__empty">期待你的想法，成为首位评论者。</p>
    <ol v-else class="comment-tree__list">
      <CommentTreeNode v-for="comment in props.comments" :key="comment.id" :comment="comment" />
    </ol>
  </section>
</template>

<style scoped>
.comment-tree {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-tree__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 18px;
}

.comment-tree__empty {
  color: var(--lg-text-secondary);
}
</style>
