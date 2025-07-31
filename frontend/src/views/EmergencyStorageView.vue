<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import type {
  Category,
  StorageItem,
  PreparednessSummary,
  Household,
} from '@/types/types.ts'
import { useApi } from '@/composables/useApi'

import EmergencyStorageGrid from '@/components/storage/EmergencyStorageGrid.vue'
import PreparednessSummaryCard from '@/components/storage/PreparednessSummaryCard.vue'
import NotificationPanel from '@/components/storage/NotificationPanel.vue'
import {
  getAllItemCategories,
  getAllStorageItemsForHousehold,
  getHouseholdPreparednessSummary,
} from '@/services/EmergencyStorageApi.ts'
import { getMyHousehold } from '@/services/householdApi.ts'
import { useNotificationStore } from '@/stores/notification'
import { AxiosError } from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const householdApi = useApi<Household>(getMyHousehold)

const categoryApi = useApi<Category[]>(getAllItemCategories || [])
const storageItemApi = useApi<StorageItem[]>(() => {
  const id = householdApi.data.value?.id
  if (id == null) throw new Error('Household not loaded')
  return getAllStorageItemsForHousehold(id)
})
const preparednessApi = useApi<PreparednessSummary>(() => {
  const id = householdApi.data.value?.id
  if (id == null) throw new Error('Household not loaded')
  return getHouseholdPreparednessSummary(id)
})

const notificationStore = useNotificationStore()

onMounted(async () => {
  // Start loading categories (independent of household)
  const categoryPromise = categoryApi.execute()

  // Load household data first and wait for it
  await householdApi.execute()

  // Only proceed with dependent calls if household ID is available
  if (householdApi.data.value?.id) {
    await Promise.all([storageItemApi.execute(), preparednessApi.execute()])
    // Fetch notifications from store after getting householdId
    notificationStore.fetchNotifications(householdApi.data.value.id)
  }

  await categoryPromise
})

watch(
  () => householdApi.error.value,
  (error) => {
    const axiosError = error as AxiosError
    if (axiosError?.response?.status === 404) {
      router.push({ name: 'join-household' })
    }
  }
)

watch(
  () => householdApi.data.value?.id,
  (id, oldId) => {
    if (id && id !== oldId) {
      storageItemApi.execute()
      preparednessApi.execute()
      notificationStore.fetchNotifications(id)
    }
  }
)

const categories = computed(() => categoryApi.data.value ?? [])
const storageItems = computed(() => storageItemApi.data.value ?? [])
const preparednessSummary = computed(() => preparednessApi.data.value)
const notifications = computed(() => notificationStore.notifications)

const isLoading = computed(
  () =>
    categoryApi.loading.value ||
    storageItemApi.loading.value ||
    preparednessApi.loading.value ||
    notificationStore.isLoading
)

const fatalError = computed(
  () =>
    categoryApi.error.value ||
    storageItemApi.error.value ||
    preparednessApi.error.value ||
    notificationStore.error
)

const handleItemUpdated = async () => {
  // Refresh preparedness score after item update
  preparednessApi.execute({ silent: true })
}
</script>

<template>
  <div class="container mx-auto p-6 rounded-lg bg-[#E1E5F2] min-h-screen">
    <p v-if="isLoading" class="py-8 text-center text-gray-600">Laster inn …</p>
    <p v-else-if="fatalError" class="py-8 text-center text-red-600">
      {{ fatalError }}
    </p>

    <div v-else class="flex flex-col gap-10">
      <div class="flex flex-wrap justify-center items-start gap-6">
        <PreparednessSummaryCard
          v-if="preparednessSummary"
          :summary="preparednessSummary"
          class="w-full max-w-md"
        />
      </div>

      <EmergencyStorageGrid
        :categories="categories"
        :storage-items="storageItems"
        @updated="handleItemUpdated"
      />
    </div>
  </div>
</template>
