<script setup lang="ts">
const props = withDefaults(defineProps<{
  tone?: 'accent' | 'neutral'
}>(), {
  tone: 'accent',
})
</script>

<template>
  <span class="lg-badge" :class="[`lg-badge--${props.tone}`]">
    <slot />
  </span>
</template>

<style scoped>
.lg-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--lg-radius-full);
  padding: 4px 12px;
  font-size: 12px;
  letter-spacing: 0.02em;
  font-weight: 600;
  text-transform: uppercase;
  background: rgba(79, 124, 255, 0.12);
  color: var(--lg-accent);
}

.lg-badge--neutral {
  background: rgba(148, 163, 184, 0.18);
  color: var(--lg-text-secondary);
}

body.dark .lg-badge--neutral {
  background: rgba(148, 163, 184, 0.28);
  color: var(--lg-text-inverse);
}
</style>
