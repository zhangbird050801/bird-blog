<script setup lang="ts">
const props = withDefaults(defineProps<{
  title: string
  subtitle?: string
}>(), {
  subtitle: '',
})
</script>

<template>
  <article class="sidebar-panel lg-glass">
    <header>
      <h2>{{ props.title }}</h2>
      <p v-if="props.subtitle">{{ props.subtitle }}</p>
    </header>
    <div class="sidebar-panel__content">
      <slot />
    </div>
  </article>
</template>

<style scoped>
.sidebar-panel {
  padding: clamp(20px, 2.4vw, 28px);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.sidebar-panel header h2 {
  margin: 0 0 4px;
  font-size: 18px;
}

.sidebar-panel header p {
  margin: 0;
  color: var(--lg-text-secondary);
  font-size: 14px;
}
</style>
