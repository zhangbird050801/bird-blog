<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const progress = ref(0)

const updateProgress = () => {
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight
  const scrollTop = window.scrollY
  
  const scrollableHeight = documentHeight - windowHeight
  if (scrollableHeight > 0) {
    progress.value = Math.min(100, (scrollTop / scrollableHeight) * 100)
  }
}

onMounted(() => {
  window.addEventListener('scroll', updateProgress)
  updateProgress()
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateProgress)
})
</script>

<template>
  <div class="reading-progress">
    <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
  </div>
</template>

<style scoped>
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(148, 163, 184, 0.2);
  z-index: 1000;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--sg-primary), var(--sg-secondary));
  transition: width 0.1s ease;
  box-shadow: 0 0 10px var(--sg-primary);
}
</style>
