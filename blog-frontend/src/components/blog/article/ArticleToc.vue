<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

const props = defineProps<{
  content: string
}>()

interface TocItem {
  id: string
  text: string
  level: number
}

const activeId = ref('')
const tocItems = ref<TocItem[]>([])

// 从HTML内容中提取目录
const extractToc = () => {
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = props.content
  
  const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6')
  const items: TocItem[] = []
  
  headings.forEach((heading, index) => {
    const level = parseInt(heading.tagName.substring(1))
    const text = heading.textContent || ''
    const id = `heading-${index}`
    
    // 为文章中的标题添加ID（实际渲染时需要）
    heading.setAttribute('id', id)
    
    items.push({ id, text, level })
  })
  
  tocItems.value = items
}

// 监听滚动，更新当前激活的标题
const handleScroll = () => {
  const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6')
  const scrollTop = window.scrollY + 100
  
  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i] as HTMLElement
    if (heading.offsetTop <= scrollTop) {
      activeId.value = heading.id
      break
    }
  }
}

// 平滑滚动到指定标题
const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

onMounted(() => {
  extractToc()
  window.addEventListener('scroll', handleScroll)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 计算缩进样式
const getIndentClass = (level: number) => {
  return `toc-item-level-${level}`
}
</script>

<template>
  <div class="article-toc" v-if="tocItems.length > 0">
    <div class="toc-header">
      <i class="fa fa-list-ul"></i>
      <span>目录</span>
    </div>
    <nav class="toc-content">
      <a
        v-for="item in tocItems"
        :key="item.id"
        :class="['toc-item', getIndentClass(item.level), { active: activeId === item.id }]"
        @click.prevent="scrollToHeading(item.id)"
        :href="`#${item.id}`"
      >
        {{ item.text }}
      </a>
    </nav>
  </div>
</template>

<style scoped>
.article-toc {
  position: sticky;
  top: 100px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-height: calc(100vh - 150px);
  overflow-y: auto;
  transition: all 0.3s ease;
}

body.dark .article-toc {
  background: rgba(40, 42, 44, 0.6);
}

.toc-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--lg-text-primary);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--sg-primary);
}

.toc-header i {
  color: var(--sg-primary);
}

.toc-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toc-item {
  display: block;
  padding: 6px 12px;
  color: var(--lg-text-secondary);
  text-decoration: none;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.6;
  transition: all 0.3s ease;
  cursor: pointer;
  border-left: 2px solid transparent;
}

.toc-item:hover {
  background: rgba(151, 223, 253, 0.1);
  color: var(--sg-primary);
  transform: translateX(4px);
}

.toc-item.active {
  background: rgba(151, 223, 253, 0.15);
  color: var(--sg-primary);
  border-left-color: var(--sg-primary);
  font-weight: 600;
}

/* 层级缩进 */
.toc-item-level-1 {
  padding-left: 12px;
  font-weight: 600;
}

.toc-item-level-2 {
  padding-left: 24px;
}

.toc-item-level-3 {
  padding-left: 36px;
  font-size: 13px;
}

.toc-item-level-4 {
  padding-left: 48px;
  font-size: 13px;
}

.toc-item-level-5,
.toc-item-level-6 {
  padding-left: 60px;
  font-size: 12px;
}

/* 滚动条样式 */
.article-toc::-webkit-scrollbar {
  width: 4px;
}

.article-toc::-webkit-scrollbar-thumb {
  background: var(--sg-primary);
  border-radius: 2px;
}

.article-toc::-webkit-scrollbar-track {
  background: rgba(148, 163, 184, 0.1);
}

@media (max-width: 1024px) {
  .article-toc {
    display: none;
  }
}
</style>
