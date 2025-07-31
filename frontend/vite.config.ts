import { sentryVitePlugin } from "@sentry/vite-plugin";
import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import path from "node:path";



export default defineConfig({
  define: {
    global: 'window',
  },
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    tailwindcss(),
    sentryVitePlugin({
      org: "aryan-2i",
      project: "krisefikser",
    }),
  ],
  optimizeDeps: {
    include: ['leaflet', 'leaflet-draw'],
  },
  test: {
    globals: true,
    environment: 'jsdom',
    css: false,
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      leaflet: path.resolve(__dirname, 'node_modules/leaflet')
    },
  },
  css: {
    preprocessorOptions: {
      sass: {
        additionalData: `@use "vuetify/settings" with ($utilities: true);`
      }
    }
  },
  build: {
    sourcemap: true
  }
})
