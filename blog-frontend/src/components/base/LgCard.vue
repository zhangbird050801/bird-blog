<script setup lang="ts">
const props = withDefaults(defineProps<{
  padding?: 'none' | 'xs' | 'sm' | 'md' | 'lg'
  interactive?: boolean
}>(), {
  padding: 'md',
  interactive: false,
})
</script>

<template>
  <section
    class="lg-card lg-glass"
    :class="[
      props.interactive ? 'lg-card--interactive' : '',
      `lg-card--pad-${props.padding}`,
    ]"
  >
    <slot />
  </section>
</template>

<style scoped>
.lg-card {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 18px;
  color: var(--lg-text-primary);
}

.lg-card--interactive {
  cursor: pointer;
}

.lg-card--pad-none {
  padding: 0;
}

.lg-card--pad-xs {
  padding: 12px 18px;
}

.lg-card--pad-sm {
  padding: 18px 24px;
}

.lg-card--pad-md {
  padding: 24px 28px;
}

.lg-card--pad-lg {
  padding: clamp(28px, 4vw, 40px);
}
</style>
