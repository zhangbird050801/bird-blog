<script setup lang="ts">
import { onMounted } from 'vue'

/**
 * Ribbon.js 背景组件
 * 确保彩带效果正确加载和显示
 */

onMounted(() => {
  // 延迟加载 ribbon.js，确保 DOM 完全就绪
  setTimeout(() => {
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.setAttribute('size', '90')
    script.setAttribute('alpha', '0.6')
    script.setAttribute('zIndex', '-1')
    script.src = '/ribbon.min.js'
    
    script.onload = () => {
      console.log('Ribbon.js 已加载')
    }
    
    script.onerror = () => {
      console.error('Ribbon.js 加载失败')
    }
    
    document.body.appendChild(script)
  }, 100)
})
</script>

<template>
  <!-- 这个组件不渲染任何内容 -->
</template>
