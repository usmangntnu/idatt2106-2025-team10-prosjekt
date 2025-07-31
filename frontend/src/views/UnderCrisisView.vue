<template>
  <component :is="currentComponent" />
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'
import UnderDashboard from '@/components/crisis/UnderDashboard.vue'
import UnderPage from '@/components/crisis/UnderPage.vue'

const userStore = useUserStore()

const currentComponent = computed(() =>
  userStore.isAuthenticated ? UnderDashboard : UnderPage
)
</script>
