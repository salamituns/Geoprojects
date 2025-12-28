import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
// Base path for deployment - can be set via BASE_PATH or VITE_BASE_PATH environment variable
// For local development: leave empty or unset (defaults to '/' for root)
// For Tomcat deployment: set to context path (e.g., '/geological-sample-api/')
// Note: BASE_PATH from Makefile will be passed as VITE_BASE_PATH
const basePath = process.env.VITE_BASE_PATH || process.env.BASE_PATH || '';

export default defineConfig({
  base: basePath || '/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
