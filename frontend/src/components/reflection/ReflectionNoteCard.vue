<template>
  <div
    class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition cursor-pointer"
    @click="$emit('click')"
  >
    <!-- Card Header -->
    <div class="p-4 bg-gray-50 border-b">
      <h3 class="font-semibold text-lg truncate">{{ note.title }}</h3>
      <div class="flex justify-between items-center mt-1 text-sm text-gray-500">
        <div class="flex items-center">
          <span>{{ formatDate(note.createdAt) }}</span>
        </div>
        <div class="flex items-center">
          <span>{{ note.creator.username }}</span>
        </div>
      </div>
    </div>

    <!-- Card Content -->
    <div class="p-4">
      <p class="text-gray-600 line-clamp-3">{{ note.content }}</p>
    </div>

    <!-- Card Footer -->
    <div
      class="px-4 py-3 bg-gray-50 border-t flex justify-between items-center"
    >
      <!-- Visibility Badge -->
      <div>
        <span
          :class="[
            'px-2 py-1 text-xs font-medium rounded-full',
            note.visibility === 'PUBLIC'
              ? 'bg-green-100 text-green-800'
              : note.visibility === 'HOUSEHOLD'
                ? 'bg-blue-100 text-blue-800'
                : 'bg-gray-100 text-gray-800',
          ]"
        >
          {{ visibilityLabel }}
        </span>
      </div>

      <!-- Read More -->
      <div class="text-sm text-[#1F7A8C] font-medium">Les mer</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ReflectionNote } from '@/types/types.ts'

const props = defineProps<{
  note: ReflectionNote
}>()

// Format date
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('no-NO', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(date)
}

// Compute visibility label
const visibilityLabel = computed(() => {
  switch (props.note.visibility) {
    case 'PUBLIC':
      return 'Offentlig'
    case 'HOUSEHOLD':
      return 'Husstand'
    case 'PRIVATE':
      return 'Privat'
    default:
      return props.note.visibility
  }
})
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
