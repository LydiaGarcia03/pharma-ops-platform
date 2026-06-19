import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: { '@': path.resolve(__dirname, './src') },
  },
  server: {
    port: 5173,
    proxy: {
      '/api/identity': {
        target: 'http://localhost:8081',
        rewrite: (p) => p.replace(/^\/api\/identity/, ''),
      },
      '/api/inventory': {
        target: 'http://localhost:8082',
        rewrite: (p) => p.replace(/^\/api\/inventory/, ''),
      },
      '/api/sales': {
        target: 'http://localhost:8083',
        rewrite: (p) => p.replace(/^\/api\/sales/, ''),
      },
    },
  },
})
