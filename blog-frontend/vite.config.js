import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },

  server: {
    host: '127.0.0.1', port: 3000,
    proxy: {
      // 获取路径中包含 /api 的请求
      '/api': {
        // 后台服务所在源
        target: 'http://127.0.0.1:8000',
        changeOrigin: true,
        rewrite: (path)=>path.replace(/^\/api/,"")
      }
    }
  }
})

