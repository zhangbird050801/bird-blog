import { ref } from 'vue'

const STORAGE_KEY = 'liquid-glass-theme'
const themeRef = ref<'light' | 'dark'>('light')
let initialized = false

// 根据存储初始化主题
function inferTheme(): 'light' | 'dark' {
  if (typeof window === 'undefined') {
    return 'light'
  }
  const stored = window.localStorage.getItem(STORAGE_KEY)
  if (stored === 'light' || stored === 'dark') {
    return stored
  }
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

function syncBodyClass(theme: 'light' | 'dark') {
  if (typeof document === 'undefined') {
    return
  }
  document.body.classList.toggle('dark', theme === 'dark')
}

export function useTheme() {
  // 切换主题并持久化
  function setTheme(theme: 'light' | 'dark') {
    themeRef.value = theme
    if (typeof window !== 'undefined') {
      window.localStorage.setItem(STORAGE_KEY, theme)
    }
    syncBodyClass(theme)
  }

  function toggleTheme() {
    setTheme(themeRef.value === 'light' ? 'dark' : 'light')
  }

  function applyStoredTheme() {
    if (initialized) return
    const theme = inferTheme()
    themeRef.value = theme
    syncBodyClass(theme)
    initialized = true
  }

  return {
    theme: themeRef,
    setTheme,
    toggleTheme,
    applyStoredTheme,
  }
}
