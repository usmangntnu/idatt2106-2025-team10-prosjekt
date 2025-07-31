<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useMapStore, useDrawStore } from '@/stores/mapStore.ts'
import { useMap } from '@/composables/useMap.ts'
import type {
  EventResponse,
  EventTypeResponse,
  PositionResponse,
  PositionTypeResponse,
} from '@/types/types.ts'
import type { LatLngLiteral, Map as LeafletMap } from 'leaflet'
import {
  getAllEventTypes,
  getAllEvents,
  getAllPositions,
  getEventById,
  getAllPositionTypes,
  getPositionById,
} from '@/services/mapApi.ts'
import { watch } from 'vue'
import {
  createEvent,
  createPosition,
  deleteEventById,
  deletePositionById,
  updateEvent,
  updatePosition,
} from '@/services/adminApi.ts'
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import type { CrisisLevel, EventStatus } from '@/types/types.ts'

const errorMessage = ref('')
const drawStore = useDrawStore()
const mapStore = useMapStore()
const sidebarOpen = ref(true)
const sidebarWidth = computed(() => (sidebarOpen.value ? '500px' : '150px'))
const selectedEditType = ref<'position' | 'event' | null>(null)
const mode = ref<'positions' | 'events' | 'edit'>('positions')
const drawMode = ref<'draw' | 'mark' | null>(null)
const eventTypes = ref<EventTypeResponse[]>([])
const typeId = ref<number | null>(null)
const title = ref('')
const desc = ref('')
const selectedSeverity = ref<CrisisLevel | ''>('')
const selectedStatus = ref<EventStatus | ''>('')
const eventList = ref<EventResponse[]>([])
const positionList = ref<PositionResponse[]>([])
const positionTypes = ref<PositionTypeResponse[]>([])
const selectedStartTime = ref<Date>(new Date())
const selectedEventId = ref<number | null>(null)
const selectedPositionId = ref<number | null>(null)
const capacity = ref<number | null>(null)
const severities = [
  { label: 'LAV', value: 'LOW' },
  { label: 'MODERAT', value: 'MEDIUM' },
  { label: 'HØY', value: 'HIGH' },
  { label: 'KRITISK', value: 'CRITICAL' },
]
const status = [
  { label: 'KOMMENDE', value: 'UPCOMING' },
  { label: 'AKTIV', value: 'ACTIVE' },
  { label: 'INAKTIV', value: 'INACTIVE' },
  { label: 'AVSLUTTET', value: 'FINISHED' },
]
const severityEmoji: Record<string, string> = {
  LOW: '🟢',
  MEDIUM: '🟡',
  HIGH: '🟠',
  CRITICAL: '🔴',
}
const statusEmoji: Record<string, string> = {
  UPCOMING: '🔵',
  ACTIVE: '🟢',
  INACTIVE: '🟡',
  FINISHED: '🟣',
}
const geometryGeoJson = ref<string>('')

const selectedPositionType = computed(() => {
  return positionTypes.value.find((t) => t.id === typeId.value)
})

watch(selectedEventId, async (id) => {
  if (!id) return
  controls?.clearAllShapes()
  const event = await getEventById(id)
  geometryGeoJson.value = event.geometryGeoJson || ''
})

watch(
  () => drawStore.geoJsonStrings,
  (newArr) => {
    if (newArr.length) {
      geometryGeoJson.value = newArr[newArr.length - 1]
    }
  },
  { deep: true }
)

watch(
  () => mapStore.clickedPositionGeoJson,
  (newVal) => {
    if (newVal) geometryGeoJson.value = newVal
  }
)

function formatPolygon(poly: LatLngLiteral[]): string {
  return poly.map((p) => `${p.lat}, ${p.lng}`).join(' ; ')
}

function formatCircle(c: { center: LatLngLiteral; radius: number }): string {
  return `${c.center.lat}, ${c.center.lng}   r=${Math.round(c.radius)} m`
}

const coordString = computed(() => {
  if (drawStore.circles.length) {
    return formatCircle(drawStore.circles[drawStore.circles.length - 1])
  }
  if (drawStore.polygons.length) {
    return formatPolygon(drawStore.polygons[drawStore.polygons.length - 1])
  }
  if (mapStore.clickedPosition) {
    const { lat, lng } = mapStore.clickedPosition
    return `${lat}, ${lng}`
  }
  return ''
})

/**
 * TODO: Extract ifs to onmounted and simplify the code
 */
let controls: ReturnType<typeof useMap> | null = null
function handleMapReady(m: LeafletMap) {
  controls = useMap(m)
  controls.activateDrawTools()
  controls.beginDrawMarker()
  controls.beginDrawPolygon()

  if (mode.value === 'positions') controls.startPositionDraw()
  else if (mode.value === 'events') controls.startEventDraw()
}
defineExpose({
  handleMapReady,
})

function clearAllInputFields() {
  title.value = ''
  desc.value = ''
  typeId.value = null
  selectedSeverity.value = ''
  selectedStatus.value = ''
  geometryGeoJson.value = ''
  selectedStartTime.value = new Date()
  selectedEventId.value = null
  selectedPositionId.value = null
}

watch(mode, (newMode) => {
  console.log('Mode changed to:', newMode)
  clearAllInputFields()
  if (!controls) return

  if (newMode === 'positions') {
    drawMode.value = null
    selectedEditType.value = null
    controls.clearAllShapes()
    controls.startPositionDraw()
  } else if (newMode === 'events') {
    drawMode.value = null
    selectedEditType.value = null
    controls.clearAllShapes()
    controls.startEventDraw()
  } else if (newMode === 'edit') {
    controls.clearAllShapes()
    controls?.disableDraw()
  }
})

