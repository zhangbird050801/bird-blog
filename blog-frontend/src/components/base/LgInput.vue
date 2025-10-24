<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  placeholder?: string
  type?: string
  icon?: boolean
  ariaLabel?: string
}>(), {
  placeholder: '',
  type: 'text',
  icon: false,
  ariaLabel: undefined,
})

const emits = defineEmits<{
  'update:modelValue': [value: string]
  submit: []
}>()

const classes = computed(() => [
  'lg-input',
  props.icon ? 'lg-input--icon' : '',
])

function onInput(event: Event) {
  const target = event.target as HTMLInputElement
  emits('update:modelValue', target.value)
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    emits('submit')
  }
}
</script>

<template>
  <label class="lg-input__wrapper">
    <span class="visually-hidden">{{ props.ariaLabel }}</span>
    <input
      :type="props.type"
      :placeholder="props.placeholder"
      :value="props.modelValue"
      :aria-label="props.ariaLabel"
      :class="classes"
      @input="onInput"
      @keydown="onKeydown"
    />
    <slot name="icon" />
  </label>
</template>

<style scoped>
.lg-input__wrapper {
  position: relative;
  width: 100%;
  display: inline-flex;
  align-items: center;
}

.lg-input {
  width: 100%;
  border-radius: var(--lg-radius-full);
  border: 1px solid rgba(79, 124, 255, 0.28);
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(16px) saturate(145%);
  padding: 12px 18px;
  font-size: 14px;
  color: var(--lg-text-primary);
  transition: all var(--lg-transition);
}

.lg-input:focus {
  outline: none;
  border-color: rgba(79, 124, 255, 0.45);
  box-shadow: var(--lg-focus-ring);
}

.lg-input--icon {
  padding-left: 44px;
}

.lg-input__wrapper :deep(svg) {
  position: absolute;
  left: 16px;
  width: 18px;
  height: 18px;
  color: var(--lg-accent);
}

body.dark .lg-input {
  background: rgba(15, 23, 42, 0.6);
  border-color: rgba(148, 163, 184, 0.32);
  color: var(--lg-text-primary);
}
</style>
