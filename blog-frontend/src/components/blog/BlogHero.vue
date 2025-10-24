<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Props {
  backgroundImage?: string
  title?: string
  subtitle?: string
}

const props = withDefaults(defineProps<Props>(), {
  backgroundImage: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=650&fit=crop',
  title: 'BirdBlog',
  subtitle: '探索技术，分享知识'
})

// 打字机效果
const displayedText = ref('')
const fullText = props.title
let currentIndex = 0

function typeWriter() {
  if (currentIndex < fullText.length) {
    displayedText.value += fullText.charAt(currentIndex)
    currentIndex++
    setTimeout(typeWriter, 100)
  }
}

onMounted(() => {
  setTimeout(typeWriter, 500)
})
</script>

<template>
  <div 
    class="blog-hero" 
    :style="{ backgroundImage: `url(${backgroundImage})` }"
  >
    <div class="hero-title">
      <h1 class="typing-text">
        <span>{{ displayedText }}</span>
        <span class="cursor">|</span>
      </h1>
      <p class="hero-subtitle">{{ subtitle }}</p>
    </div>
  </div>
</template>

<style scoped>
.blog-hero {
  height: 500px;
  position: relative;
  width: 100%;
  background-size: cover;
  background-position: center 50%;
  background-repeat: no-repeat;
  margin-bottom: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-title {
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: 'HYTMR', 'Sigmar One', Arial, sans-serif;
}

.typing-text {
  font-size: clamp(48px, 8vw, 80px);
  font-weight: 200;
  transform: matrix(1, 0, 0, 1, 0, 0);
  -webkit-transform: matrix(1, 0, 0, 1, 0, 0);
  text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
  margin: 0 0 20px;
}

.hero-subtitle {
  font-size: clamp(18px, 3vw, 24px);
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 光标闪烁动画 */
.cursor {
  font-family: Arial, sans-serif;
  font-size: 1em;
  display: inline-block;
  vertical-align: baseline;
  opacity: 1;
  text-shadow: 1px 1px 0 #ff3f1a, -1px -1px 0 #00a7e0;
  animation: blink 500ms infinite;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

/* 淡入上滑动画 */
@keyframes fadeInUp {
  0% {
    transform: translateY(90px);
    -webkit-transform: translateY(90px);
  }
  80% {
    transform: translateY(5px);
    -webkit-transform: translateY(5px);
  }
  90% {
    transform: translateY(-5px);
    -webkit-transform: translateY(-5px);
  }
  100% {
    transform: translateY(0);
    -webkit-transform: translateY(0);
  }
}

@-webkit-keyframes fadeInUp {
  0% {
    -webkit-transform: translateY(90px);
    transform: translateY(90px);
  }
  80% {
    -webkit-transform: translateY(5px);
    transform: translateY(5px);
  }
  90% {
    -webkit-transform: translateY(-5px);
    transform: translateY(-5px);
  }
  100% {
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
}

/* 移动端适配 */
@media screen and (max-width: 700px) {
  .blog-hero {
    height: 450px !important;
    margin-bottom: -140px !important;
  }
  
  .hero-title {
    font-size: 50px !important;
    top: 120px;
  }
  
  .hero-profile {
    top: 350px;
    width: 90%;
  }
}

@media screen and (max-width: 500px) {
  .hero-title {
    font-size: 40px !important;
  }
}
</style>
