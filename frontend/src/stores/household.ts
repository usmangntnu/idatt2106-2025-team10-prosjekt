import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Household } from '@/types/types'
import { getMyHousehold } from '@/services/householdApi'

export const useHouseholdStore = defineStore('household', () => {
  const household = ref<Household | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchHousehold() {
    loading.value = true
    error.value = null
    try {
      household.value = await getMyHousehold()
    } catch (e: any) {
      error.value = e?.message || 'Feil ved henting av husholdning'
    } finally {
      loading.value = false
    }
  }

  return { household, loading, error, fetchHousehold }
})
