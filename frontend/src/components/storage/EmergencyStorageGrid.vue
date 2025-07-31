<script setup lang="ts">
import type { Category, StorageItem } from '@/types/types'
import EmergencyStorageItem from '@/components/storage/EmergencyStorageItem.vue'
import { computed } from 'vue'

const props = defineProps<{
  categories: Category[]
  storageItems: StorageItem[]
}>()

const emit = defineEmits<{ updated: [] }>()

const getItemsByCategory = (categoryId: number) => {
  // First filter items by category
  const categoryItems = props.storageItems.filter(
    (item) => item.categoryId === categoryId
  )

  // Then sort them by stockCompletionPercentage (lowest first)
  // Handle null/undefined values by treating them as 0
  return categoryItems.sort((a, b) => {
    const percentageA = a.stockCompletionPercentage ?? 0
    const percentageB = b.stockCompletionPercentage ?? 0
    return percentageA - percentageB
  })
}

const sortedItemsByCategory = computed(() => {
  const result = new Map()

  props.categories.forEach((category) => {
    result.set(category.id, getItemsByCategory(category.id))
  })

  return result
})

console.log()
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
    <div
      v-for="cat in props.categories"
      :key="cat.id"
      class="bg-[#FFFCF8] border border-gray-300 rounded-xl shadow-md p-4 text-center"
    >
      <h3
        class="text-lg font-semibold flex items-center justify-center gap-2 mb-4"
      >
        {{ cat.name }}
      </h3>

      <div class="space-y-2">
        <EmergencyStorageItem
          v-for="item in sortedItemsByCategory.get(cat.id)"
          :key="item.id"
          :item="item"
          @updated="emit('updated')"
        />
      </div>
    </div>
  </div>
</template>