watch(selectedEditType, () => {
  clearAllInputFields()
  if (selectedEditType.value === 'position') {
    controls?.clearAllShapes()
    controls?.startPositionDraw()
  } else if (selectedEditType.value === 'event') {
    controls?.clearAllShapes()
    controls?.startEventDraw()
  } else {
    controls?.clearAllShapes()
    drawMode.value = null
  }
})

watch(
  [selectedEditType, selectedEventId, selectedPositionId],
  async ([editType, eId, pId]) => {
    if (editType === 'event' && eId) {
      const event = await getEventById(eId)
      drawEventOnMap(event)
      const cleanIso = event.startTime.replace(/\[.*\]$/, '')
      selectedStartTime.value = new Date(cleanIso)
      title.value = event.title
      desc.value = event.description
      typeId.value = event.eventType.id
      selectedSeverity.value = event.severity.toUpperCase() as CrisisLevel
      selectedStatus.value = event.status
    }

    if (editType === 'position' && pId) {
      const position = await getPositionById(pId)
      drawPositionOnMap(position)
      title.value = position.title
      desc.value = position.description
      typeId.value = position.type.id
    }
  }
)

/**
 * TODO: Maybe extract this function to its appropriate class during component migration

 * @param message the error message to be displayed
 */
function showError(message: string) {
  errorMessage.value = message
  setTimeout(() => {
    errorMessage.value = ''
  }, 3000)
}

/**
 * TODO: Extract this function to its appropriate class during component migration
 */
function showSuccess(message: string) {
  errorMessage.value = message
  setTimeout(() => {
    errorMessage.value = ''
  }, 3000)
}

/**
 * TODO: Extract this function to a composable
 * Function to draw the marker on the map, takes in the position object to be drawn and passes it to the appropriate
 * draw function.
 *
 * @param position an {@link PositionResponse} object to be drawn on the map
 */
function drawPositionOnMap(position: PositionResponse) {
  if (!controls) return

  controls?.drawMarker(position)
  return
}

/**
 * TODO: Extract this function to a composable
 * Function to draw the event on the map, takes in the event object to be drawn and passes it to the appropriate
 * draw function.
 *
 * @param event an {@link EventResponse} object to be drawn on the map
 */
function drawEventOnMap(event: EventResponse) {
  if (!controls) return

  if (event.circleData) {
    const center = {
      lat: event.circleData.latitude,
      lng: event.circleData.longitude,
    }

    controls?.drawCircleFromCenter(event, center, event.circleData.radius)
    return
  }

  if (event.geometryGeoJson) {
    controls.drawEventOnMap(event)
  }
}

function handleSavePosition() {
  const pos = mapStore.clickedPosition

  if (!title.value || !title.value.trim()) {
    showError('Tittel mangler. En posisjon må ha en tittel')
    return
  }
  if (!desc.value || !desc.value.trim()) {
    showError(
      'Beskrivelse mangler. Gi en beskrivelse av posisjonen, for eksempel kontaktinfo eller åpningstider'
    )
    return
  }
  if (typeId.value == null) {
    showError('Type posisjon må velges. For eksempel tilfluktsrom eller annet')
    return
  }
  if (!pos || pos.lat == null || pos.lng == null) {
    showError(
      'Koordinater mangler. Bruk markør-ikonet på kartet for å velge posisjon'
    )
    return
  }
  if (isNaN(pos.lat) || isNaN(pos.lng)) {
    showError('Koordinatene må være gyldige tall')
    return
  }
  if (pos.lat < -90 || pos.lat > 90 || pos.lng < -180 || pos.lng > 180) {
    showError('Koordinatene er utenfor gyldig område')
    return
  }
  if (
    capacity.value !== null &&
    (isNaN(capacity.value) || capacity.value < 0)
  ) {
    showError('Kapasitet må være et positivt tall')
    return
  }

  savePosition(
    title.value.trim(),
    pos.lng,
    pos.lat,
    desc.value.trim(),
    typeId.value,
    capacity.value
  )
}

/**
 * Saves the position to the server.
 * TODO - Extract method to a separate utils class, maybe mapUtils.ts
 *
 * @param lng the longitude of the position
 * @param lat the latitude of the position
 * @param desc the description of the position
 * @param title the title of the position
 * @param typeId the type of the position
 * @param capacity
 * @constructor an async function that saves the position to the server
 */
const savePosition = async (
  title: string,
  lng: number,
  lat: number,
  desc: string,
  typeId: number,
  capacity: number | null
) => {
  if (!title || !lng || !lat || !desc || !typeId) {
    return
  }
  try {
    const description = desc.toString()
    const response = await createPosition({
      title,
      longitude: lng,
      latitude: lat,
      description,
      typeId,
      capacity,
    })
    console.log('Position saved:', response)
    controls?.clearAllShapes()
    controls?.drawMarker(response)
    showSuccess('Vellykket! Posisjonen er lagret.')
  } catch (error) {
    console.error('Error saving position:', error)
  }
}

/**
 * TODO: Extract this method to a composable class
 * Helper method to assist updatePosition not receive any null or other odd values
 */
