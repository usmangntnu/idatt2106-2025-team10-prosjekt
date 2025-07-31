<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center"
  >
    <!-- Modal backdrop -->
    <div class="fixed inset-0 bg-black opacity-50" @click="closeModal"></div>

    <!-- Modal content -->
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4 z-10">
      <div class="p-6">
        <h2 class="text-2xl font-bold mb-4">Lag refleksjonsnotat</h2>

        <form @submit.prevent="submitForm">
          <!-- Title field -->
          <div class="mb-4">
            <label
              for="title"
              class="block text-sm font-medium text-gray-700 mb-1"
              >Tittel</label
            >
            <input
              type="text"
              id="title"
              v-model="form.title"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#1F7A8C]"
              required
            />
          </div>

          <!-- Content field -->
          <div class="mb-4">
            <label
              for="content"
              class="block text-sm font-medium text-gray-700 mb-1"
              >Innhold</label
            >
            <textarea
              id="content"
              v-model="form.content"
              rows="6"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#1F7A8C]"
              required
            ></textarea>
          </div>

          <!-- Visibility dropdown -->
          <div class="mb-6">
            <label
              for="visibility"
              class="block text-sm font-medium text-gray-700 mb-1"
              >Synlighet</label
            >
            <select
              id="visibility"
              v-model="form.visibility"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#1F7A8C]"
              required
            >
              <option value="PRIVATE">Privat</option>
              <option value="PUBLIC">Offentlig</option>
              <option value="HOUSEHOLD">Husstand</option>
            </select>
          </div>

          <!-- Action buttons -->
          <div class="flex justify-end space-x-3">
            <button
              type="button"
              @click="closeModal"
              class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100"
            >
              Avbryt
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-[#1F7A8C] text-white rounded-md hover:brightness-110 transition"
              :disabled="loading"
            >
              {{ loading ? 'Lagrer...' : 'Lagre' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { addReflectionNote } from '@/services/reflectionApi.ts'
import { useApi } from '@/composables/useApi.ts'
import type {
  ReflectionNoteCreate,
  ReflectionNoteVisibility,
} from '@/types/types.ts'

const props = defineProps<{
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'created'): void
}>()

const form = reactive<ReflectionNoteCreate>({
  title: '',
  content: '',
  visibility: 'PRIVATE' as ReflectionNoteVisibility,
})

const { loading, error, execute } = useApi(() => addReflectionNote(form))

// Close the modal and reset form
const closeModal = () => {
  emit('close')
  resetForm()
}

// Reset form to initial state
const resetForm = () => {
  form.title = ''
  form.content = ''
  form.visibility = 'PRIVATE'
}

// Submit the form
const submitForm = async () => {
  await execute()

  if (!error.value) {
    emit('created')
    closeModal()
  }
}
</script>
