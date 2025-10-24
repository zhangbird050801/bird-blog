import { ref } from 'vue'

// 统一加载与错误状态管理
export function useAsyncData<T>() {
  const data = ref<T | null>(null)
  const loading = ref(false)
  const error = ref<Error | null>(null)

  async function run(task: () => Promise<T>) {
    loading.value = true
    error.value = null
    try {
      data.value = await task()
    } catch (err) {
      error.value = err instanceof Error ? err : new Error('未知错误')
    } finally {
      loading.value = false
    }
    return data.value
  }

  return { data, loading, error, run }
}
