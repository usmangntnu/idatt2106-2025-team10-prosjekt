<script setup lang="ts">
import { ref, defineEmits } from 'vue'
import { inviteUserToHousehold } from '@/services/householdApi.ts'

const emit = defineEmits(['close'])
const statusMessage = ref('')
const isLoading = ref(false)
const inviteCode = ref('')

async function getInviteCodeToHousehold() {
  statusMessage.value = ''
  inviteCode.value = ''
  isLoading.value = true

  try {
    inviteCode.value = await inviteUserToHousehold()
  } catch (error) {
    console.error('Error inviting user to household:', error)
    statusMessage.value = `Feil under generering av invitasjonskode. Vennligst prøv igjen.`
  } finally {
    isLoading.value = false
  }
}

async function copyToClipboard() {
  if (!inviteCode.value) return
  try {
    await navigator.clipboard.writeText(inviteCode.value)
    statusMessage.value = 'Koden er kopiert til utklippstavlen!'
  } catch (err) {
    console.error('Clipboard error:', err)
    statusMessage.value = 'Kunne ikke kopiere koden.'
  }
}

function closeModal() {
  emit('close')
}
</script>

<template>
  <div class="text-left">
    <p class="mb-4 text-gray-600">
      Generer en invitasjonskode for å la andre bli med i din husstand. Denne
      koden kan kun brukes én gang.
    </p>

    <div v-if="inviteCode" class="mb-4">
      <p class="text-sm text-gray-500 mb-1">Invitasjonskode:</p>
      <div class="flex">
        <input
          type="text"
          readonly
          :value="inviteCode"
          class="flex-grow bg-gray-50 border border-gray-300 text-gray-900 rounded-l-lg p-2.5 focus:ring-[#FF9900]/50 focus:border-[#FF9900]"
        />
        <button
          @click="copyToClipboard"
          class="bg-[#232F3E] text-white px-3 rounded-r-lg hover:bg-[#131A22] transition-colors duration-200"
          title="Kopier til utklippstavle"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path d="M8 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" />
            <path
              d="M6 3a2 2 0 00-2 2v11a2 2 0 002 2h8a2 2 0 002-2V5a2 2 0 00-2-2 3 3 0 01-3 3H9a3 3 0 01-3-3z"
            />
          </svg>
        </button>
      </div>
    </div>

    <p
      v-if="statusMessage"
      :class="inviteCode ? 'text-green-600' : 'text-red-500'"
      class="mb-4"
    >
      {{ statusMessage }}
    </p>

    <button
      v-if="!inviteCode"
      @click="getInviteCodeToHousehold"
      :disabled="isLoading"
      class="w-full bg-[#232F3E] hover:bg-[#131A22] text-white font-medium py-2.5 px-5 rounded transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-[#FF9900]/50 flex items-center justify-center disabled:opacity-50"
    >
      <svg
        v-if="isLoading"
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
      <span>{{
        isLoading ? 'Genererer kode...' : 'Generer invitasjonskode'
      }}</span>
    </button>

    <button
      v-else
      @click="getInviteCodeToHousehold"
      :disabled="isLoading"
      class="mt-2 w-full bg-gray-100 hover:bg-gray-200 text-gray-800 font-medium py-2 px-4 rounded transition-colors duration-200 flex items-center justify-center"
    >
      <span>Generer ny kode</span>
    </button>
  </div>
</template>
