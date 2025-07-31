<script setup lang="ts">
import { computed } from 'vue'
import type {
  DashboardSummary,
  PreparednessSummary,
  Notification,
} from '@/types/types'
import PreparednessSummaryCard from '@/components/storage/PreparednessSummaryCard.vue'
import NotificationPanel from '@/components/storage/NotificationPanel.vue'

const props = defineProps<{
  dashboard: DashboardSummary
}>()

const preparedness = computed<PreparednessSummary>(
  () => props.dashboard.preparedness
)
const notifications = computed<Notification[]>(
  () => props.dashboard.recentNotifications
)
</script>

<template>
  <div class="flex justify-center">
    <!-- Unified container  -->
    <div
      class="w-full max-w-5xl bg-white rounded-xl shadow p-6 flex flex-col md:flex-row gap-6"
    >
      <!-- left  / top on mobile -->
      <PreparednessSummaryCard :summary="preparedness" class="flex-1" />

      <!-- right / bottom on mobile -->
      <NotificationPanel :notifications="notifications" class="flex-1" />
    </div>
  </div>
</template>
