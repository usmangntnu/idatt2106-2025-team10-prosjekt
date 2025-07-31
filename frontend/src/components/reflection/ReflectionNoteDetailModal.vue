<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center"
  >
    <!-- Modal backdrop -->
    <div class="fixed inset-0 bg-black opacity-50" @click="closeModal"></div>

    <!-- Modal content -->
    <div
      class="bg-white rounded-lg shadow-xl w-full max-w-2xl mx-4 z-10 overflow-hidden"
    >
      <!-- Modal header -->
      <div
        class="bg-gray-50 px-6 py-4 border-b flex justify-between items-center"
      >
        <div>
          <h2 class="text-xl font-bold">{{ note.title }}</h2>
          <div class="flex items-center gap-4 text-sm text-gray-500 mt-1">
            <div class="flex items-center gap-1">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                />
              </svg>
              <span>{{ note.creator.username }}</span>
            </div>
            <div class="flex items-center gap-1">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
              <span>{{ formatDate(note.createdAt) }}</span>
            </div>
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
          </div>
        </div>
        <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-6 w-6"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>

      <!-- Modal body -->
      <div class="p-6">
        <div class="prose max-w-none">
          <p class="whitespace-pre-wrap">{{ note.content }}</p>
        </div>
      </div>

      <!-- Modal footer -->
      <div class="bg-gray-50 px-6 py-4 border-t flex justify-between">
        <!-- Delete button (only shown for own notes) -->
        <button
          v-if="isOwnNote"
          @click="confirmDelete"
          class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition flex items-center"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5 mr-2"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fill-rule="evenodd"
              d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
              clip-rule="evenodd"
            />
          </svg>
          Slett notat
        </button>
        <!-- Spacer div when there's no delete button -->
        <div v-else></div>

        <button
          @click="closeModal"
          class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition"
        >
          Lukk
        </button>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div
      v-if="showDeleteConfirm"
      class="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-60"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Bekreft sletting</h3>
        <p class="text-gray-600 mb-6">
          Er du sikker på at du vil slette notatet "{{ note.title }}"? Denne
          handlingen kan ikke angres.
        </p>
        <div class="flex justify-end space-x-3">
          <button
            @click="showDeleteConfirm = false"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
          >
            Avbryt
          </button>
          <button
            @click="deleteNote"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
          >
            Slett
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '@/stores/user.ts'
import { deleteReflectionNote } from '@/services/reflectionApi'
import type { ReflectionNote } from '@/types/types.ts'

const props = defineProps<{
  isOpen: boolean
  note: ReflectionNote
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'deleted', id: number): void
}>()

const { currentUser } = useUserStore()
const showDeleteConfirm = ref(false)
const isDeleting = ref(false)

// Check if current user is the note creator
const isOwnNote = computed(() => {
  return props.note.creator.email === currentUser?.email
})

// Format date
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('no-NO', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
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

// Close modal
const closeModal = () => {
  emit('close')
}

// Show delete confirmation dialog
const confirmDelete = () => {
  showDeleteConfirm.value = true
}

// Delete the note
const deleteNote = async () => {
  if (isDeleting.value) return

  try {
    isDeleting.value = true
    await deleteReflectionNote(props.note.id)
    showDeleteConfirm.value = false
    emit('deleted', props.note.id)
    emit('close')
  } catch (error) {
    console.error('Failed to delete note:', error)
    // You might want to show an error notification here
  } finally {
    isDeleting.value = false
  }
}
</script>
