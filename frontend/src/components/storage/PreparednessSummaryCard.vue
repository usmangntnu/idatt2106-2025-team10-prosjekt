<script setup lang="ts">
import { computed } from 'vue'
import type { PreparednessSummary } from '@/types/types.ts'

const props = defineProps<{
  summary: PreparednessSummary
}>()

const score = computed(() => Math.round(props.summary.overallScore * 100))

/* simple colour scale */
const ringColour = computed(() =>
  score.value >= 80
    ? 'stroke-green-500'
    : score.value >= 50
      ? 'stroke-yellow-400'
      : 'stroke-red-500'
)
</script>

<template>
  <div class="bg-white rounded-xl shadow p-6 flex flex-col items-center gap-4">
    <h2 class="text-l font-semibold">Beredskapsscore</h2>

    <!-- radial progress ring -->
    <div class="relative w-24 h-24">
      <svg viewBox="0 0 36 36" class="w-full h-full rotate-[-90deg]">
        <!-- background circle -->
        <circle
          cx="18"
          cy="18"
          r="15.915"
          class="stroke-gray-200"
          stroke-width="3"
          fill="none"
        />
        <!-- progress circle -->
        <circle
          cx="18"
          cy="18"
          r="15.915"
          :class="ringColour"
          stroke-width="3"
          fill="none"
          stroke-linecap="round"
          :stroke-dasharray="`${score} 100`"
        />
      </svg>
      <span
        class="absolute inset-0 flex items-center justify-center text-xl font-bold"
      >
        {{ score }}%
      </span>
    </div>

    <!-- quick facts -->
    <ul class="text-sm text-gray-600 space-y-1">
      <li>Total: {{ summary.totalItems }}</li>
      <li>OK: {{ summary.adequateItems }}</li>
      <li>Lav beholdning: {{ summary.lowStockItems }}</li>
      <li>Utløper snart: {{ summary.expiringItems }}</li>
    </ul>
  </div>
</template>
