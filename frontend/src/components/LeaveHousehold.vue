<template>
  <div>
    <p class="mb-4 text-gray-600">
      Er du sikker på at du vil forlate denne husstanden? Dette kan ikke angres.
    </p>

    <button
      @click="onLeave"
      :disabled="leaveLoading"
      class="w-full bg-[#232F3E] hover:bg-[#131A22] text-white font-medium py-2.5 px-5 rounded transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-[#FF9900]/50 flex items-center justify-center disabled:opacity-50"
    >
      <svg
        v-if="leaveLoading"
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
      <span>{{ leaveLoading ? 'Forlater...' : 'Ja, forlat husstand' }}</span>
    </button>

    <p v-if="leaveError" class="text-red-500 mt-3">
      Noe gikk galt ved forlatelse av husstanden.
    </p>
  </div>
</template>

<script setup lang="ts">
import { watch, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import { leaveHousehold } from '@/services/householdApi.ts'

const emit = defineEmits(['close'])
const router = useRouter()
const leaveApi = useApi<void, unknown>(leaveHousehold)

// expose top-level refs for template auto-unwrapping
const leaveLoading = leaveApi.loading
const leaveError = leaveApi.error

function onLeave() {
  leaveApi.execute()
}

watch(
  () => leaveApi.data.value,
  (done) => {
    if (done !== null) {
      router.push({ name: 'join-household' })
    }
  }
)
</script>
