<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  variant?: 'primary' | 'ghost' | 'glass'
  size?: 'sm' | 'md' | 'lg'
  as?: 'button' | 'a'
  href?: string
  type?: 'button' | 'submit' | 'reset'
  loading?: boolean
}>(), {
  variant: 'primary',
  size: 'md',
  as: 'button',
  type: 'button',
  loading: false,
})

const classes = computed(() => [
  'lg-button',
  `lg-button--${props.variant}`,
  `lg-button--${props.size}`,
  props.loading ? 'lg-button--loading' : '',
])
</script>

<template>
  <component
    :is="props.as"
    :href="props.as === 'a' ? props.href : undefined"
    :type="props.as === 'button' ? props.type : undefined"
    class="lg-button"
    :class="classes"
    :aria-busy="props.loading ? 'true' : undefined"
  >
    <span class="lg-button__content">
      <slot />
    </span>
  </component>
</template>

<style scoped>
.lg-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: var(--lg-radius-full);
  border: 1px solid transparent;
  font-weight: 600;
  letter-spacing: 0.01em;
  transition: background var(--lg-transition), color var(--lg-transition);
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  min-width: 0;
}

.lg-button:focus-visible {
  outline: none;
  box-shadow: var(--lg-focus-ring);
}

.lg-button--primary {
  background: linear-gradient(135deg, rgba(79, 124, 255, 0.92), rgba(122, 162, 255, 0.82));
  color: var(--lg-text-inverse);
}

.lg-button--ghost {
  background: transparent;
  border-color: rgba(79, 124, 255, 0.35);
  color: var(--lg-accent);
}

.lg-button--glass {
  background: rgba(255, 255, 255, 0.16);
  border-color: rgba(255, 255, 255, 0.32);
  color: var(--lg-text-primary);
  backdrop-filter: blur(12px);
}

body.dark .lg-button--glass {
  background: rgba(15, 23, 42, 0.42);
  border-color: rgba(148, 163, 184, 0.32);
  color: var(--lg-text-primary);
}

.lg-button--sm {
  padding: 6px 14px;
  font-size: 13px;
}

.lg-button--md {
  padding: 10px 20px;
  font-size: 14px;
}

.lg-button--lg {
  padding: 14px 26px;
  font-size: 15px;
}

.lg-button--loading {
  opacity: 0.65;
  pointer-events: none;
}

.lg-button__content {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
</style>