function handleUpdatePosition() {
  const pos = mapStore.clickedPosition

  if (!selectedPositionId.value) {
    showError(
      'Ingen posisjon er valgt for oppdatering. Velg en posisjon fra listen'
    )
    return
  }

  if (!title.value || !title.value.trim()) {
    showError('Tittel mangler eller er ugyldig')
    return
  }

  if (!desc.value || !desc.value.trim()) {
    showError('Beskrivelse mangler eller er ugyldig')
    return
  }

  if (typeId.value == null) {
    showError('Type posisjon må velges')
    return
  }

  if (!pos || pos.lat == null || pos.lng == null) {
    showError('Koordinater mangler. Velg posisjon på kartet')
    return
  }

  if (isNaN(pos.lat) || isNaN(pos.lng)) {
    showError('Koordinatene må være gyldige tall')
    return
  }

  if (pos.lat < -90 || pos.lat > 90 || pos.lng < -180 || pos.lng > 180) {
    showError('Koordinatene er utenfor gyldig område')
    return
  }

  if (
    capacity.value !== null &&
    (isNaN(capacity.value) || capacity.value < 0)
  ) {
    showError('Kapasitet må være et positivt tall eller være tomt')
    return
  }

  positionUpdate(
    selectedPositionId.value,
    title.value.trim(),
    pos.lng,
    pos.lat,
    desc.value.trim(),
    typeId.value,
    capacity.value
  )
}

/**
 * TODO: Extract this method to a composable class
 * Method to update a position
 *
 * @param id the id of the position
 * @param title the title of the position
 * @param longitude
 * @param latitude
 * @param desc the description of the position
 * @param typeId the type of the position
 * @param capacity the capacity of the position
 * @constructor an async function that updates the position
 */
const positionUpdate = async (
  id: number,
  title: string,
  longitude: number,
  latitude: number,
  desc: string,
  typeId: number,
  capacity: number | null
) => {
  console.log('Updating position:', {
    id,
    title,
    longitude,
    latitude,
    desc,
    typeId,
    capacity,
  })
  if (!title && !longitude && !latitude && !desc && !typeId && !capacity) {
    return
  }
  try {
    const description = desc.toString()
    const response = await updatePosition(id, {
      title,
      longitude,
      latitude,
      description,
      typeId,
      capacity,
    })
    console.log('Position updated with following response fields:', response)
    controls?.clearAllShapes()
    controls?.drawMarker(response)
    showSuccess('Vellykket! Posisjonen er oppdatert.')
  } catch (error) {
    console.error('Error updating position:', error)
  }
}

/**
 * TODO: Extract this method to a composable class
 * Helper method to assist saveEvent not receive any null or other odd values
 */
function handleSaveEvent() {
  if (!title.value || !title.value.trim()) {
    showError('Tittel mangler eller er ugyldig')
    return
  }

  if (!desc.value || !desc.value.trim()) {
    showError('Beskrivelse mangler eller er ugyldig')
    return
  }

  if (!typeId.value) {
    showError('Type hendelse må velges')
    return
  }

  if (!selectedSeverity.value) {
    showError('Krisenivå må velges')
    return
  }

  if (!selectedStatus.value) {
    showError('Status må velges')
    return
  }

  if (
    !(selectedStartTime.value instanceof Date) ||
    isNaN(selectedStartTime.value.getTime())
  ) {
    showError('Starttidspunktet er ugyldig')
    return
  }

  let geoJson = drawStore.geoJsonStrings.at(-1)

  if (mode.value === 'events' && mapStore.clickedPosition) {
    const { lat, lng } = mapStore.clickedPosition

    if (
      lat == null ||
      lng == null ||
      isNaN(lat) ||
      isNaN(lng) ||
      lat < -90 ||
      lat > 90 ||
      lng < -180 ||
      lng > 180
    ) {
      showError('Koordinatene for punktet er ugyldige')
      return
    }

    geoJson = JSON.stringify({
      type: 'Point',
      coordinates: [lng, lat],
    })
  }

  if (!geoJson) {
    showError('Du må markere et område eller punkt på kartet')
    return
  }

  saveEvent(
    title.value.trim(),
    desc.value.trim(),
    geoJson,
    typeId.value,
    selectedSeverity.value,
    selectedStatus.value,
    selectedStartTime.value
  )
}

/**
 * TODO: Extract this method to a composable class
 * Method to delete a position by its position id
 *
 * @param id the id of the position
 * @constructor an async function that deletes the position
 */
const deletePosition = async (id: number | null) => {
  if (!id || id === null) {
    showError(
      'Ingen posisjon er valgt eller tilgjengelig for sletting. Velg en posisjon fra listen'
    )
    return
  }

  try {
    console.log('Deleting position:', {
      id,
    })
    const response = await deletePositionById(id)
    positionList.value = positionList.value.filter((p) => p.id !== id)
    controls?.clearAllShapes()
    console.log('Position deleted:', response)
    showSuccess('Vellykket! Posisjonen er slettet.')
  } catch (error) {
    console.error('Error deleting position:', error)
  }
}

/**
 * Method to save the event to the server.
 *
 * @param title the title of the event
 * @param desc the description of the event
 * @param geometryGeoJson the geometry of the event in GeoJSON format
 * @param typeId the type of the event
 * @param severity the crisis level of the event
 * @param selectedStatus the status of the event
 * @param dateTime the start time of the event
 * @constructor an async function that saves the event to the server
 */
