<template>
  <div class="max-w-md mx-auto bg-white p-6 rounded-lg shadow-md m-10">
    <!-- Join Household Section -->
    <div class="mb-10">
      <h1 class="text-2xl font-bold text-gray-800 mb-2">
        Bli med i en husholdning
      </h1>
      <p class="text-gray-600 mb-6">
        Skriv inn inviteringskoden du har fått fra husholdningsadministrator
      </p>

      <form @submit.prevent="onSubmitJoin">
        <div class="mb-4">
          <label
            for="householdToken"
            class="block text-sm font-medium text-gray-700 mb-1"
            >Husholdningskode</label
          >
          <input
            id="householdToken"
            v-model="householdToken"
            type="text"
            required
            :disabled="loading"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
            placeholder="Skriv inn kode"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-blue-400 transition-colors duration-200"
        >
          <span v-if="loading" class="inline-flex items-center">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
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
            Laster...
          </span>
          <span v-else>Bli med i husholdning</span>
        </button>

        <p
          v-if="joinStatusMessage"
          class="mt-3 text-sm"
          :class="
            joinStatusMessage.includes('Ble med')
              ? 'text-green-600'
              : 'text-red-600'
          "
        >
          {{ joinStatusMessage }}
        </p>
      </form>
    </div>

    <!-- Divider -->
    <div class="relative my-8">
      <div class="absolute inset-0 flex items-center">
        <div class="w-full border-t border-gray-300"></div>
      </div>
      <div class="relative flex justify-center text-sm">
        <span class="px-2 bg-white text-gray-500">eller</span>
      </div>
    </div>

    <!-- Create Household Section -->
    <div>
      <h1 class="text-2xl font-bold text-gray-800 mb-2">Lag en husholdning</h1>
      <p class="text-gray-600 mb-6">
        Fyll inn informasjon om husholdningen før du lager den
      </p>

      <form @submit.prevent="onSubmitCreate">
        <div class="space-y-4">
          <div>
            <label
              for="householdName"
              class="block text-sm font-medium text-gray-700 mb-1"
              >Husholdningsnavn</label
            >
            <input
              id="householdName"
              v-model="householdName"
              type="text"
              required
              :disabled="loading"
              placeholder="Navn på husholdningen"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:bg-gray-100 disabled:text-gray-500"
            />
          </div>

          <!-- Adresse (Kartverket-autocomplete) -->
          <div class="mb-4">
            <AddressSelector
              v-model="selectedAddress"
              :suggestions="suggestions"
              :loading="loading"
              @search="handleSearch"
            />
          </div>

          <!-- Read-only Postkode & By -->
          <div v-if="selectedAddress" class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Postkode</label
              >
              <input
                type="text"
                :value="selectedAddress.postalCode"
                disabled
                class="w-full px-4 py-2 bg-gray-100 rounded-lg"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >By</label
              >
              <input
                type="text"
                :value="selectedAddress.city"
                disabled
                class="w-full px-4 py-2 bg-gray-100 rounded-lg"
              />
            </div>
          </div>
        </div>

        <button
          type="submit"
          :disabled="loading || !selectedAddress"
          class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-blue-400 mt-6 transition-colors duration-200"
        >
          <span v-if="loading" class="inline-flex items-center">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
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
            Laster...
          </span>
          <span v-else>Lag husholdning</span>
        </button>

        <p
          v-if="createStatusMessage"
          class="mt-3 text-sm"
          :class="
            createStatusMessage.includes('opprettet')
              ? 'text-green-600'
              : 'text-red-600'
          "
        >
          {{ createStatusMessage }}
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { createHousehold, joinHousehold } from '@/services/householdApi.ts'
import { useRouter } from 'vue-router'
import type { HouseholdCreate } from '@/types/types.ts'
import AddressSelector from '@/components/AddressSelector.vue'
import { autocompleteAddress } from '@/services/addressApi'
import type { AddressSuggestion } from '@/types/types.ts'

const suggestions = ref<AddressSuggestion[]>([])
const selectedAddress = ref<AddressSuggestion | null>(null)

const router = useRouter()

const loading = ref(false)
const householdToken = ref('')
const householdName = ref('')
const createStatusMessage = ref('')
const joinStatusMessage = ref('')
const joinForm = ref()

async function onSubmitJoin() {
  if (!householdToken.value) {
    joinStatusMessage.value = 'Vennligst skriv inn en husholdningskode.'
    return
  }
  joinStatusMessage.value = 'Blir med i husholdning...'
  loading.value = true
  try {
    await joinHousehold(householdToken.value)
    console.log('Household joined with token:', householdToken.value)
    joinStatusMessage.value = 'Ble med i husholdning!'
    // Redirect
    setTimeout(() => {
      router.push('/household')
    }, 1500)
  } catch (error: any) {
    console.error('Error joining household:', error)
    if (error.response && error.response.status === 409) {
      joinStatusMessage.value =
        'Du er allerede i en husholdning, forlat den før du blir med i en annen.'
    } else if (error.response && error.response.status === 404) {
      joinStatusMessage.value =
        'Fant ikke husholdning. Vennligst sjekk koden og prøv igjen.'
    } else {
      joinStatusMessage.value =
        'Kunne ikke bli med i husholdning. Vennligst prøv igjen.'
    }
  } finally {
    loading.value = false
  }
}

async function onSubmitCreate() {
  if (!selectedAddress.value) {
    createStatusMessage.value = 'Vennligst velg en adresse.'
    return
  }

  loading.value = true
  createStatusMessage.value = 'Oppretter husholdning...'
  try {
    const household: HouseholdCreate = {
      name: householdName.value,
      address: selectedAddress.value.text,
      postalCode: selectedAddress.value.postalCode,
      city: selectedAddress.value.city,
      longitude: selectedAddress.value.longitude,
      latitude: selectedAddress.value.latitude,
    }
    console.log('creating household with name', householdName.value)
    await createHousehold(household)
    console.log('Household created with name:', householdName.value)
    createStatusMessage.value =
      'Husholdning med navn ' +
      householdName.value +
      ' opprettet! Sender deg videre...'
    setTimeout(() => {
      router.push('/household')
    }, 1500)
  } catch (error: any) {
    console.error('Error creating household:', error)
    if (error.response && error.response.status === 409) {
      createStatusMessage.value =
        'Du er allerede i en husholdning. Forlat den før du lager en ny en.'
    } else {
      createStatusMessage.value =
        'Feil under opprettelse av husholdning. Vennligst prøv igjen.'
    }
  } finally {
    loading.value = false
  }
}

async function handleSearch(query: string) {
  if (query.length < 3) {
    suggestions.value = []
    return
  }
  loading.value = true
  try {
    suggestions.value = await autocompleteAddress(query, 10)
  } finally {
    loading.value = false
  }
}
//TODO after joining or creating household add it to the user store
//TODO make sure postcode (and address?) is valid
</script>
