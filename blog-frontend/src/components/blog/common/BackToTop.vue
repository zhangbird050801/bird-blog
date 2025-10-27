<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const isVisible = ref(false)
const isScrolling = ref(false)

function handleScroll() {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  isVisible.value = scrollTop > 600 && !isScrolling.value
}

function scrollToTop() {
  isScrolling.value = true
  isVisible.value = false
  
  const scrollStep = -window.scrollY / 30
  const scrollInterval = setInterval(() => {
    if (window.scrollY !== 0) {
      window.scrollBy(0, scrollStep)
    } else {
      clearInterval(scrollInterval)
      isScrolling.value = false
    }
  }, 15)
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <transition name="fade-up">
    <div 
      v-if="isVisible"
      class="back-to-top"
      @click="scrollToTop"
      role="button"
      aria-label="返回顶部"
      tabindex="0"
      @keydown.enter="scrollToTop"
    >
      <svg viewBox="0 0 24 24" width="24" height="24" aria-hidden="true">
        <path 
          fill="currentColor" 
          d="M12 4l-8 8h5v8h6v-8h5z"
        />
      </svg>
      <span class="back-to-top__text">TOP</span>
    </div>
  </transition>
</template>

<style scoped>
.back-to-top {
  position: fixed;
  right: 40px;
  bottom: 80px;
  z-index: 99;
  width: 60px;
  height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: var(--sg-primary);
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.3s ease;
  animation: float 2s ease-in-out infinite;
}

.back-to-top:hover {
  background: var(--sg-dark);
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
}

.back-to-top svg {
  width: 24px;
  height: 24px;
}

.back-to-top__text {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

@keyframes float {
  0% {
    transform: scale(0.95) translateY(10px);
  }
  50% {
    transform: scale(1) translateY(0px);
  }
  100% {
    transform: scale(0.95) translateY(10px);
  }
}

/* 淡入淡出过渡 */
.fade-up-enter-active,
.fade-up-leave-active {
  transition: all 0.3s ease;
}

.fade-up-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .back-to-top {
    right: 20px;
    bottom: 60px;
    width: 50px;
    height: 50px;
  }
  
  .back-to-top svg {
    width: 20px;
    height: 20px;
  }
  
  .back-to-top__text {
    font-size: 9px;
  }
}

@media (max-width: 500px) {
  .back-to-top {
    right: 15px;
    bottom: 50px;
  }
}
</style>
