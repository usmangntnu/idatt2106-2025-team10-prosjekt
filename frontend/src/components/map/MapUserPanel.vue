<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { type Map as LeafletMap } from 'leaflet'
import {
  getAllEvents,
  getAllEventTypes,
  getAllPositions,
  getAllPositionTypes,
  getAllShelters,
} from '@/services/mapApi.ts'
import type {
  EventResponse,
  EventTypeResponse,
  PositionResponse,
  PositionTypeResponse,
} from '@/types/types.ts'
import { useLocationStore } from '@/stores/useLocationStore.ts'
import { useUserStore } from '@/stores/user.ts'
import { severityEmoji, severities } from '@/utils/mapUtils.ts'
import { useMap } from '@/composables/useMap.ts'
import booleanPointInPolygon from '@turf/boolean-point-in-polygon'
import distance from '@turf/distance'
import { createWebSocket } from '@/utils/socket.ts'
import { useHouseholdStore } from '@/stores/household.ts'

let controls: ReturnType<typeof useMap> | null = null

const hasLocation = ref(false)

const severityFilter = ref<string[]>([])
const positionFilter = ref<string[]>([])

const sidebarOpen = ref(true)
const sidebarWidth = computed(() => (sidebarOpen.value ? '300px' : '80px'))

const locationStore = useLocationStore()
const householdStore = useHouseholdStore()
const { isAuthenticated } = useUserStore()

const householdMarkerOn = ref<boolean>(false)

const showEvents = ref(false)
const showPositions = ref(false)
const showHousehold = ref(false)
const showMyPosition = ref(false)

const eventTypeFilter = ref<string[]>([])

const positionTypes = ref<string[]>([])
const eventTypes = ref<string[]>([])

const events = ref<EventResponse[]>([])
const positions = ref<PositionResponse[]>([])

/**
 * Handles the map ready event. When the map is ready, it initializes the methods from the useMap composable used to
 * handle map interactions.
 * @param map The Leaflet map instance.
 */
function handleMapReady(map: LeafletMap) {
  controls = useMap(map)
  // remove all map tools that are linked to the map, like the draw tool, etc
  controls.removeAllMapTools()
}

/**
 * Toggles the visibility of a section in the sidebar.
 * @param section The section to toggle. Can be 'events', 'positions', 'household', or 'myposition'.
 */
function toggleSection(section: string) {
  if (section === 'events') {
    showEvents.value = !showEvents.value
  } else if (section === 'positions') {
    showPositions.value = !showPositions.value
  } else if (section === 'household') {
    showHousehold.value = !showHousehold.value
  } else if (section === 'myposition') {
    showMyPosition.value = !showMyPosition.value
  }
}

defineExpose({ handleMapReady })

watch(
  [severityFilter, eventTypeFilter],
  ([newSeverities, newTypes]) => {
    if (!controls) return

    controls.clearAllEventsFromMap()

    if (severityFilter.value.length == 0 && eventTypeFilter.value.length != 0) {
      const visibleEvents = events.value.filter((e) =>
        newTypes.includes(e.eventType.name)
      )
      for (const ev of visibleEvents) {
        controls.drawEventOnMap(ev, false)
      }
      console.log('trriggered:', eventTypeFilter.value)
      return
    }
    if (eventTypeFilter.value.length == 0 && severityFilter.value.length != 0) {
      const visibleEvents = events.value.filter((e) =>
        newSeverities.includes(e.severity)
      )
      for (const ev of visibleEvents) {
        controls.drawEventOnMap(ev, false)
      }
      console.log('trriggered:', severityFilter.value)
      return
    }
    if (newSeverities.length == 0 && newTypes.length == 0) {
      controls.clearAllEventsFromMap()
      console.log('trriggered:', severityFilter.value, eventTypeFilter.value)
      return
    }

    const visibleEvents = events.value.filter(
      (e) =>
        newSeverities.includes(e.severity) &&
        newTypes.includes(e.eventType.name)
    )

    for (const ev of visibleEvents) {
      controls.drawEventOnMap(ev, false)
    }
  },
  { deep: true }
)
/**
 * Watcher for positionFilter to update the map with positions based on the selected filters.
 */
watch(
  positionFilter,
  (newValue) => {
    if (!controls) return

    controls.clearAllPositionsFromMap()

    console.log(positions.value)

    const visiblePositions = positions.value.filter((p) =>
      newValue.includes(p.type.name)
    )
    controls.drawAllPositionFromPositions(visiblePositions, false)
  },
  { deep: true }
)

function toggleHouseholdPosition() {
  if (!householdMarkerOn.value) {
    const lat = householdStore.household?.latitude
    const lng = householdStore.household?.longitude
    if (lat !== undefined && lng !== undefined) {
      controls?.drawHouseholdOnMap(lat, lng)
      householdMarkerOn.value = true
    }
  } else {
    controls?.clearHouseholdMarker()
    householdMarkerOn.value = false
  }
}

