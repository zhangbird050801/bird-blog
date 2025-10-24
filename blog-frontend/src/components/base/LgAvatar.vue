<script setup lang="ts">
const props = withDefaults(defineProps<{
  src: string
  alt: string
  size?: number
}>(), {
  size: 48,
})
</script>

<template>
  <div class="lg-avatar" :style="{ width: `${props.size}px`, height: `${props.size}px` }">
    <img :src="props.src" :alt="props.alt" loading="lazy" />
  </div>
</template>

<style scoped>
.lg-avatar {
  border-radius: var(--lg-radius-full);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.28);
}

.lg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
</style>
