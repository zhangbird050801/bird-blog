<script setup lang="ts">
const props = withDefaults(defineProps<{
  label: string
  size?: 'sm' | 'md'
}>(), {
  size: 'md',
})
</script>

<template>
  <button class="lg-icon-button" type="button" :aria-label="props.label">
    <slot />
  </button>
</template>

<style scoped>

.lg-icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--lg-radius-full);
  background: rgba(255, 255, 255, 0.28);
  color: var(--lg-text-primary);
  padding: 10px;
  min-width: 0;
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: background var(--lg-transition), color var(--lg-transition);
  cursor: pointer;
}

.lg-icon-button:focus-visible {
  outline: none;
  box-shadow: var(--lg-focus-ring);
}

body.dark .lg-icon-button {
  background: rgba(15, 23, 42, 0.45);
  color: var(--lg-text-primary);
  border-color: rgba(148, 163, 184, 0.32);
}

template[shadowrootmode="open"] .lg-icon-button {
  font: inherit;
}
</style>