const saveEvent = async (
  title: string,
  desc: string,
  geometryGeoJson: string,
  typeId: number | null,
  severity: string,
  selectedStatus: EventStatus | '',
  dateTime: Date
) => {
  if (!title || !geometryGeoJson || !typeId || !severity) {
    console.error('Missing required fields. Missing:', {
      title,
      geometryGeoJson,
      typeId,
      severity,
    })
    return
  }
  try {
    let circleData = undefined
    if (drawStore.circles.length) {
      const circle = drawStore.circles[drawStore.circles.length - 1]
      circleData = {
        latitude: circle.center.lat,
        longitude: circle.center.lng,
        radius: circle.radius,
      }
      console.log('Circle data:', circleData)
    }

    const description = desc.toString()
    severity = severity.toString().toUpperCase()

    const response = await createEvent({
      title,
      description,
      geometryGeoJson,
      typeId,
      startTime: dateTime.toISOString(),
      status: selectedStatus as EventStatus,
      severity,
      circleData,
    })
    console.log('Event saved:', response)
    controls?.clearAllShapes()
    controls?.drawEventOnMap(response)
    showSuccess('Vellykket! Hendelsen er lagret.')
  } catch (error) {
    console.error('Error saving event:', error)
  }
}

function handleUpdateEvent() {
  if (!selectedEventId.value) {
    showError('Ingen hendelse er valgt for oppdatering')
    return
  }

  if (!title.value || !title.value.trim()) {
    showError('Tittel mangler eller er ugyldig')
    return
  }

  if (!desc.value || !desc.value.trim()) {
    showError('Beskrivelse mangler eller er ugyldig')
    return
  }

  if (typeId.value == null) {
    showError('Type hendelse må velges')
    return
  }

  if (!selectedSeverity.value) {
    showError('Krisenivå må velges')
    return
  }

  if (!selectedStatus.value) {
    showError('Status må velges')
    return
  }

  if (
    !(selectedStartTime.value instanceof Date) ||
    isNaN(selectedStartTime.value.getTime())
  ) {
    showError('Starttidspunktet er ugyldig')
    return
  }

  updateEventButton(
    selectedEventId.value,
    title.value.trim(),
    desc.value.trim(),
    typeId.value,
    selectedSeverity.value,
    selectedStatus.value,
    selectedStartTime.value
  )
}

/**
 * Method to update an event.
 * Finds out whether the event to be updated is a point, polygon or circle by checking the
 * draw - or clickstore from mapStore.
 *
 * @param id the id of the event
 * @param title the title of the event
 * @param desc the description of the event
 * @param typeId the type of the event
 * @param severity the crisis level of the event
 * @param geometryGeoJson the geometry of the event in GeoJSON format
 * @param selectedStatus
 * @param dateTime
 */
const updateEventButton = async (
  id: number,
  title: string,
  desc: string,
  typeId: number,
  severity: string,
  selectedStatus: EventStatus | '',
  dateTime: Date
) => {
  if (!id && !title && !typeId && !severity) {
    return
  }

  try {
    let circleData = undefined
    if (drawStore.circles.length) {
      const circle = drawStore.circles[drawStore.circles.length - 1]
      circleData = {
        latitude: circle.center.lat,
        longitude: circle.center.lng,
        radius: circle.radius,
      }
      console.log('Circle data:', circleData)
    }

    const description = desc.toString()
    severity = severity.toString().toUpperCase()
    console.log('Updating event:', {
      id,
      title,
      description,
      geometryGeoJson,
      typeId,
      severity,
      status: selectedStatus as EventStatus,
      dateTime: dateTime.toISOString(),
    })
    const response = await updateEvent(id, {
      title,
      description,
      geometryGeoJson: geometryGeoJson.value,
      typeId,
      startTime: dateTime.toISOString(),
      severity,
      status: selectedStatus as EventStatus,
      circleData,
    })
    console.log('Event updated:', response)
    controls?.clearAllShapes()
    controls?.drawEventOnMap(response)
    showSuccess('Vellykket! Hendelsen er oppdatert.')
  } catch (error) {
    console.error('Error updating event:', error)
  }
}

/**
 * Method to delete an even, as determined by the id
 *
 * @param id the id of the event
 * @constructor an async function that deletes the event
 */
const deleteEvent = async (id: number) => {
  if (!id) {
    return
  }
  try {
    console.log('Deleting event:', {
      id,
    })
    const response = await deleteEventById(id)
    eventList.value = eventList.value.filter((e) => e.id !== id)
    controls?.clearAllShapes()
    console.log('Event deleted:', response)
    showSuccess('Vellykket! Hendelsen er slettet.')
  } catch (error) {
    console.error('Error deleting event:', error)
  }
}

onMounted(async () => {
  console.log('MapAdminPanel mounted')

  eventList.value = await getAllEvents()
  positionList.value = await getAllPositions()

  try {
    positionTypes.value = await getAllPositionTypes()
    console.log('Position types:', positionTypes.value)
  } catch (e) {
    positionTypes.value = []
    console.error('Klarte ikke hente posisjonstyper', e)
  }

  if (mode.value === 'positions') {
    drawMode.value = 'mark'
    controls?.clearAllShapes()
    controls?.startPositionDraw()
  } else if (mode.value === 'events') {
    drawMode.value = 'draw'
    controls?.clearAllShapes()
    controls?.startEventDraw()
  }

  try {
    eventTypes.value = await getAllEventTypes()
    console.log('Event types:', eventTypes.value)
  } catch (e) {
    eventTypes.value = []
    console.error('Klarte ikke hente event-types', e)
  }

  controls?.drawAllEventsFromEvents(eventList.value, false)
  controls?.drawAllPositionFromPositions(positionList.value, false)
})

