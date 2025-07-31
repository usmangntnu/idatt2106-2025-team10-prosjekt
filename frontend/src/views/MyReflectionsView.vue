<template>
  <div class="min-h-screen bg-[#E1E5F2]">
    <!-- Main Content -->
    <main class="container mx-auto px-4 py-8">
      <!-- Action Buttons -->
      <div class="flex flex-col md:flex-row justify-between items-center mb-8">
        <div class="flex space-x-4 mb-4 md:mb-0">
          <!-- Create Note Button -->
          <button
            @click="openModal"
            class="bg-[#1F7A8C] text-white px-6 py-3 rounded-lg shadow hover:brightness-110 transition flex items-center"
          >
            <span class="mr-2">Lag nytt notat</span>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                clip-rule="evenodd"
              />
            </svg>
          </button>

          <!-- Back to All Notes -->
          <router-link
            to="/reflections"
            class="bg-gray-100 text-gray-800 px-6 py-3 rounded-lg shadow hover:bg-gray-200 transition flex items-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-2"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L7.414 9H15a1 1 0 110 2H7.414l2.293 2.293a1 1 0 010 1.414z"
                clip-rule="evenodd"
              />
            </svg>
            <span>Tilbake til alle notater</span>
          </router-link>
        </div>

        <!-- Filter/Search -->
        <div class="w-full md:w-96">
          <div class="relative">
            <div
              class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 text-gray-500"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fill-rule="evenodd"
                  d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z"
                  clip-rule="evenodd"
                />
              </svg>
            </div>
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Søk i notater..."
              class="w-full pl-10 pr-4 py-3 rounded-lg border-2 border-[#1F7A8C] bg-white shadow-md focus:outline-none focus:ring-2 focus:ring-[#1F7A8C] focus:border-transparent transition-all text-gray-700"
            />
            <button
              v-if="searchQuery"
              @click="searchQuery = ''"
              class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500 hover:text-gray-700"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fill-rule="evenodd"
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Notes Grid -->
      <div v-if="loading" class="flex justify-center p-10">
        <div
          class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#1F7A8C]"
        ></div>
      </div>

      <div
        v-else-if="filteredPrivateNotes.length === 0"
        class="text-center p-10 bg-white rounded-lg shadow mt-6"
      >
        <div class="mb-4">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-16 w-16 text-gray-300 mx-auto"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 6v6m0 0v6m0-6h6m-6 0H6"
            />
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900">Ingen notater funnet</h3>
        <p class="mt-2 text-gray-500">
          Begynn å lage refleksjonsnotater ved å klikke på "Lag nytt notat".
        </p>
      </div>

      <div v-else>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <ReflectionNoteCard
            v-for="note in filteredPrivateNotes"
            :key="note.id"
            :note="note"
            @click="viewNoteDetails(note)"
          />
        </div>
      </div>
    </main>

    <!-- Create Note Modal -->
    <CreateReflectionNoteModal
      :isOpen="isModalOpen"
      @close="closeModal"
      @created="onNoteCreated"
    />

    <!-- Note Detail Modal -->
    <ReflectionNoteDetailModal
      v-if="selectedNote"
      :isOpen="isDetailModalOpen"
      :note="selectedNote"
      @close="closeDetailModal"
      @deleted="handleDeleted"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import CreateReflectionNoteModal from '@/components/reflection/CreateReflectionNoteModal.vue'
import ReflectionNoteCard from '@/components/reflection/ReflectionNoteCard.vue'
import ReflectionNoteDetailModal from '@/components/reflection/ReflectionNoteDetailModal.vue'
import { useApi } from '@/composables/useApi'
import { getMyReflectionNotes } from '@/services/reflectionApi.ts'
import type { ReflectionNote } from '@/types/types.ts'

// State
const searchQuery = ref('')
const isModalOpen = ref(false)
const isDetailModalOpen = ref(false)
const selectedNote = ref<ReflectionNote | null>(null)

// Fetch notes
const {
  data: privateNotes,
  loading,
  error,
  execute,
} = useApi<ReflectionNote[]>(() => getMyReflectionNotes())

// Filter notes by search query
const filteredPrivateNotes = computed(() => {
  if (!privateNotes.value) return []
  return privateNotes.value.filter(
    (note) =>
      note.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      note.content.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const handleDeleted = () => {
  execute()
}

// Modal handlers
const openModal = () => {
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
}

const viewNoteDetails = (note: ReflectionNote) => {
  selectedNote.value = note
  isDetailModalOpen.value = true
}

const closeDetailModal = () => {
  isDetailModalOpen.value = false
  selectedNote.value = null
}

// Handle successful note creation
const onNoteCreated = () => {
  closeModal()
  execute()
}

// Initial data load
onMounted(() => {
  execute()
})
</script>
