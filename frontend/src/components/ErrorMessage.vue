<template>
  <div
    v-if="message"
    class="flex items-start gap-3 p-3 mt-2 border-l-4 border-[#E63946] bg-[#F8D7DA] text-[#721C24] rounded"
  >
    <v-icon name="fa-circle-exclamation" class="text-[#E63946] mt-1" />
    <span class="text-sm font-medium">{{ message }}</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  props: {
    message: {
      type: String,
      default: '',
    },
  },
})
</script>
