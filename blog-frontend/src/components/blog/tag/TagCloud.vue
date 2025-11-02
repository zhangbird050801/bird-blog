<script setup lang="ts">
import type { Tag } from '@/api'
import { computed } from 'vue'

const props = defineProps<{
  tags: Tag[]
}>()

// 生成固定的标签颜色
const generateTagColor = () => {
  // 使用统一的灰色
  return '#666666'
}

// 计算标签字体大小
const getTagSize = (articleCount?: number) => {
  if (!articleCount) return 'base'

  // 根据文章数量返回不同的大小级别
  if (articleCount >= 20) return 'xl'
  if (articleCount >= 10) return 'lg'
  if (articleCount >= 5) return 'md'
  return 'sm'
}

// 文章越少越透明
const getTagOpacity = (articleCount?: number) => {
  if (!articleCount) return 0.85

  const maxCount = Math.max(...props.tags.map(tag => tag.articleCount || 0))
  const ratio = articleCount / maxCount
  return 0.85 + (ratio * 0.15) // 0.85 到 1.0 之间
}
</script>

<template>
  <div class="tag-cloud" aria-label="标签云">
    <a
      v-for="tag in props.tags"
      :key="tag.id"
      :href="`/?tag=${tag.id}`"
      :title="`${tag.name}${tag.articleCount ? ` (${tag.articleCount} 篇文章)` : ''}`"
      :class="`tag-cloud__item tag-cloud__item--${getTagSize(tag.articleCount)}`"
      :style="{
        opacity: getTagOpacity(tag.articleCount),
        color: generateTagColor()
      }"
    >
      {{ tag.name }}
      <span v-if="tag.articleCount !== undefined" class="tag-cloud__count">({{ tag.articleCount }})</span>
    </a>
  </div>
</template>

<style scoped>
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: flex-start;
}

.tag-cloud__item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 20px;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
}

/* 基础样式 */
.tag-cloud__item--sm {
  font-size: 12px;
  font-weight: 500;
  padding: 3px 8px;
}

.tag-cloud__item--base {
  font-size: 14px;
  font-weight: 500;
}

.tag-cloud__item--md {
  font-size: 16px;
  font-weight: 600;
  padding: 5px 14px;
}

.tag-cloud__item--lg {
  font-size: 18px;
  font-weight: 600;
  padding: 6px 16px;
}

.tag-cloud__item--xl {
  font-size: 20px;
  font-weight: 700;
  padding: 7px 18px;
}

.tag-cloud__count {
  font-size: 11px;
  font-weight: 400;
  opacity: 0.9;
  background: rgba(255, 255, 255, 0.2);
  padding: 1px 6px;
  border-radius: 10px;
  backdrop-filter: blur(1px);
}

/* 调整不同大小下计数的字体和样式 */
.tag-cloud__item--sm .tag-cloud__count {
  font-size: 10px;
  padding: 1px 4px;
}

.tag-cloud__item--md .tag-cloud__count {
  font-size: 11px;
  padding: 2px 6px;
}

.tag-cloud__item--lg .tag-cloud__count {
  font-size: 12px;
  padding: 2px 7px;
}

.tag-cloud__item--xl .tag-cloud__count {
  font-size: 13px;
  padding: 2px 8px;
}

/* 悬停效果 */
.tag-cloud__item:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4px);
}

.tag-cloud__item:hover .tag-cloud__count {
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(2px);
}

/* 暗色模式适配 */
body.dark .tag-cloud__item {
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(4px);
}

body.dark .tag-cloud__count {
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(2px);
}

body.dark .tag-cloud__item:hover {
  background: rgba(0, 0, 0, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

body.dark .tag-cloud__item:hover .tag-cloud__count {
  background: rgba(0, 0, 0, 0.4);
}

/* 添加光晕效果 */
.tag-cloud__item::before {
  content: '';
  position: absolute;
  inset: -1px;
  padding: 1px;
  border-radius: inherit;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  mask-composite: subtract;
  -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tag-cloud__item:hover::before {
  opacity: 1;
}
</style>