function toggleUserPosition() {
  if (!hasLocation.value) {
    const lat = locationStore.latitude.value
    const lng = locationStore.longitude.value
    if (lat !== null && lng !== null) {
      controls?.drawUserOnMap(lat, lng)
      hasLocation.value = true
    }
  } else {
    controls?.clearUserMarker()
    hasLocation.value = false
  }
}

onMounted(async () => {
  console.log('MapUserPanel mounted')
  createWebSocket(handleIncomingEvent)

  try {
    positionTypes.value = (await getAllPositionTypes()).map(
      (type: PositionTypeResponse) => type.name
    )
    console.log('Position types:', positionTypes.value)
  } catch (e) {
    positionTypes.value = []
    console.error('Klarte ikke hente posisjonstyper', e)
  }

  try {
    eventTypes.value = (await getAllEventTypes()).map(
      (type: EventTypeResponse) => type.name
    )
    console.log('Event types:', eventTypes.value)
  } catch (e) {
    eventTypes.value = []
    console.error('Klarte ikke hente hendelsestyper', e)
  }

  positions.value = await getAllPositions()
  const shelters = await getAllShelters()
  if (Array.isArray(shelters)) {
    positions.value.push(...shelters)
  } else {
    positions.value.push(shelters)
  }
  console.log('Positions:', positions.value)
  events.value = await getAllEvents()
  await householdStore.fetchHousehold()
  locationStore.getLocation()
  console.log(
    'Location store:',
    locationStore.latitude,
    locationStore.longitude
  )

  /**
   * Adding to the map all events from the database that are currently active.
   * by utiliing the watcher on the positionFilter and severityFilter. Only events with status 'ACTIVE' are shown,
   * and the corresponding filters are applied.
   */
  const visibleEvents = events.value.filter((e) => e.status === 'ACTIVE')
  for (const ev of visibleEvents) {
    controls?.drawEventOnMap(ev, false)
  }
})

/**
 * Handles incoming events from the WebSocket connection. It checks if the event is within the user's location and
 * updates the events array accordingly, such that it can be displayed on the map.
 *
 * @param topic the topic of callback
 * @param ev the event data
 */
function handleIncomingEvent({
  topic,
  data: ev,
}: {
  topic: string
  data: EventResponse
}) {
  const lat = locationStore.latitude.value
  const lng = locationStore.longitude.value

  if (lat === null || lng === null) return
  if (topic !== '/topic/events' && topic !== '/topic/events/delete') return

  if (topic === '/topic/events/delete') {
    const deletedId = ev
    const index = events.value.findIndex((e) => e.id === ev.id)
    if (index === -1) {
      return
    }
    controls?.clearEventFromMap(events.value[index])
    severityFilter.value.splice(
      severityFilter.value.indexOf(events.value[index].severity),
      1
    )

    events.value = events.value.filter((e) => e.id !== deletedId.id)
    return
  }

  const userPt: [number, number] = [lng, lat]
  let affected = false

  if (topic === '/topic/events') {
    if (ev.geometryGeoJson) {
      controls?.clearAllEventsFromMap()
      const idx = events.value.findIndex((e) => e.id === ev.id)
      if (idx >= 0) events.value[idx] = ev
      else events.value.push(ev)
      controls?.drawEventOnMap(ev, false)

      const geo = JSON.parse(ev.geometryGeoJson)
      affected = booleanPointInPolygon(userPt, geo)

      if (ev.circleData) {
        const center: [number, number] = [
          ev.circleData.longitude,
          ev.circleData.latitude,
        ]
        const d = distance(center, userPt, { units: 'meters' })
        affected = d <= ev.circleData.radius

        if (affected) {
          controls?.drawEventOnMap(ev, false)
        }
      }
    }
  }
}

