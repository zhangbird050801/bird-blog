import { ref } from 'vue'

/**
 * Toast 消息提示组合式函数
 * 与 LgToast 组件配合使用
 */
export function useToast() {
  const toastMessage = ref('')
  const toastType = ref<'success' | 'error' | 'warning' | 'info'>('success')
  const showToastRef = ref(false)

  /**
   * 显示消息提示
   * @param message 消息内容
   * @param type 消息类型
   * @param duration 显示时长（毫秒），默认3000
   */
  const show = (message: string, type: 'success' | 'error' | 'warning' | 'info' = 'success', duration = 3000) => {
    toastMessage.value = message
    toastType.value = type
    showToastRef.value = true

    // 自动隐藏
    setTimeout(() => {
      showToastRef.value = false
    }, duration)
  }

  return {
    show,
    toastMessage,
    toastType,
    showToast: showToastRef
  }
}