import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { Notification } from '@/types/types.ts'
import {
  getNotificationsForHousehold,
  deleteNotification as deleteNotificationApi,
} from '@/services/EmergencyStorageApi'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const hasNotifications = computed(() => notifications.value.length > 0)

  async function fetchNotifications(householdId: number) {
    isLoading.value = true
    error.value = null
    try {
      notifications.value = await getNotificationsForHousehold(householdId)
    } catch (e: any) {
      error.value = e?.message ?? 'Feil ved henting av varsler'
    } finally {
      isLoading.value = false
    }
  }

  // --- New actions: ---

  async function deleteNotification(notificationId: number) {
    try {
      await deleteNotificationApi(notificationId) // deletes in backend
      removeNotification(notificationId) // removes from local store
    } catch (e) {
      console.error('Failed to delete notification', e)
    }
  }

  // Add a notification if it's not already in the list.
  function addNotification(notification: Notification) {
    // To avoid duplicates you can check by ID:
    if (!notifications.value.some((n) => n.id === notification.id)) {
      notifications.value.push(notification)
    }
  }

  // Remove notification by ID.
  function removeNotification(notificationId: number) {
    notifications.value = notifications.value.filter(
      (n) => n.id !== notificationId
    )
  }

  return {
    notifications,
    hasNotifications,
    isLoading,
    error,
    fetchNotifications,
    addNotification,
    removeNotification,
    deleteNotification,
  }
})
