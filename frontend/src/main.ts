;(window as any).global = window
import KriseFikser from './KriseFikser.vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
;(window as any).L = L
import 'leaflet-routing-machine'
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css'
import 'leaflet-draw'
import 'leaflet-draw/dist/leaflet.draw.css'
import { useNotificationStore } from '@/stores/notification'
import { createWebSocket } from '@/utils/socket'
import './assets/main.css'
import { OhVueIcon, addIcons } from 'oh-vue-icons'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as Sentry from '@sentry/vue'
import router from './router'
import {
  GiPointing,
  FcGlobe,
  LaUserCircleSolid,
  FaEye,
  FaEyeSlash,
  CoBell,
} from 'oh-vue-icons/icons'

addIcons(GiPointing, FcGlobe, LaUserCircleSolid, FaEye, FaEyeSlash, CoBell)

import { useUserStore } from '@/stores/user.ts'

const app = createApp(KriseFikser)

app.component('v-icon', OhVueIcon)
app.use(createPinia())
app.use(router)
Sentry.init({
  app,
  dsn: 'https://e140ad144f016966ae0e865856c70071@o4509201711628288.ingest.de.sentry.io/4509201738629200',
  // Setting this option to true will send default PII data to Sentry.
  // For example, automatic IP address collection on events
  sendDefaultPii: true,
})

const userStore = useUserStore()
userStore.fetchCurrentUser().finally(() => {
  const notificationStore = useNotificationStore()
  createWebSocket((event) => {
    if (event.topic === '/topic/notifications') {
      notificationStore.addNotification(event.data)
    } else if (event.topic === '/topic/notifications/delete') {
      const id =
        typeof event.data === 'object' && event.data.id
          ? event.data.id
          : event.data
      notificationStore.removeNotification(id)
    }
  })

  app.mount('#app')
})
