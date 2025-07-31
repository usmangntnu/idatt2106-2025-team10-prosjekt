<template>
  <div
    class="bg-white border border-[#BFDBF7] rounded-2xl shadow-sm p-6 flex flex-col justify-between h-full hover:shadow-lg hover:-translate-y-1 transition-transform duration-200 ease-in-out"
  >
    <div>
      <h2 class="text-xl font-bold text-[#022B3A] mb-2">{{ title }}</h2>
      <p class="text-[#1F7A8C] text-sm leading-relaxed">{{ description }}</p>
    </div>
    <button
      @click="$emit('click')"
      class="mt-6 w-full py-2.5 rounded-lg font-semibold transition duration-200 hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#1F7A8C] text-white"
      :style="{ backgroundColor: buttonColor || '#1F7A8C' }"
    >
      {{ buttonText }}
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description: string
  buttonText: string
  buttonColor: string
}>()

defineEmits(['click'])
</script>
