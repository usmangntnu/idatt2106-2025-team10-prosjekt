<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.ts'
import { getMyHousehold } from '@/services/householdApi.ts'
import type { Household, User } from '@/types/types.ts'
import InviteToHousehold from '@/components/InviteToHousehold.vue'
import { useApi } from '@/composables/useApi.ts'
import type { AxiosError } from 'axios'
import LeaveHousehold from '@/components/LeaveHousehold.vue'

const householdApi = useApi<Household, AxiosError>(getMyHousehold)

const userStore = useUserStore()
const router = useRouter()

onMounted(() => {
  if (!userStore.currentUser) {
    router.push({ name: 'login' })
  } else {
    householdApi.execute()
  }
})

// Watch for 404 to redirect to join-household
watch(
  () => householdApi.error.value,
  (error) => {
    if (error?.response?.status === 404) {
      router.push({ name: 'join-household' })
    }
  }
)

const loading = computed(() => householdApi.loading.value)
const statusMessage = computed(() =>
  householdApi.error.value
    ? 'Det oppstod en feil ved henting av husstandsinformasjon.'
    : ''
)

const householdNameDisplay = computed(
  () => householdApi.data.value?.householdName ?? 'Din husstand'
)
const usersInHousehold = computed<User[]>(
  () => householdApi.data.value?.users ?? []
)
const householdOwner = computed<User | undefined>(
  () => householdApi.data.value?.owner
)
const householdCity = computed(() => householdApi.data.value?.city ?? '')
const householdPostalCode = computed(
  () => householdApi.data.value?.postalCode ?? ''
)
const householdAddress = computed(() => householdApi.data.value?.address ?? '')

const isOwner = computed(
  () => userStore.currentUser?.email === householdOwner.value?.email
)

const showInviteModal = ref(false)
const showLeaveModal = ref(false)
</script>