const showingPositions = ref(true)
const showingEvents = ref(true)

function togglePositions() {
  showingPositions.value = !showingPositions.value
  if (showingPositions.value) {
    controls?.drawAllPositionFromPositions(positionList.value, false)
  } else {
    controls?.clearAllPositionsFromMap()
  }
}

function toggleEvents() {
  showingEvents.value = !showingEvents.value
  if (showingEvents.value) {
    controls?.drawAllEventsFromEvents(eventList.value, false)
  } else {
    controls?.clearAllEventsFromMap()
  }
}
</script>

<template>
  <div
    class="sidebar absolute top-3 left-3 z-50 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 transform scale-75 origin-top-left"
    :style="{ width: sidebarWidth }"
  >
    <div
      class="overflow-y-auto scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-transparent"
      :class="sidebarOpen ? 'max-h-[75vh] pb-3' : 'max-h-[15vh]'"
    >
      <div class="flex items-center justify-between px-4 py-2">
        <h2 class="text-xl font-bold text-gray-900">Kartadministrasjon</h2>
      </div>
      <template v-if="sidebarOpen">
        <!-- Mode Toggle -->
        <div class="px-4 py-3 bg-white">
          <div class="mb-2 flex space-x-2 p-2 bg-gray-100 rounded-lg">
            <button
              @click="togglePositions"
              class="flex-1 py-1 px-2 rounded-sm text-xs font-medium transition-colors duration-200 flex items-center justify-center"
              :class="
                showingPositions
                  ? 'bg-amber-500 text-white hover:bg-amber-600'
                  : 'bg-green-500 text-white hover:bg-green-600'
              "
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-3 w-3 mr-1"
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
              {{
                showingPositions
                  ? 'Skjul alle posisjoner'
                  : 'Vis alle posisjoner'
              }}
            </button>

            <button
              @click="toggleEvents"
              class="flex-1 py-1 px-2 rounded-sm text-xs font-medium transition-colors duration-200 flex items-center justify-center"
              :class="
                showingEvents
                  ? 'bg-amber-500 text-white hover:bg-amber-600'
                  : 'bg-green-500 text-white hover:bg-green-600'
              "
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-3 w-3 mr-1"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>
              {{
                showingEvents ? 'Skjul alle hendelser' : 'Vis alle hendelser'
              }}
            </button>
          </div>
          <div class="flex gap-1 p-1 bg-gray-100 rounded-lg">
            <button
              class="flex-1 py-2 rounded-md text-xs font-medium transition-all duration-200"
              :class="
                mode === 'positions'
                  ? 'bg-blue-600 text-white shadow-sm'
                  : 'text-gray-700 hover:bg-gray-200'
              "
              @click="mode = 'positions'"
            >
              Ny posisjon
            </button>
            <button
              class="flex-1 py-2 rounded-md text-xs font-medium transition-all duration-200"
              :class="
                mode === 'events'
                  ? 'bg-blue-600 text-white shadow-sm'
                  : 'text-gray-700 hover:bg-gray-200'
              "
              @click="mode = 'events'"
            >
              Ny hendelse
            </button>
            <button
              class="flex-1 py-2 rounded-md text-xs font-medium transition-all duration-200"
              :class="
                mode === 'edit'
                  ? 'bg-blue-600 text-white shadow-sm'
                  : 'text-gray-700 hover:bg-gray-200'
              "
              @click="mode = 'edit'"
            >
              Endre/Slett
            </button>
          </div>
        </div>

        <!-- Posisjonsadministrasjon -->
        <div v-if="mode === 'positions'" class="px-4 py-2">
          <div class="mb-4">
            <h3
              class="text-lg font-semibold text-gray-900 mb-2 flex items-center"
            >
              <div class="p-1 mr-2 rounded-full bg-blue-100 text-blue-600">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4"
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
              Legg til posisjon
            </h3>

            <div class="space-y-3">
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Tittel</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  v-model="title"
                />
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Type posisjon</label
                >
                <select
                  v-model="typeId"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                >
                  <option v-for="t in positionTypes" :key="t.id" :value="t.id">
                    {{ t.name }}
                  </option>
                </select>
              </div>

              <div
                v-if="selectedPositionType?.name === 'Tilfluktsrom'"
                class="bg-gray-50 p-3 rounded-lg border border-gray-100"
              >
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Kapasitet</label
                >
                <input
                  type="number"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  v-model="capacity"
                  placeholder="F.eks. 100"
                />
                <p class="text-[10px] text-gray-600 mt-1 italic">
                  Angi kapasitet for dette tilfluktsrommet. La feltet stå tomt
                  hvis ikke aktuelt.
                </p>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Beskrivelse</label
                >
                <textarea
                  v-model="desc"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  rows="3"
                  placeholder="F.eks. åpningstider, kontaktinfo"
                ></textarea>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <h4 class="text-xs font-medium text-gray-700 mb-1">
                  Områdevalg
                </h4>
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Koordinater</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs font-mono focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  :value="
                    mapStore.clickedPosition
                      ? `${mapStore.clickedPosition.lat}, ${mapStore.clickedPosition.lng}`
                      : ''
                  "
                />
                <p class="text-[10px] text-gray-600 mt-1">
                  Skriv inn manuelt eller marker på kartet.
                </p>
              </div>
            </div>

            <button
              class="w-full bg-green-600 text-white py-2 px-4 rounded-md text-base font-medium hover:bg-green-700 transition-colors duration-200 shadow-sm mt-4 flex items-center justify-center"
              @click="handleSavePosition"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4 mr-1"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
              Lagre posisjon
            </button>
          </div>
        </div>

        <!-- Hendelsesadministrasjon -->
        <div v-if="mode === 'events'" class="px-4 py-2">
          <div class="mb-4">
            <h3
              class="text-lg font-semibold text-gray-900 mb-2 flex items-center"
            >
              <div class="p-1 mr-2 rounded-full bg-red-100 text-red-600">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4"
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
              Opprett ny hendelse
            </h3>

            <div class="space-y-3">
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Tittel</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  v-model="title"
                />
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Type hendelse</label
                >
                <select
                  v-model="typeId"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                >
                  <option v-for="t in eventTypes" :key="t.id" :value="t.id">
                    {{ t.name }}
                  </option>
                </select>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label
                  for="severity"
                  class="text-xs font-medium text-gray-700 block mb-1"
                  >Krisenivå</label
                >
                <div class="relative">
                  <select
                    id="severity"
                    v-model="selectedSeverity"
                    class="w-full border border-gray-300 p-2 rounded-md text-xs pl-8 pr-8 appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg krisenivå --</option>
                    <option
                      v-for="s in severities"
                      :key="s.value"
                      :value="s.value"
                    >
                      {{ severityEmoji[s.value] }} {{ s.label }}
                    </option>
                  </select>
                  <div
                    class="pointer-events-none absolute inset-y-0 right-2 flex items-center"
                  >
                    <span
                      class="block w-3 h-3 rounded-full"
                      :class="{
                        'bg-green-500': selectedSeverity === 'LOW',
                        'bg-yellow-500': selectedSeverity === 'MEDIUM',
                        'bg-orange-500': selectedSeverity === 'HIGH',
                        'bg-red-500': selectedSeverity === 'CRITICAL',
                        'bg-gray-300': !selectedSeverity,
                      }"
                    ></span>
                  </div>
                </div>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Starttidspunkt</label
                >
                <Datepicker
                  v-model="selectedStartTime"
                  :enable-time-picker="true"
                  :is-24="true"
                  :minute-increment="5"
                  :clearable="true"
                  input-class-name="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                  type="button"
                  class="w-full mt-1 bg-blue-600 text-white py-1 rounded-md text-xs font-medium hover:bg-blue-700 transition-colors duration-200 shadow-sm"
                  @click="selectedStartTime = new Date()"
                >
                  Sett til nå
                </button>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label
                  for="status"
                  class="text-xs font-medium text-gray-700 block mb-1"
                  >Status</label
                >
                <div class="relative">
                  <select
                    id="status"
                    v-model="selectedStatus"
                    class="w-full border border-gray-300 p-2 rounded-md text-xs pl-8 pr-8 appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg status --</option>
                    <option v-for="s in status" :key="s.value" :value="s.value">
                      {{ statusEmoji[s.value] }} {{ s.label }}
                    </option>
                  </select>
                  <div
                    class="pointer-events-none absolute inset-y-0 right-2 flex items-center"
                  >
                    <span
                      class="block w-3 h-3 rounded-full"
                      :class="{
                        'bg-blue-500': selectedStatus === 'UPCOMING',
                        'bg-green-500': selectedStatus === 'ACTIVE',
                        'bg-gray-500': selectedStatus === 'INACTIVE',
                        'bg-gray-800': selectedStatus === 'FINISHED',
                        'bg-gray-300': !selectedStatus,
                      }"
                    ></span>
                  </div>
                </div>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Beskrivelse</label
                >
                <textarea
                  v-model="desc"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  rows="3"
                  placeholder="F.eks. åpningstider..."
                ></textarea>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <h4 class="text-xs font-medium text-gray-700 mb-1">
                  Områdevalg
                </h4>
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Koordinater</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs font-mono focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  :value="coordString"
                />
                <p class="text-[10px] text-gray-600 mt-1">
                  Trykk på kartet for å definere et punkt eller polygon.
                </p>
                <p class="text-[10px] text-gray-500 italic mt-1">
                  Markerte posisjoner vises automatisk.
                </p>
                <p class="text-[10px] text-gray-500 italic mt-1">
                  Må markere minst ett område.
                </p>
              </div>
            </div>

            <button
              class="w-full bg-green-600 text-white py-2 px-4 rounded-md text-base font-medium hover:bg-green-700 transition-colors duration-200 shadow-sm mt-4 flex items-center justify-center"
              @click="handleSaveEvent"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-4 w-4 mr-1"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
              Opprett hendelse
            </button>
          </div>
        </div>

        <!-- Endre/Slett -->
        <div v-if="mode === 'edit'" class="px-4 py-2">
          <div class="mb-4">
            <h3
              class="text-lg font-semibold text-gray-900 mb-2 flex items-center"
            >
              <div class="p-1 mr-2 rounded-full bg-purple-100 text-purple-600">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-4 w-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                  />
                </svg>
              </div>
              Endre eller slett
            </h3>

            <div class="bg-gray-50 p-3 rounded-lg border border-gray-100 mb-3">
              <div class="flex gap-1 p-1 bg-gray-200 rounded-lg">
                <button
                  class="flex-1 py-2 rounded-md text-xs font-medium transition-all duration-200"
                  :class="
                    selectedEditType === 'position'
                      ? 'bg-blue-600 text-white shadow-sm'
                      : 'text-gray-700 hover:bg-gray-300'
                  "
                  @click="selectedEditType = 'position'"
                >
                  Endre posisjon
                </button>
                <button
                  class="flex-1 py-2 rounded-md text-xs font-medium transition-all duration-200"
                  :class="
                    selectedEditType === 'event'
                      ? 'bg-blue-600 text-white shadow-sm'
                      : 'text-gray-700 hover:bg-gray-300'
                  "
                  @click="selectedEditType = 'event'"
                >
                  Endre hendelse
                </button>
              </div>
            </div>

            <div
              v-if="!selectedEditType"
              class="flex items-center justify-center p-3 bg-gray-50 rounded-lg border border-gray-100"
            >
              <p class="text-gray-600 italic text-center">
                Velg hva du vil redigere eller slette.
              </p>
            </div>

            <!-- Position Edit -->
            <div v-if="selectedEditType === 'position'" class="space-y-3">
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Velg/Skriv id</label
                >
                <div class="flex gap-2 items-stretch">
                  <input
                    type="number"
                    class="flex-1 border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    v-model="selectedPositionId"
                    placeholder="F.eks. 123"
                  />
                </div>

                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Velg posisjon (tittel)</label
                >
                <div class="flex gap-2 items-stretch">
                  <select
                    v-model="selectedPositionId"
                    class="flex-1 border border-gray-300 p-2 rounded-md text-xs appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg --</option>
                    <option v-for="p in positionList" :key="p.id" :value="p.id">
                      {{ p.title }}
                    </option>
                  </select>
                </div>
                <div class="flex gap-2 mt-2">
                  <button
                    class="flex-1 bg-blue-600 text-white py-1 rounded-md text-xs font-medium hover:bg-blue-700 transition-colors duration-200 flex items-center justify-center"
                    @click="
                      controls?.clearAllShapes();
                      clearAllInputFields();
                      togglePositions();
                      toggleEvents();
                    "
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-3 w-3 mr-1"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                      />
                    </svg>
                    Nullstill
                  </button>
                </div>
              </div>
              <div
                v-if="selectedPositionType?.name === 'Tilfluktsrom'"
                class="bg-gray-50 p-3 rounded-lg border border-gray-100"
              >
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Kapasitet</label
                >
                <input
                  type="number"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  v-model="capacity"
                  placeholder="F.eks. 100"
                />
                <p class="text-[10px] text-gray-600 mt-1 italic">
                  Kapasitet er valgfritt. La stå tomt hvis ikke aktuelt.
                </p>
              </div>
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Beskrivelse</label
                >
                <textarea
                  v-model="desc"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  rows="3"
                  placeholder="F.eks. åpningstider, kontaktinfo"
                ></textarea>
              </div>
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <h4 class="text-xs font-medium text-gray-700 mb-1">
                  Områdevalg
                </h4>
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Koordinater</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs font-mono focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  :value="coordString"
                />
                <p class="text-[10px] text-gray-600 mt-1 italic">
                  Trykk på kartet for punkt eller polygon.
                </p>
              </div>
              <div class="flex gap-1 mt-3">
                <button
                  class="flex-1 bg-yellow-600 text-white py-2 rounded-md text-xs font-medium hover:bg-yellow-700 transition-colors duration-200 shadow-sm flex items-center justify-center"
                  @click="handleUpdatePosition"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-3 w-3 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                    />
                  </svg>
                  Oppdater
                </button>
                <button
                  class="flex-1 bg-red-600 text-white py-2 rounded-md text-xs font-medium hover:bg-red-700 transition-colors duration-200 shadow-sm flex items-center justify-center"
                  @click="deletePosition(selectedPositionId)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-3 w-3 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                  Slett
                </button>
              </div>
            </div>

            <!-- Event Edit -->
            <div v-if="selectedEditType === 'event'" class="space-y-3">
              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Velg/Skriv id</label
                >
                <div class="flex gap-2 items-stretch">
                  <input
                    type="number"
                    class="flex-1 border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                    v-model="selectedEventId"
                    placeholder="F.eks. 123"
                  />
                </div>

                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Velg hendelse</label
                >
                <div class="flex gap-2 items-stretch">
                  <select
                    v-model="selectedEventId"
                    class="flex-1 border border-gray-300 p-2 rounded-md text-xs appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg --</option>
                    <option v-for="e in eventList" :key="e.id" :value="e.id">
                      {{ e.title }}
                    </option>
                  </select>
                </div>
                <div class="flex gap-2 mt-2">
                  <button
                    class="flex-1 bg-blue-600 text-white py-1 rounded-md text-xs font-medium hover:bg-blue-700 transition-colors duration-200 shadow-sm flex items-center justify-center"
                    @click="
                      controls?.clearAllShapes();
                      clearAllInputFields();
                      togglePositions();
                      toggleEvents();
                    "
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-3 w-3 mr-1"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                      />
                    </svg>
                    Nullstill
                  </button>
                </div>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Tittel</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  v-model="title"
                />
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Type hendelse</label
                >
                <select
                  v-model="typeId"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                >
                  <option disabled value="">-- Velg --</option>
                  <option v-for="t in eventTypes" :key="t.id" :value="t.id">
                    {{ t.name }}
                  </option>
                </select>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label
                  for="severity"
                  class="text-xs font-medium text-gray-700 block mb-1"
                  >Krisenivå</label
                >
                <div class="relative">
                  <select
                    id="severity"
                    v-model="selectedSeverity"
                    class="w-full border border-gray-300 p-2 rounded-md text-xs pl-8 pr-8 appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg krisenivå --</option>
                    <option
                      v-for="s in severities"
                      :key="s.value"
                      :value="s.value"
                    >
                      {{ severityEmoji[s.value] }} {{ s.label }}
                    </option>
                  </select>
                  <div
                    class="pointer-events-none absolute inset-y-0 right-2 flex items-center"
                  >
                    <span
                      class="block w-3 h-3 rounded-full"
                      :class="{
                        'bg-green-500': selectedSeverity === 'LOW',
                        'bg-yellow-500': selectedSeverity === 'MEDIUM',
                        'bg-orange-500': selectedSeverity === 'HIGH',
                        'bg-red-500': selectedSeverity === 'CRITICAL',
                        'bg-gray-300': !selectedSeverity,
                      }"
                    ></span>
                  </div>
                </div>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Endre Starttidspunkt</label
                >
                <Datepicker
                  v-model="selectedStartTime"
                  :enable-time-picker="true"
                  :is-24="true"
                  :minute-increment="5"
                  :clearable="true"
                  input-class-name="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2	focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                />
                <button
                  type="button"
                  class="w-full mt-1 bg-blue-600 text-white py-1 rounded-md text-xs font-medium hover:bg-blue-700 transition-colors duration-200 shadow-sm"
                  @click="selectedStartTime = new Date()"
                >
                  Sett til nå
                </button>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label
                  for="status"
                  class="text-xs font-medium text-gray-700 block mb-1"
                  >Status</label
                >
                <div class="relative">
                  <select
                    id="status"
                    v-model="selectedStatus"
                    class="w-full border border-gray-300 p-2 rounded-md text-xs pl-8 pr-8 appearance-none bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  >
                    <option disabled value="">-- Velg status --</option>
                    <option v-for="s in status" :key="s.value" :value="s.value">
                      {{ statusEmoji[s.value] }} {{ s.label }}
                    </option>
                  </select>
                  <div
                    class="pointer-events-none absolute inset-y-0 right-2 flex items-center"
                  >
                    <span
                      class="block w-3 h-3 rounded-full"
                      :class="{
                        'bg-blue-500': selectedStatus === 'UPCOMING',
                        'bg-green-500': selectedStatus === 'ACTIVE',
                        'bg-gray-500': selectedStatus === 'INACTIVE',
                        'bg-gray-800': selectedStatus === 'FINISHED',
                        'bg-gray-300': !selectedStatus,
                      }"
                    ></span>
                  </div>
                </div>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Beskrivelse</label
                >
                <textarea
                  v-model="desc"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  rows="3"
                  placeholder="F.eks. åpningstider..."
                ></textarea>
              </div>

              <div class="bg-gray-50 p-3 rounded-lg border border-gray-100">
                <h4 class="text-xs font-medium text-gray-700 mb-1">
                  Områdevalg
                </h4>
                <label class="text-xs font-medium text-gray-700 block mb-1"
                  >Koordinater</label
                >
                <input
                  type="text"
                  class="w-full border border-gray-300 p-2 rounded-md text-xs font-mono focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
                  :value="coordString"
                />
                <p class="text-[10px] text-gray-600 mt-1">
                  Trykk på kartet for punkt eller polygon.
                </p>
                <p class="text-[10px] text-gray-500 italic mt-1">
                  Markerte posisjoner vises automatisk.
                </p>
                <p class="text-[10px] text-gray-500 italic mt-1">
                  Må markere minst ett område.
                </p>
              </div>

              <div class="flex gap-1 mt-3">
                <button
                  class="flex-1 bg-yellow-600 text-white py-2 rounded-md text-xs font-medium hover:bg-yellow-700 transition-colors duration-200 shadow-sm flex items-center justify-center"
                  @click="handleUpdateEvent"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-3 w-3 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5	h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                    />
                  </svg>
                  Oppdater
                </button>
                <button
                  class="flex-1 bg-red-600 text-white py-2 rounded-md text-xs font-medium hover:bg-red-700 transition-colors duration-200 shadow-sm flex items-center justify-center"
                  @click="deleteEvent(selectedEventId ?? -1)"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-3 w-3 mr-1"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7	m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                  Slett
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Error Message Display -->
        <div
          v-if="errorMessage"
          class="fixed bottom-3 left-1/2 transform -translate-x-1/2 px-4 py-2 rounded-md text-white shadow-lg text-center max-w-xs"
          :class="
            errorMessage.includes('Vellykket') ? 'bg-green-500' : 'bg-red-500'
          "
        >
          <div class="flex items-center">
            <svg
              v-if="errorMessage.includes('Vellykket')"
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4	c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
            {{ errorMessage }}
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
#status {
  padding-left: 2.5rem;
  background-position: right 0.75rem center;
}

@media (max-width: 640px) {
  .sidebar {
    width: 100% !important;
    bottom: 0 !important;
    top: auto !important;
    left: 0 !important;
    right: 0 !important;
    height: 50vh !important;
    overflow-y: auto !important;
    border-radius: 0.75rem 0.75rem 0 0; /* Rounded top corners only */
  }
}
</style>
