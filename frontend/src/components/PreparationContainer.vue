<template>
  <div
    class="flex flex-col items-center justify-center p-5 bg-[#F3EFE8] rounded-[15px] border border-black w-[200px] h-[150px] hover:bg-neutral-200 transition"
  >
    <slot />
    <p class="mt-4 text-base font-medium text-center">{{ label }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
}>()
</script>
