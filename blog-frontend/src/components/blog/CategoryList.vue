<script setup lang="ts">
import type { Category } from '@/api/types'

const props = defineProps<{
  categories: Category[]
}>()
</script>

<template>
  <ul class="category-list" aria-label="分类列表">
    <li v-for="category in props.categories" :key="category.id">
      <a :href="`/?category=${category.id}`">
        <span>{{ category.name }}</span>
        <span class="category-list__count">{{ category.articleCount ?? 0 }}</span>
      </a>
    </li>
  </ul>
</template>

<style scoped>
.category-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-list a {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 10px;
  border-radius: var(--lg-radius-2xl);
  background: rgba(255, 255, 255, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.32);
  color: var(--lg-text-primary);
  transition:
    background-color 0.3s ease,
    color 0.3s ease,
    border-radius 0.3s ease,
    transform 0.2s ease,
    padding-left 0.3s ease,
    margin-left 0.3s ease;
}

body.dark .category-list a {
  background: rgba(15, 23, 42, 0.55);
  border-color: rgba(148, 163, 184, 0.25);
}

.category-list a:hover {
  background-color: #50ccd5;
  color: #fff;
  border-radius: 8px;
  transform: scale(1.05);
  padding-left: 14px;
  margin-left: 4px;
  box-shadow: 0 0 0 3px #50ccd5;
}

.category-list__count {
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.category-list a:hover .category-list__count {
  color: #fff;
}
</style>
