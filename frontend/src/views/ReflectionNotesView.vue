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

          <!-- My Notes Link -->
          <router-link
            to="/reflections/my"
            class="bg-gray-100 text-gray-800 px-6 py-3 rounded-lg shadow hover:bg-gray-200 transition flex items-center"
          >
            <span class="mr-2">Dine notater</span>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                clip-rule="evenodd"
              />
            </svg>
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

      <!-- Tabs Navigation -->
      <div class="mb-6 border-b border-gray-200">
        <nav class="-mb-px flex space-x-8">
          <button
            @click="activeTab = 'public'"
            :class="[
              activeTab === 'public'
                ? 'border-[#1F7A8C] text-[#1F7A8C]'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm md:text-base',
            ]"
          >
            Offentlige notater
          </button>
          <button
            @click="activeTab = 'household'"
            :class="[
              activeTab === 'household'
                ? 'border-[#1F7A8C] text-[#1F7A8C]'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              'whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm md:text-base',
            ]"
          >
            Husstandens notater
          </button>
        </nav>
      </div>

      <!-- Notes Grid -->
      <div
        v-if="loading.public || loading.household"
        class="flex justify-center p-10"
      >
        <div
          class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#1F7A8C]"
        ></div>
      </div>

      <div v-else>
        <!-- Public Notes Tab -->
        <div v-if="activeTab === 'public'">
          <h2 class="text-xl font-semibold mb-4">
            Offentlige refleksjonsnotater
          </h2>

          <div
            v-if="filteredPublicNotes.length === 0"
            class="text-center p-10 bg-white rounded-lg shadow"
          >
            <p class="text-gray-500">Ingen offentlige notater funnet.</p>
          </div>

          <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
          >
            <ReflectionNoteCard
              v-for="note in filteredPublicNotes"
              :key="note.id"
              :note="note"
              @click="viewNoteDetails(note)"
            />
          </div>
        </div>

        <!-- Household Notes Tab -->
        <div v-if="activeTab === 'household'">
          <h2 class="text-xl font-semibold mb-4">
            Husstandens refleksjonsnotater
          </h2>

          <div
            v-if="filteredHouseholdNotes.length === 0"
            class="text-center p-10 bg-white rounded-lg shadow"
          >
            <p class="text-gray-500">Ingen husstandsnotater funnet.</p>
          </div>

          <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
          >
            <ReflectionNoteCard
              v-for="note in filteredHouseholdNotes"
              :key="note.id"
              :note="note"
              @click="viewNoteDetails(note)"
            />
          </div>
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
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useApi } from '@/composables/useApi'
import {
  getPublicReflectionNotes,
  getHouseholdReflectionNotes,
} from '@/services/reflectionApi'
import type { ReflectionNote } from '@/types/types'
import CreateReflectionNoteModal from '@/components/reflection/CreateReflectionNoteModal.vue'
import ReflectionNoteCard from '@/components/reflection/ReflectionNoteCard.vue'
import ReflectionNoteDetailModal from '@/components/reflection/ReflectionNoteDetailModal.vue'
import { useRouter } from 'vue-router'

// State
const activeTab = ref('public')
const searchQuery = ref('')
const isModalOpen = ref(false)
const isDetailModalOpen = ref(false)
const selectedNote = ref<ReflectionNote | null>(null)

const router = useRouter()

// Fetch public notes
const {
  data: publicNotes,
  loading: publicLoading,
  error: publicError,
  execute: fetchPublicNotes,
} = useApi(() => getPublicReflectionNotes())

// Fetch household notes
const {
  data: householdNotes,
  loading: householdLoading,
  error: householdError,
  execute: fetchHouseholdNotes,
} = useApi(() => getHouseholdReflectionNotes())

// Combined loading state
const loading = computed(() => ({
  public: publicLoading.value,
  household: householdLoading.value,
}))

// Filter notes by search query
const filteredPublicNotes = computed(() => {
  if (!publicNotes.value) return []
  return publicNotes.value.filter(
    (note) =>
      note.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      note.content.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const filteredHouseholdNotes = computed(() => {
  if (!householdNotes.value) return []
  return householdNotes.value.filter(
    (note) =>
      note.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      note.content.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

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

  fetchPublicNotes()
  fetchHouseholdNotes()

  router.push('/reflections/my')
}

// Watch for tab changes to load data if needed
const loadTabData = (tab: string) => {
  if (tab === 'public' && !publicNotes.value) {
    fetchPublicNotes()
  } else if (tab === 'household' && !householdNotes.value) {
    fetchHouseholdNotes()
  }
}

// Initial data load
onMounted(() => {
  loadTabData(activeTab.value)
})

// Watch tab changes
watch(activeTab, (newTab) => {
  loadTabData(newTab)
})
</script>
