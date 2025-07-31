<template>
  <div class="min-h-screen">
    <component :is="currentComponent" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import AfterDashboard from '@/components/after-crisis/AfterDashboard.vue'
import PublicOverview from '@/components/after-crisis/PublicOverview.vue'

const userStore = useUserStore()
const currentComponent = computed(() =>
  userStore.isAuthenticated ? AfterDashboard : PublicOverview
)
</script>
