<template>
  <component :is="currentComponent" />
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'
import BeforeCrisisDashboard from '@/components/before-crisis/BeforeCrisisDashboard.vue'
import BeforeCrisisUnderPage from '@/components/before-crisis/BeforeCrisisUnderPage.vue'

const userStore = useUserStore()

const currentComponent = computed(() =>
  userStore.isAuthenticated ? BeforeCrisisDashboard : BeforeCrisisUnderPage
)
</script>
