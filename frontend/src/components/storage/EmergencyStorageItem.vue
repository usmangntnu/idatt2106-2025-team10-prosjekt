<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import type { StorageItem } from '@/types/types'
import { updateStorageItem } from '@/services/EmergencyStorageApi.ts'

// Shared editing state across all item instances
import { editingId } from '@/composables/useGlobalEditing.ts'

const props = defineProps<{ item: StorageItem }>()
const emit = defineEmits<{ updated: [] }>()

// whether this item is in editing mode
const isEditing = computed(() => editingId.value === props.item.id)

const newStockValue = ref<number | null>(null)
const inputRef = ref<HTMLInputElement | null>(null)
const justUpdated = ref(false)

async function startEditing() {
  // close any other edit
  editingId.value = props.item.id
  newStockValue.value = props.item.currentStock
  await nextTick()
  inputRef.value?.focus()
  inputRef.value?.select()
}

function cancelEditing() {
  if (editingId.value === props.item.id) {
    editingId.value = null
  }
  newStockValue.value = null
}

async function saveNewStock() {
  if (
    newStockValue.value === null ||
    isNaN(newStockValue.value) ||
    newStockValue.value < 0
  ) {
    alert('Ugyldig tall')
    return
  }

  const updated = await updateStorageItem(props.item.id, newStockValue.value)
  if (updated) {
    props.item.currentStock = newStockValue.value
    props.item.stockCompletionPercentage =
      updated.stockCompletionPercentage ?? props.item.stockCompletionPercentage
    props.item.expirationDate = updated.expirationDate

    // trigger highlight effect
    justUpdated.value = true
    setTimeout(() => {
      justUpdated.value = false
    }, 1000)

    cancelEditing()
    emit('updated')
  } else {
    alert('Kunne ikke oppdatere lageret')
  }
}

const percentage = computed(
  () => (props.item.stockCompletionPercentage ?? 0) * 100
)
const barClass = computed(() => {
  if (percentage.value >= 100) return 'bg-green-500'
  if (percentage.value >= 75) return 'bg-lime-500'
  if (percentage.value >= 50) return 'bg-yellow-500'
  if (percentage.value >= 25) return 'bg-orange-500'
  return 'bg-red-500'
})

function daysUntil(exp?: string) {
  if (!exp) return null
  const diff = Math.ceil((new Date(exp).getTime() - Date.now()) / 86400000)
  return diff
}
function expiryMessage() {
  const d = daysUntil(props.item.expirationDate)
  if (d === null) return ''
  if (d < 0) return `Utløpt for ${Math.abs(d)} dager siden`
  if (d === 0) return 'Utløper i dag'
  if (d === 1) return 'Utløper i morgen'
  return `Utløper om ${d} dager`
}

// Keyboard handlers
function onContainerKeydown(event: KeyboardEvent) {
  if (!isEditing.value && (event.key === 'Enter' || event.key === ' ')) {
    event.preventDefault()
    startEditing()
  }
}
function onInputKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    event.preventDefault()
    saveNewStock()
  } else if (event.key === 'Escape') {
    event.preventDefault()
    cancelEditing()
  }
}
</script>

<template>
  <div
    role="button"
    tabindex="0"
    :aria-label="`Rediger lager for ${props.item.name}`"
    class="relative flex flex-col p-3 rounded-lg shadow-sm text-sm transition-all duration-300 cursor-pointer group focus:outline-none focus:ring-2 focus:ring-blue-500"
    :class="[
      'bg-[#EEE9E1]',
      { 'transform scale-[1.02] shadow-md': justUpdated },
      { 'hover:scale-[1.02] hover:shadow-md': !justUpdated },
    ]"
    @click="startEditing"
    @keydown="onContainerKeydown"
  >
    <!-- Colored progress bar -->
    <div
      class="absolute inset-y-0 left-0 rounded-l-lg opacity-20 transition-all duration-300"
      :class="barClass"
      :style="{ width: `${Math.min(percentage, 100)}%` }"
    />

    <div class="flex flex-col gap-1 z-10">
      <!-- Main row -->
      <div class="flex items-center justify-between gap-2">
        <span class="text-left flex-1">{{ props.item.name }}</span>

        <!-- Edit mode -->
        <div v-if="isEditing" @click.stop class="flex items-center gap-2">
          <input
            ref="inputRef"
            type="number"
            v-model.number="newStockValue"
            class="w-16 p-1 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            min="0"
            @keydown="onInputKeydown"
          />
          <div class="flex gap-1">
            <button
              type="button"
              @click="saveNewStock"
              class="bg-green-500 text-white p-1 rounded hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-400"
              aria-label="Lagre ny beholdning"
            >
              ✓
            </button>
            <button
              type="button"
              @click="cancelEditing"
              class="bg-gray-500 text-white p-1 rounded hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-400"
              aria-label="Avbryt redigering"
            >
              ✕
            </button>
          </div>
        </div>

        <!-- Display mode -->
        <span v-else class="font-semibold whitespace-nowrap">
          {{ props.item.currentStock }}
          <template v-if="props.item.recommendedStockForHousehold != null">
            / {{ props.item.recommendedStockForHousehold }}
          </template>
          {{ props.item.unit ?? '' }}
        </span>
      </div>

      <!-- Expiry date -->
      <div class="text-xs flex justify-between items-center">
        <span>{{ expiryMessage() }}</span>
      </div>
    </div>
  </div>
</template>
