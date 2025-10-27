<script setup lang="ts">
import type { CommentNode } from '@/api'
import CommentTreeNode from './CommentTreeNode.vue'

const props = defineProps<{
  comments: CommentNode[]
}>()
</script>

<template>
  <section class="comment-tree">
    <p v-if="!props.comments.length" class="comment-tree__empty">
      <i class="fa fa-comment-o"></i>
      暂无评论，期待你的想法
    </p>
    <ol v-else class="comment-tree__list">
      <CommentTreeNode v-for="comment in props.comments" :key="comment.id" :comment="comment" />
    </ol>
  </section>
</template>

<style scoped>
.comment-tree {
  display: flex;
  flex-direction: column;
}

.comment-tree__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.comment-tree__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px 20px;
  color: var(--lg-text-secondary);
  font-size: 14px;
}

.comment-tree__empty i {
  font-size: 18px;
  color: var(--sg-primary);
}
</style>