<template>
  <div
    class="max-w-5xl mx-auto px-4 py-8 sm:px-6 lg:px-8 bg-[#E1E5F2] min-h-screen"
  >
    <!-- Loading and error states -->
    <div
      v-if="loading"
      class="bg-[#FF9900]/10 rounded-lg p-4 mb-6 flex items-center"
      role="status"
    >
      <svg
        class="animate-spin h-5 w-5 text-[#000000] mr-3"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle
          class="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          stroke-width="4"
        ></circle>
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        ></path>
      </svg>
      <span class="font-medium text-[#232F3E]"
        >Laster inn husstandsinformasjon...</span
      >
    </div>

    <div
      v-if="statusMessage"
      class="bg-red-50 border-l-4 border-red-500 p-4 mb-6"
      role="alert"
    >
      <div class="flex items-center">
        <div class="flex-shrink-0">
          <svg
            class="h-5 w-5 text-red-500"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fill-rule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              clip-rule="evenodd"
            />
          </svg>
        </div>
        <div class="ml-3">
          <p class="text-sm text-red-700">{{ statusMessage }}</p>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div
      v-if="!loading && !statusMessage"
      class="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-200"
    >
      <!-- Header with household name -->
      <div class="bg-[#BFDBF7] px-6 py-4">
        <h1 class="text-2xl sm:text-3xl font-bold text-[#131A22]">
          {{ householdNameDisplay }}
        </h1>
      </div>

      <!-- Household details -->
      <div class="p-6">
        <div class="mb-8">
          <h2 class="text-xl font-semibold text-[#232F3E] mb-4 border-b pb-2">
            Husstandsinformasjon
          </h2>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-4">
            <div class="flex flex-col">
              <span class="text-sm text-gray-500 font-medium">Adresse</span>
              <span class="text-gray-800">{{ householdAddress || '—' }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500 font-medium">Postnummer</span>
              <span class="text-gray-800">{{
                householdPostalCode || '—'
              }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-sm text-gray-500 font-medium">By</span>
              <span class="text-gray-800">{{ householdCity || '—' }}</span>
            </div>
          </div>
        </div>

        <!-- Users section -->
        <div class="mb-8">
          <h2 class="text-xl font-semibold text-[#232F3E] mb-4 border-b pb-2">
            Brukere i husstanden
            <span
              class="ml-2 bg-[#BFDBF7] text-[#131A22] text-xs font-medium px-2.5 py-0.5 rounded-full"
              >{{ usersInHousehold.length }}</span
            >
          </h2>

          <ul
            class="divide-y divide-gray-100"
            aria-label="Household members list"
          >
            <li
              v-for="user in usersInHousehold"
              :key="user.email"
              class="py-3 flex items-center justify-between"
            >
              <div class="flex items-center space-x-3">
                <div
                  class="bg-[#BFDBF7] rounded-full w-10 h-10 flex items-center justify-center text-[#131A22] font-semibold"
                >
                  {{ user.firstName?.[0]?.toUpperCase() || '?'
                  }}{{ user.lastName?.[0]?.toUpperCase() || '' }}
                </div>
                <div>
                  <p class="font-medium text-gray-800">
                    {{ user.firstName }} {{ user.lastName }}
                  </p>
                  <p class="text-sm text-gray-500">{{ user.email }}</p>
                </div>
              </div>
              <div
                v-if="user.email === householdOwner?.email"
                class="bg-[#232F3E] text-[#FFFFFF] text-m px-2 py-1 rounded"
              >
                Eier
              </div>
            </li>
          </ul>
        </div>

        <!-- Actions section -->
        <div
          class="space-y-4 sm:flex sm:flex-wrap sm:gap-4 mt-6 pt-6 border-t border-gray-200"
        >
          <button
            @click="router.push({ name: 'storage' })"
            class="w-full sm:w-auto h-10 bg-[#232F3E] hover:bg-[#131A22] text-white font-medium px-5 rounded transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-[#FF9900]/50 flex items-center justify-center"
            aria-label="Gå til ditt husstandslager"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-2"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M5 4a3 3 0 00-3 3v6a3 3 0 003 3h10a3 3 0 003-3V7a3 3 0 00-3-3H5zm0 2h10a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1V7a1 1 0 011-1z"
                clip-rule="evenodd"
              />
            </svg>
            Husstandslager
          </button>

          <div v-if="isOwner" class="w-full sm:w-auto">
            <button
              @click="showInviteModal = true"
              class="w-full h-10 bg-[#232F3E] hover:bg-[#131A22] text-white font-medium px-5 rounded transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-[#FF9900]/50 flex items-center justify-center"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-5 w-5 mr-2"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z"
                />
              </svg>
              Inviter til husstand
            </button>
          </div>

          <button
            @click="showLeaveModal = true"
            class="w-full sm:w-auto h-10 bg-red-600 hover:bg-red-700 text-white font-medium px-5 rounded transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-[#FF9900]/50 flex items-center justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5 mr-2"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M3 3a1 1 0 00-1 1v12a1 1 0 001 1h12a1 1 0 001-1V7.414l-5-5H3zM2 4a2 2 0 012-2h8.5a1 1 0 01.707.293l5 5A1 1 0 0118 8v8a2 2 0 01-2 2H4a2 2 0 01-2-2V4z"
                clip-rule="evenodd"
              />
              <path
                d="M11 7.5a.5.5 0 01.5-.5h4a.5.5 0 010 1h-4a.5.5 0 01-.5-.5z"
              />
            </svg>
            Forlat husstand
          </button>
        </div>

        <!-- Modal for Invite -->
        <div
          v-if="showInviteModal"
          class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
        >
          <div class="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 class="text-lg font-semibold text-[#232F3E] mb-4">
              Inviter til husstand
            </h3>
            <InviteToHousehold @close="showInviteModal = false" />
            <div class="mt-4 text-right">
              <button
                @click="showInviteModal = false"
                class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded transition-colors duration-200"
              >
                Lukk
              </button>
            </div>
          </div>
        </div>

        <!-- Modal for Leave -->
        <div
          v-if="showLeaveModal"
          class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
        >
          <div class="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 class="text-lg font-semibold text-[#232F3E] mb-4">
              Forlat husstand
            </h3>
            <LeaveHousehold @close="showLeaveModal = false" />
            <div class="mt-4 text-right">
              <button
                @click="showLeaveModal = false"
                class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded transition-colors duration-200"
              >
                Avbryt
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
