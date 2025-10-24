<script setup lang="ts">
import type { Tag } from '@/api/types'

const props = defineProps<{
  tags: Tag[]
}>()
</script>

<template>
  <div class="tag-cloud" aria-label="标签云">
    <a v-for="tag in props.tags" :key="tag.id" :href="`/?tag=${tag.id}`">
      #{{ tag.name }}
      <span class="tag-cloud__count">({{ tag.articleCount ?? 0 }})</span>
    </a>
  </div>
</template>

<style scoped>
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-cloud a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  border-radius: var(--lg-radius-full);
  background: rgba(79, 124, 255, 0.16);
  color: var(--lg-accent);
  font-size: 14px;
  transition:
    background-color 0.3s ease,
    color 0.3s ease,
    border-radius 0.3s ease,
    transform 0.2s ease,
    padding-left 0.3s ease,
    margin-left 0.3s ease;
}

.tag-cloud__count {
  color: var(--lg-text-secondary);
  font-size: 13px;
}

.tag-cloud a:hover {
  background-color: #50ccd5;
  color: #fff;
  border-radius: 8px;
  transform: scale(1.05);
  padding-left: 12px;
  margin-left: 4px;
  box-shadow: 0 0 0 3px #50ccd5;
}

.tag-cloud a:hover .tag-cloud__count {
  color: #fff;
}
</style>
