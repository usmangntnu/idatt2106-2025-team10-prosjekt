<template>
  <div
    @click="$emit('button-click')"
    class="relative w-[385px] h-[424px] bg-white border border-black rounded-[25px] box-border cursor-pointer hover:opacity-80"
  >
    <h2
      class="absolute w-[327px] h-[48px] left-[29px] top-[74px] font-sans font-semibold text-[40px] leading-[48px] text-[#333333]"
    >
      {{ title }}
    </h2>
    <p
      class="absolute w-[317px] h-[58px] left-[34px] top-[189px] font-sans font-semibold text-[24px] leading-[29px] text-center text-[#333333]"
    >
      {{ description }}
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description: string
  buttonText?: string
}>()

defineEmits<{
  (e: 'button-click'): void
}>()
</script>
