import type { EventResponse, PositionResponse } from '@/types/types.ts'
import { useUserStore } from '@/stores/user.ts'
import { computed } from 'vue'
import 'leaflet-routing-machine'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)

/**
 * A mapping of position types to their respective icon URLs.
 */
const iconMap: Record<string, string> = {
  ANGREP: '/icons/map/attack-marker.svg',
  FLOM: '/icons/map/flood-marker.svg',
  BRANN: '/icons/map/fire-marker.svg',
  FLYANGREP: '/icons/map/flight-attack-marker.svg',
  FARESONE: '/icons/map/threat-zone-marker.png',
  TILFLUKTSROM: '/icons/map/shelter-icon.svg',
  MATSTASJON: '/icons/map/food-station.svg',
  HJERTESTARTER: '/icons/map/health-station.svg',
}

/**
 * The default icon URL for positions.
 */
const defaultIcon =
  'https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon.png'

/**
 * Helper function to normalize the type string.
 *
 * @param type - The type string to normalize.
 * @returns The normalized type string.
 */
function normalizeType(type: string): string {
  console.log("Normalize should return: " + type.replace(/\s+/g, '').toUpperCase())
  return type.replace(/\s+/g, '').toUpperCase()
}

/**
 * Returns the icon URL for a given event type.
 *
 * @param type - The type of the event.
 * @returns The URL of the icon for the given type.
 */
export const getIconUrlForEventType = (type: string): string => {
  const key = normalizeType(type)
  return iconMap[key] || defaultIcon
}

export function getIconUrlForPosType(type: string): string {
  const key = normalizeType(type)
  return iconMap[key] || defaultIcon
}

export const severities = [
  { label: 'LAV', value: 'LOW' },
  { label: 'MODERAT', value: 'MEDIUM' },
  { label: 'HØY', value: 'HIGH' },
  { label: 'KRITISK', value: 'CRITICAL' },
]

export const status = [
  { label: 'KOMMENDE', value: 'UPCOMING' },
  { label: 'AKTIV', value: 'ACTIVE' },
  { label: 'INAKTIV', value: 'INACTIVE' },
  { label: 'AVSLUTTET', value: 'FINISHED' },
]

export const severityEmoji: Record<string, string> = {
  LOW: '🟢',
  MEDIUM: '🟡',
  HIGH: '🟠',
  CRITICAL: '🔴',
}

export const statusEmoji = {
  UPCOMING: '🔵',
  ACTIVE: '🟢',
  INACTIVE: '🟡',
  FINISHED: '🟣',
}

// Map severity to border color classes
export const severityColorClass: Record<string, string> = {
  LOW: 'border-green-500',
  MEDIUM: 'border-yellow-500',
  HIGH: 'border-orange-500',
  CRITICAL: 'border-red-500',
}

/**
 * Hendelses-popup (med fremhevet tittel, mindre fonter og kompakt layout)
 */
export function formatEventPopup(event: EventResponse) {
  userStore.fetchCurrentUser()

  const showAdmin = isAdmin.value
  const sevLabel =
    severities.find((s) => s.value === event.severity)?.label ?? event.severity
  const statLabel =
    status.find((s) => s.value === event.status)?.label ?? event.status
  const typeLabel = event.eventType?.name || 'Ukjent'
  const borderCls = severityColorClass[event.severity] || 'border-gray-300'
  const hideCloseButtonStyle = `
    <style>
      .leaflet-container a.leaflet-popup-close-button {
        display: none !important;
      }
    </style>
    `

  return `
  ${hideCloseButtonStyle}
  <div class="max-w-sm bg-white rounded-lg shadow-md p-2 border-l-4 ${borderCls} animate-fadeIn">
    <h3 class="text-sm font-semibold text-gray-900 mb-1">${event.title}</h3>
    ${showAdmin ? `<p class="text-[8px] font-medium text-gray-700 mb-1">ID: ${event.id}</p>` : ''}
    <div class="space-y-1 mb-1">
      <p class="flex items-center text-[8px] font-medium text-gray-700">
        <strong class="w-16">Hendelse:</strong>
        <span class="inline-flex items-center ml-1">${typeLabel}</span>
      </p>
      <p class="flex items-center text-[8px] font-medium text-gray-700">
        <strong class="w-16">Krisenivå:</strong>
        <span class="inline-flex items-center ml-1">
          ${severityEmoji[event.severity as keyof typeof severityEmoji]}
          <span class="ml-1">${sevLabel}</span>
        </span>
      </p>
      <p class="flex items-center text-[8px] font-medium text-gray-700">
        <strong class="w-16">Status:</strong>
        <span class="inline-flex items-center ml-1 italic animate-pulse">
          ${statusEmoji[event.status]} ${statLabel}
        </span>
      </p>
      <p class="text-[7px] text-gray-500">
        <strong>Start:</strong>
        ${new Date(event.startTime).toLocaleString('nb-NO')}
      </p>
    </div>
    <p class="text-xs text-gray-800 leading-snug whitespace-pre-line">${event.description || ''}</p>
  </div>`
}

/**
 * Posisjon-popup (type-badge, capacity badge, kompakt styling)
 */
export function formatPositionPopup(pos: PositionResponse) {
  userStore.fetchCurrentUser()

  const showAdmin = isAdmin.value
  const { title, description, capacity, type, id } = pos
  const typeName = type?.name || 'Ukjent'
  const isShelter = typeName === 'Tilfluktsrom'
  const hideCloseButtonStyle = `
    <style>
      .leaflet-container a.leaflet-popup-close-button {
        display: none !important;
      }
    </style>
    `

  const typeBadge = `
    <span class="inline-block bg-blue-100 text-blue-800 text-[8px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-full">
      ${typeName}
    </span>`

  const capHtml =
    isShelter && capacity != null
      ? `
    <div class="flex items-center space-x-1 mt-2">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-green-500" viewBox="0 0 20 20" fill="currentColor">
        <path d="M3 7a1 1 0 011-1h12a1 1 0 011 1v8a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"/>
      </svg>
      <span class="text-[8px] font-semibold text-gray-700">Kapasitet:</span>
      <span class="inline-block bg-blue-100 text-blue-800 text-[8px] px-1.5 py-0.5 rounded-full font-mono">
        ${capacity}
      </span>
    </div>`
      : ''

  return `
  ${hideCloseButtonStyle}
  <div class="max-w-xs bg-white rounded-lg shadow-md p-3 border border-gray-200 animate-fadeIn">
    <div class="mb-1">
      ${typeBadge}
    </div>
    <h3 class="text-lg font-bold text-gray-900 mb-1">${title}</h3>
    ${showAdmin ? `<p class="text-[8px] font-medium text-gray-700 mb-1">ID: ${id}</p>` : ''}
    <p class="text-sm text-gray-800 leading-tight whitespace-pre-line">${description || ''}</p>
    ${capHtml}
    <div class="mt-2 text-center">
      <button
        id="route-btn-${id}"
        class="px-2 py-1 text-xs font-semibold bg-blue-600 text-white rounded"
      >
        Vis rute
      </button>
    </div>
  </div>`
}
