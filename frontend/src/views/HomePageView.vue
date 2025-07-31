<template>
  <div class="min-h-screen">
    <component :is="currentComponent" />
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import MainDashboard from '@/components/home/MainDashboard.vue'
import HomePage from '@/components/home/HomePage.vue'
import { computed } from 'vue'

const userStore = useUserStore()

// komponent velges basert pa bruker
const currentComponent = computed(() => {
  return userStore.isAuthenticated ? MainDashboard : HomePage
})
</script>
