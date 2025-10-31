<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const emit = defineEmits<{
  select: [emoji: string]
}>()

// 表情分类
const emojiCategories = {
  常用: ['😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚'],
  手势: ['👍', '👎', '👌', '✌️', '🤞', '🤟', '🤘', '👏', '🙌', '👐', '🤲', '🤝', '🙏', '✍️', '💪', '🦾', '🦿', '🦵', '🦶'],
  心情: ['😎', '🤗', '🤔', '🤐', '🤫', '🤭', '😏', '😒', '😞', '😔', '😟', '😕', '🙁', '😣', '😖', '😫', '😩', '🥺', '😢', '😭'],
  动物: ['🐶', '🐱', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', '🦁', '🐮', '🐷', '🐽', '🐸', '🐵', '🙈', '🙉', '🙊', '🐒'],
  食物: ['🍎', '🍊', '🍋', '🍌', '🍉', '🍇', '🍓', '🍈', '🍒', '🍑', '🥭', '🍍', '🥥', '🥝', '🍅', '🍆', '🥑', '🥦', '🥬', '🥒'],
  符号: ['❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔', '❣️', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '☮️']
}

const showPicker = ref(false)
const currentCategory = ref<keyof typeof emojiCategories>('常用')

/**
 * 切换表情选择器显示状态
 */
function togglePicker() {
  showPicker.value = !showPicker.value
}

/**
 * 选择表情
 */
function selectEmoji(emoji: string) {
  emit('select', emoji)
  // 选择后不关闭面板，方便连续选择
}

/**
 * 切换分类
 */
function switchCategory(category: keyof typeof emojiCategories) {
  currentCategory.value = category
}

/**
 * 点击外部关闭面板
 */
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.emoji-picker') && !target.closest('.emoji-picker-trigger')) {
    showPicker.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

defineExpose({
  toggle: togglePicker,
  show: () => showPicker.value = true,
  hide: () => showPicker.value = false
})
</script>

<template>
  <div class="emoji-picker-wrapper">
    <!-- 表情按钮 -->
    <button
      type="button"
      class="emoji-picker-trigger"
      :class="{ 'is-active': showPicker }"
      @click.stop="togglePicker"
      title="插入表情"
    >
      <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
        <line x1="9" y1="9" x2="9.01" y2="9"/>
        <line x1="15" y1="9" x2="15.01" y2="9"/>
      </svg>
    </button>

    <!-- 表情选择面板 -->
    <div v-if="showPicker" class="emoji-picker" @click.stop>
      <!-- 分类标签 -->
      <div class="emoji-picker__categories">
        <button
          v-for="category in Object.keys(emojiCategories)"
          :key="category"
          class="emoji-picker__category"
          :class="{ 'is-active': currentCategory === category }"
          @click="switchCategory(category as keyof typeof emojiCategories)"
        >
          {{ category }}
        </button>
      </div>

      <!-- 表情列表 -->
      <div class="emoji-picker__list">
        <button
          v-for="(emoji, index) in emojiCategories[currentCategory]"
          :key="index"
          class="emoji-picker__item"
          @click="selectEmoji(emoji)"
          :title="emoji"
        >
          {{ emoji }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.emoji-picker-wrapper {
  position: relative;
  display: inline-block;
}

/* 表情按钮 */
.emoji-picker-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  background: transparent;
  border: 1px solid var(--lg-border);
  border-radius: var(--lg-radius-md);
  color: var(--lg-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.emoji-picker-trigger:hover {
  background: var(--lg-surface);
  border-color: var(--sg-primary);
  color: var(--sg-primary);
}

.emoji-picker-trigger.is-active {
  background: var(--lg-surface);
  border-color: var(--sg-primary);
  color: var(--sg-primary);
}

.emoji-picker-trigger svg {
  width: 18px;
  height: 18px;
}

/* 表情选择面板 */
.emoji-picker {
  position: absolute;
  bottom: calc(100% + 8px);
  right: 0;
  width: 300px;
  max-height: 350px;
  background: var(--lg-surface);
  border: 1px solid var(--lg-border);
  border-radius: var(--lg-radius-lg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 分类标签 */
.emoji-picker__categories {
  display: flex;
  gap: 4px;
  padding: 8px;
  border-bottom: 1px solid var(--lg-border);
  overflow-x: auto;
  scrollbar-width: thin;
}

.emoji-picker__category {
  flex-shrink: 0;
  padding: 6px 12px;
  font-size: 13px;
  color: var(--lg-text-secondary);
  background: transparent;
  border: none;
  border-radius: var(--lg-radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.emoji-picker__category:hover {
  background: var(--lg-background);
  color: var(--lg-text-primary);
}

.emoji-picker__category.is-active {
  background: var(--sg-primary);
  color: white;
  font-weight: 500;
}

/* 表情列表 */
.emoji-picker__list {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 4px;
  padding: 12px;
  overflow-y: auto;
  max-height: 250px;
}

.emoji-picker__item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  background: transparent;
  border: none;
  border-radius: var(--lg-radius-sm);
  font-size: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emoji-picker__item:hover {
  background: var(--lg-background);
  transform: scale(1.2);
}

.emoji-picker__list::-webkit-scrollbar {
  width: 6px;
}

.emoji-picker__list::-webkit-scrollbar-track {
  background: transparent;
}

.emoji-picker__list::-webkit-scrollbar-thumb {
  background: var(--lg-border);
  border-radius: 3px;
}

.emoji-picker__list::-webkit-scrollbar-thumb:hover {
  background: var(--lg-text-tertiary);
}

/* 响应式 */
@media (max-width: 768px) {
  .emoji-picker {
    width: 280px;
    max-height: 300px;
  }

  .emoji-picker__list {
    grid-template-columns: repeat(8, 1fr);
  }
}
</style>