</script>
<template>
  <div
    :style="{ width: sidebarWidth }"
    class="absolute top-4 left-4 z-50 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 transform scale-75 origin-top-left"
  >
    <div
      class="overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-transparent"
      :class="sidebarOpen ? 'max-h-[85vh] pb-2' : 'max-h-[20vh]'"
    >
      <!-- Hovedmeny -->
      <div class="menu-items divide-y divide-gray-100">
        <!-- Vis hendelser -->
        <div>
          <div
            @click="toggleSection('events')"
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors duration-200 group"
          >
            <div class="flex items-center space-x-2">
              <div
                class="p-1 rounded-full bg-red-100 text-red-600 group-hover:bg-red-200 transition-colors duration-200"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
              </div>
              <span class="font-medium text-base text-gray-800"
                >Vis hendelser</span
              >
            </div>
            <svg
              class="h-4 w-4 text-gray-500 transition-transform duration-200"
              :class="showEvents ? 'transform rotate-180' : ''"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>

          <!-- Hendelser undermeny -->
          <div v-if="showEvents" class="px-4 py-3 bg-gray-50">
            <div class="mb-3">
              <div class="font-medium text-sm mb-2 text-gray-700">
                Krisenivå:
              </div>
              <div class="space-y-2">
                <div
                  v-for="s in severities"
                  :key="s.value"
                  class="flex items-center"
                >
                  <input
                    :id="`severity-${s.value}`"
                    type="checkbox"
                    class="mr-2 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                    :value="s.value"
                    v-model="severityFilter"
                  />
                  <label
                    :for="`severity-${s.value}`"
                    class="text-sm flex items-center"
                  >
                    <span class="mr-1 text-base">{{
                      severityEmoji[s.value]
                    }}</span>
                    {{ s.label }}
                  </label>
                </div>
              </div>
            </div>

            <div>
              <div class="font-medium text-sm mb-2 text-gray-700">
                Vis hendelsestype:
              </div>
              <div class="space-y-2">
                <div
                  v-for="type in eventTypes"
                  :key="type"
                  class="flex items-center"
                >
                  <input
                    :id="`event-${type}`"
                    type="checkbox"
                    class="mr-2 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                    :value="type"
                    v-model="eventTypeFilter"
                  />
                  <label :for="`event-${type}`" class="text-sm">{{
                    type
                  }}</label>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Vis posisjoner -->
        <div>
          <div
            @click="toggleSection('positions')"
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors duration-200 group"
          >
            <div class="flex items-center space-x-2">
              <div
                class="p-1 rounded-full bg-blue-100 text-blue-600 group-hover:bg-blue-200 transition-colors duration-200"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
              </div>
              <span class="font-medium text-base text-gray-800"
                >Vis posisjoner</span
              >
            </div>
            <svg
              class="h-4 w-4 text-gray-500 transition-transform duration-200"
              :class="showPositions ? 'transform rotate-180' : ''"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>

          <!-- Posisjoner undermeny -->
          <div v-if="showPositions" class="px-4 py-3 bg-gray-50">
            <div class="space-y-2">
              <div
                v-for="type in positionTypes"
                :key="type"
                class="flex items-center"
              >
                <input
                  :id="`pos-${type}`"
                  type="checkbox"
                  class="mr-2 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                  :value="type"
                  v-model="positionFilter"
                />
                <label :for="`pos-${type}`" class="text-sm">{{ type }}</label>
              </div>
            </div>
          </div>
        </div>

        <!-- Min posisjon -->
        <div>
          <div
            @click="toggleSection('myposition')"
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors duration-200 group"
          >
            <div class="flex items-center space-x-2">
              <div
                class="p-1 rounded-full bg-purple-100 text-purple-600 group-hover:bg-purple-200 transition-colors duration-200"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <span class="font-medium text-base text-gray-800"
                >Min posisjon</span
              >
            </div>
            <svg
              class="h-4 w-4 text-gray-500 transition-transform duration-200"
              :class="showMyPosition ? 'transform rotate-180' : ''"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>

          <!-- Min posisjon undermeny -->
          <div v-if="showMyPosition" class="px-10 py-8 bg-gray-50">
            <div class="text-base">
              <div class="flex space-x-3">
                <button
                  :class="[
                    'text-white py-4 px-6 rounded-xl text-xl font-semibold transition-colors duration-200 shadow-md flex items-center',
                    hasLocation
                      ? 'bg-gray-600 hover:bg-gray-700'
                      : 'bg-green-700 hover:bg-green-800',
                  ]"
                  @click="toggleUserPosition"
                >
                  <v-icon
                    class="mr-2"
                    :name="hasLocation ? 'fa-eye-slash' : 'fa-eye'"
                  />
                  {{ hasLocation ? 'Skjul posisjon' : 'Vis posisjon' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Min husstand -->
        <div v-if="isAuthenticated">
          <div
            @click="toggleSection('household')"
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 cursor-pointer transition-colors duration-200 group"
          >
            <div class="flex items-center space-x-2">
              <div
                class="p-1 rounded-full bg-green-100 text-green-600 group-hover:bg-green-200 transition-colors duration-200"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                  />
                </svg>
              </div>
              <span class="font-medium text-base text-gray-800"
                >Min husstand</span
              >
            </div>
            <svg
              class="h-4 w-4 text-gray-500 transition-transform duration-200"
              :class="showHousehold ? 'transform rotate-180' : ''"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>

          <!-- Min husstand undermeny -->
          <div v-if="showHousehold" class="px-10 py-8 bg-gray-50">
            <div class="text-sm">
              <button
                :class="[
                  'text-white py-4 px-6 rounded-xl text-xl font-semibold transition-colors duration-200 shadow-md flex items-center',
                  householdMarkerOn
                    ? 'bg-gray-600 hover:bg-gray-700'
                    : 'bg-green-700 hover:bg-green-800',
                ]"
                @click="toggleHouseholdPosition"
              >
                <v-icon
                  class="mr-2"
                  :name="householdMarkerOn ? 'fa-eye-slash' : 'fa-eye'"
                />
                {{ householdMarkerOn ? 'Skjul husstand' : 'Vis husstand' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Overgang for dropdown-menyene */
.menu-items > div > div:last-child {
  transition: all 0.3s ease;
}
</style>
