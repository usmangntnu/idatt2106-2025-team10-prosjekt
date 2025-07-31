<template>
  <div>
    <label for="address" class="block text-sm font-medium text-gray-700 mb-1"
      >Adresse</label
    >
    <multiselect
      id="address"
      v-model="selected"
      :options="suggestions"
      :custom-label="customLabel"
      :searchable="true"
      :loading="loading"
      :clear-on-select="false"
      :close-on-select="true"
      :show-no-results="false"
      placeholder="Start å skrive for å søke…"
      @search-change="onSearch"
      @select="onSelect"
    >
      <template #option="{ option }">
        <div class="flex justify-between">
          <span>{{ option.text }}</span>
          <span class="text-gray-500 text-sm">{{ option.city }}</span>
        </div>
      </template>

      <template #noOptions>
        <span class="text-gray-500 italic">Skriv minst 3 tegn for å søke…</span>
      </template>
    </multiselect>

    <p class="mt-2 text-xs text-gray-500">
      Adressedata levert av
      <a href="https://www.kartverket.no" target="_blank" class="underline">
        Kartverket </a
      >, lisensiert under
      <a
        href="https://creativecommons.org/licenses/by/4.0/"
        target="_blank"
        class="underline"
      >
        CC BY 4.0 </a
      >.
    </p>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, watch } from 'vue'
import Multiselect from 'vue-multiselect'
import type { AddressSuggestion } from '@/types/types.ts'

type Suggestion = AddressSuggestion

const props = defineProps<{
  modelValue: Suggestion | null
  suggestions: Suggestion[]
  loading: boolean
}>()

const emits = defineEmits<{
  (e: 'update:modelValue', value: Suggestion | null): void
  (e: 'search', query: string): void
}>()

const selected = ref<Suggestion | null>(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    selected.value = val
  }
)

// Function with explicit parameter type prevents 'any' error
function customLabel(option: Suggestion): string {
  return option.text
}

function onSearch(query: string) {
  emits('search', query)
}

function onSelect(option: Suggestion) {
  selected.value = option
  emits('update:modelValue', option)
}
</script>

<style>
@import 'vue-multiselect/dist/vue-multiselect.min.css';
</style>
