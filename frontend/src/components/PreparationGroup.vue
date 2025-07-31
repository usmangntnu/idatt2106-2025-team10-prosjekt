<template>
  <div class="border rounded-[25px] bg-white p-10 w-[550px] h-[500px]">
    <h2 class="text-[22px] font-semibold mb-12 pt-4 text-center text-[#333333]">
      Se og oppdater ditt beredskap
    </h2>
    <div class="grid grid-cols-2 gap-x-0 gap-y-8 justify-items-center">
      <PreparationComponent label="Mat og drikke">
        <v-icon name="gi-character" />
      </PreparationComponent>

      <PreparationComponent label="Varme og energi">
        <v-icon name="gi-character" />
      </PreparationComponent>

      <PreparationComponent label="Informasjon">
        <v-icon name="gi-character" />
      </PreparationComponent>

      <PreparationComponent label="Førstehjelp">
        <v-icon name="gi-character" />
      </PreparationComponent>
    </div>
  </div>
</template>

<script setup lang="ts">
import PreparationComponent from '@/components/PreparationContainer.vue'
</script>
