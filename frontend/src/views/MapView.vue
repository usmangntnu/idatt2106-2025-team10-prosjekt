<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import MapAdminPanel from '@/components/map/MapAdminPanel.vue'
import MapUserPanel from '@/components/map/MapUserPanel.vue'
import Map from '@/components/map/Map.vue'
import { useUserStore } from '@/stores/user.ts'
import type { Map as LeafletMap } from 'leaflet'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

onMounted(() => {
  userStore.fetchCurrentUser()
  console.log('isAdmin', isAdmin.value)
})

const adminPanelRef = ref<InstanceType<typeof MapAdminPanel> | null>(null)
const userPanelRef = ref<InstanceType<typeof MapUserPanel> | null>(null)

function handleMapReady(map: LeafletMap) {
  if (isAdmin.value && adminPanelRef.value?.handleMapReady) {
    adminPanelRef.value.handleMapReady(map)
  } else if (!isAdmin.value && userPanelRef.value?.handleMapReady) {
    userPanelRef.value.handleMapReady(map)
  }
}
</script>

<template>
  <div class="relative w-full h-screen">
    <!-- Map container - absolute positioned to fill the screen -->
    <div class="absolute inset-0 z-10">
      <Map @mapReady="handleMapReady" />
    </div>

    <!-- Panel container - positioned with higher z-index -->
    <div class="absolute inset-0 z-20" style="pointer-events: none">
      <!-- Panels with pointer-events enabled -->
      <div style="pointer-events: auto">
        <MapAdminPanel v-if="isAdmin" ref="adminPanelRef" />
        <MapUserPanel v-else ref="userPanelRef" />
      </div>
    </div>
  </div>
</template>
