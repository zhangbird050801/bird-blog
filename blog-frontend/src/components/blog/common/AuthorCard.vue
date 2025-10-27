<script setup lang="ts">
import LgAvatar from '@/components/base/LgAvatar.vue'

// 社交链接配置
const socialLinks = [
  { name: 'GitHub', icon: 'github', url: 'https://github.com' },
  { name: 'Twitter', icon: 'twitter', url: 'https://twitter.com' },
  { name: '微博', icon: 'weibo', url: 'https://weibo.com' },
  { name: 'Email', icon: 'email', url: 'mailto:contact@birdblog.com' },
]
</script>

<template>
  <div class="author-card">
    <div class="author-card__header">
      <img 
        src="https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=800&h=200&fit=crop" 
        alt="作者背景" 
        class="author-banner"
      />
      <LgAvatar
        src="https://avatars.githubusercontent.com/u/78243168?v=4"
        alt="站长头像"
        :size="80"
        class="author-avatar"
      />
    </div>
    
    <!-- 作者信息 -->
    <div class="author-card__body">
      <h3 class="author-name">Birdy</h3>
      <p class="author-bio">BirdBlog 设计实验者，专注视觉与交互融合</p>
      
      <!-- 社交链接 -->
      <div class="social-links">
        <a 
          v-for="link in socialLinks" 
          :key="link.name"
          :href="link.url"
          :title="link.name"
          target="_blank"
          rel="noopener noreferrer"
          class="social-link"
        >
          <span class="social-icon" :data-icon="link.icon">
            {{ link.icon.charAt(0).toUpperCase() }}
          </span>
        </a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.author-card {
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.author-card:hover {
  box-shadow: 0 8px 24px rgba(80, 204, 213, 0.3), 0 0 0 2px rgba(80, 204, 213, 0.5) !important;
}

.author-card__header {
  position: relative;
  text-align: center;
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
  overflow: visible;
  margin: 0;
  padding: 0;
  line-height: 0;
}

.author-banner {
  width: 100%;
  min-height: 120px;
  object-fit: cover;
  display: block;
  margin: 0;
  padding: 0;
  border-radius: var(--lg-radius-sm) var(--lg-radius-sm) 0 0;
}

.author-avatar {
  position: absolute;
  bottom: -40px;
  left: 50%;
  transform: translateX(-50%);
  border: 4px solid var(--sg-card-bg);
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.author-card__body {
  padding: 52px 28px 24px;
  text-align: center;
}

.author-name {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
  color: var(--lg-text-primary);
}

.author-bio {
  margin: 0 0 16px;
  font-size: 15px;
  color: var(--lg-text-secondary);
  line-height: 1.5;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  color: rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease-in-out;
  text-decoration: none;
  font-size: 18px;
  font-weight: 600;
}

.social-link:hover {
  background: #f4692c;
  color: #fff;
  transform: scale(1.1);
}

body.dark .social-link {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
}

body.dark .social-link:hover {
  background: #f4692c;
  color: #fff;
}

.social-icon {
  font-style: normal;
}
</style>
