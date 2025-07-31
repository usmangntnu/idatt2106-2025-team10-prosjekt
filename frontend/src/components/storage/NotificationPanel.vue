<script setup lang="ts">
import type { Notification } from '@/types/types.ts'
import { useNotificationStore } from '@/stores/notification'

const props = defineProps<{
  notifications: Notification[]
}>()

const notificationStore = useNotificationStore()

const icon = (type: Notification['type']) =>
  type === 'EXPIRATION' ? '⏰' : '📉'
const colour = (type: Notification['type']) =>
  type === 'EXPIRATION' ? 'bg-orange-200' : 'bg-red-200'

function defaultMessage(n: Notification) {
  return n.type === 'EXPIRATION'
    ? `${n.itemName ?? 'Vare'} utløper snart`
    : `${n.itemName ?? 'Vare'} har lav beholdning`
}

function deleteNotification(id: number) {
  notificationStore.removeNotification(id)
}
</script>

<template>
  <div class="bg-white rounded-xl shadow p-4 w-full max-w-xs">
    <h2 class="text-lg font-semibold mb-3 text-center">Notifikasjoner</h2>

    <div
      v-if="!props.notifications || props.notifications.length === 0"
      class="text-center text-sm text-gray-500"
    >
      Ingen varsler 🎉
    </div>

    <ul v-else class="space-y-2 max-h-64 overflow-y-auto pr-1">
      <li
        v-for="n in props.notifications"
        :key="n.id"
        :class="['flex items-start gap-2 p-2 rounded relative', colour(n.type)]"
      >
        <span class="text-xl leading-none">{{ icon(n.type) }}</span>
        <span class="text-sm">
          {{ n.message ?? defaultMessage(n) }}
        </span>
        <button
          v-if="n.type === 'EXPIRATION'"
          @click="notificationStore.deleteNotification(n.id)"
          class="absolute top-1 right-2 text-gray-400 hover:text-red-500 text-lg font-bold px-1 cursor-pointer bg-transparent border-none"
          title="Fjern varsel"
          aria-label="Fjern varsel"
        >
          &times;
        </button>
      </li>
    </ul>
  </div>
</template>
