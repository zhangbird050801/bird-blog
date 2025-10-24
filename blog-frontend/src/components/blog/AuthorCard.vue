<script setup lang="ts">
import LgAvatar from '@/components/base/LgAvatar.vue'
</script>

<template>
  <div class="author-card">
    <div class="author-card__header">
      <LgAvatar
        src="https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=240&q=80"
        alt="站长头像"
        :size="64"
      />
      <div>
        <h3>Birdy</h3>
        <p>BirdBlog 设计实验者，专注视觉与交互融合。</p>
      </div>
    </div>
    <ul class="author-card__meta">
      <li>
        <span>近期项目</span>
        <strong>Fluid UI 框架</strong>
      </li>
      <li>
        <span>所在地</span>
        <strong>上海</strong>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.author-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.author-card__header {
  display: flex;
  gap: 16px;
  align-items: center;
}

.author-card__header h3 {
  margin: 0 0 4px;
}

.author-card__header p {
  margin: 0;
  font-size: 14px;
  color: var(--lg-text-secondary);
}

.author-card__meta {
  display: grid;
  gap: 12px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.author-card__meta span {
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.author-card__meta strong {
  font-size: 15px;
}
</style>
