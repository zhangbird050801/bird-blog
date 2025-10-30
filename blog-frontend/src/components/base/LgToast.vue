<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'

const props = withDefaults(defineProps<{
  show: boolean
  message: string
  type?: 'success' | 'error' | 'info' | 'warning'
  duration?: number
}>(), {
  type: 'success',
  duration: 3000
})

const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
}>()

const visible = ref(false)
const isLeaving = ref(false)

watch(() => props.show, (newVal) => {
  if (newVal) {
    visible.value = true
    isLeaving.value = false
    
    if (props.duration > 0) {
      setTimeout(() => {
        close()
      }, props.duration)
    }
  }
})

function close() {
  isLeaving.value = true
  setTimeout(() => {
    visible.value = false
    emit('update:show', false)
  }, 300)
}

onMounted(() => {
  if (props.show) {
    visible.value = true
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition name="toast">
      <div 
        v-if="visible" 
        class="toast-container"
        :class="{ 'toast-leaving': isLeaving }"
      >
        <div class="toast" :class="`toast--${type}`">
          <div class="toast-icon">
            <!-- Success Icon -->
            <svg v-if="type === 'success'" viewBox="0 0 24 24" width="24" height="24">
              <path 
                fill="currentColor" 
                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"
              />
            </svg>
            
            <!-- Error Icon -->
            <svg v-else-if="type === 'error'" viewBox="0 0 24 24" width="24" height="24">
              <path 
                fill="currentColor" 
                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"
              />
            </svg>
            
            <!-- Info Icon -->
            <svg v-else-if="type === 'info'" viewBox="0 0 24 24" width="24" height="24">
              <path 
                fill="currentColor" 
                d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"
              />
            </svg>
            
            <!-- Warning Icon -->
            <svg v-else-if="type === 'warning'" viewBox="0 0 24 24" width="24" height="24">
              <path 
                fill="currentColor" 
                d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"
              />
            </svg>
          </div>
          
          <div class="toast-message">{{ message }}</div>
          
          <button class="toast-close" @click="close" aria-label="关闭">
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path 
                fill="currentColor" 
                d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
              />
            </svg>
          </button>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 320px;
  max-width: 500px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px) saturate(140%);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.05);
  pointer-events: auto;
  animation: slideDown 0.3s ease;
}

body.dark .toast {
  background: rgba(30, 30, 46, 0.95);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1);
}

.toast-leaving {
  animation: slideUp 0.3s ease forwards;
}

.toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast--success .toast-icon {
  color: #22c55e;
}

.toast--error .toast-icon {
  color: #ef4444;
}

.toast--info .toast-icon {
  color: #3b82f6;
}

.toast--warning .toast-icon {
  color: #f59e0b;
}

.toast-message {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: var(--lg-text-primary);
  line-height: 1.5;
}

.toast-close {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: var(--lg-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.toast-close:hover {
  background: rgba(0, 0, 0, 0.1);
  color: var(--lg-text-primary);
}

body.dark .toast-close:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* Animations */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-20px);
  }
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
