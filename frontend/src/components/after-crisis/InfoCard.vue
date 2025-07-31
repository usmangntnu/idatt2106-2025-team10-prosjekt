<template>
  <div
    class="bg-white text-[#022B3A] rounded-xl p-6 shadow-md flex flex-col justify-between hover:scale-[1.02] transition"
  >
    <div>
      <h2 class="text-xl font-bold mb-1">
        <span>{{ icon }}</span> {{ title }}
      </h2>
      <p class="text-[#1F7A8C]">
        {{ description }}
      </p>
    </div>
    <router-link
      v-if="link && linkText"
      :to="link"
      class="mt-4 inline-block bg-[#1F7A8C] text-white text-center py-2 rounded-lg font-semibold hover:brightness-110 transition"
    >
      {{ linkText }}
    </router-link>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon: string
  title: string
  description?: string
  link?: string
  linkText?: string
}>()
</script>
