import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server:{
    host: '10.164.130.137',
    proxy: {
      '/api': {
        target: 'http://10.164.130.137:8089',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
