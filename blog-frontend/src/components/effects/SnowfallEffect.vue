<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'

// 雪花样式数组
const snowflakes = ['❄', '❄', '❆', '❅', '✥']

// 雪花生成间隔（毫秒）
const GENERATION_INTERVAL = 300

// 定时器引用
let animationTimer: number | null = null

/**
 * 创建单个雪花元素
 * 设置随机位置、大小、透明度和动画参数
 */
function createSnowflake() {
  const snowflake = document.createElement('span')
  snowflake.classList.add('snowflake')
  
  // 随机选择雪花样式
  const randomIndex = Math.floor(Math.random() * snowflakes.length)
  snowflake.textContent = snowflakes[randomIndex]
  
  // 起始位置：横向随机分布在整个视口宽度
  snowflake.style.left = `${Math.random() * 100}vw`
  snowflake.style.top = `-30px`
  
  // 雪花大小与透明度
  const size = Math.random() * 18 + 10
  snowflake.style.fontSize = `${size}px`
  const opacity = Math.random() * 0.6 + (size > 18 ? 0.4 : 0)
  snowflake.style.setProperty('--opacity', opacity.toString())
  
  // 下落持续时间（秒）
  const fallDuration = Math.random() * 10 + 10
  snowflake.style.animationDuration = `${fallDuration}s, ${fallDuration}s`
  
  // 横向偏移幅度（左右飘动）
  const translateX = Math.random() * 500 - 200
  snowflake.style.setProperty('--translateX', `${translateX}px`)
  
  // 纵向下落距离
  snowflake.style.setProperty('--translateY', `${window.innerHeight}px`)
  
  // 添加到 body
  document.body.appendChild(snowflake)
  
  // 动画结束后移除雪花元素
  setTimeout(() => {
    snowflake.remove()
  }, fallDuration * 1000)
}

/**
 * 雪花动画循环
 * 持续生成新雪花
 */
function snowfallAnimation() {
  createSnowflake()
  animationTimer = window.setTimeout(snowfallAnimation, GENERATION_INTERVAL)
}

// 组件挂载时启动雪花动画
onMounted(() => {
  snowfallAnimation()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (animationTimer !== null) {
    clearTimeout(animationTimer)
  }
  // 清理所有残留的雪花元素
  document.querySelectorAll('.snowflake').forEach(el => el.remove())
})
</script>

<template>
</template>

<style>
.snowflake {
  position: fixed;
  pointer-events: none;
  animation-name: snowflakeFallRotate, snowflakeFadeOut;
  animation-timing-function: linear;
  animation-iteration-count: 1;
  animation-fill-mode: forwards;
  color: white;
  z-index: 99; 
  text-shadow: 0 0 1px rgba(255, 255, 255, 0.8), 0 0 5px rgba(255, 255, 255, 0.8); /* 荧光效果 */
  user-select: none; 
}

/* 雪花下落与旋转动画 */
@keyframes snowflakeFallRotate {
  0% {
    transform: translateY(0) translateX(0) rotate(0);
  }
  70% {
    transform: translateY(var(--translateY)) translateX(var(--translateX)) rotate(720deg);
  }
  100% {
    transform: translateY(var(--translateY)) translateX(var(--translateX)) rotate(800deg);
  }
}

/* 雪花淡出动画 */
@keyframes snowflakeFadeOut {
  0%, 90% {
    opacity: var(--opacity);
  }
  100% {
    opacity: 0;
  }
}
</style>
