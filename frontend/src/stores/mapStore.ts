import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LatLngLiteral } from 'leaflet'

/**
 * Store for managing the clicked position on the map.
 *
 * @returns {Object} - The store object containing the clicked position and methods to set and clear it.
 */
export const useMapStore = defineStore('map', () => {
  const clickedPosition = ref<LatLngLiteral | undefined | null>(null)
  const clickedPositionGeoJson = ref<string | undefined | null>(null)

  function setClickedPositionGeoJson(geoJson: string) {
    clickedPositionGeoJson.value = geoJson
  }

  function setClickedPosition(pos: LatLngLiteral) {
    clickedPosition.value = pos
  }

  function clearClickedPosition() {
    clickedPosition.value = null
  }

  return {
    clickedPosition,
    clickedPositionGeoJson,
    setClickedPositionGeoJson,
    setClickedPosition,
    clearClickedPosition,
  }
})

/**
 * Store for managing polygons and circles drawn on the map.
 *
 * @returns {Object} - The store object containing polygons, circles, and methods to add and clear them.
 */
export const useDrawStore = defineStore('draw', () => {
  const polygons = ref<LatLngLiteral[][]>([]) // array of polygons
  const circles = ref<{ center: LatLngLiteral; radius: number }[]>([])
  const geoJsonStrings = ref<string[]>([])

  /**
   * Adds a polygon to the store.
   *
   * @param {LatLngLiteral[]} polygon - The polygon to add.
   */
  function addPolygon(polygon: LatLngLiteral[]) {
    polygons.value.push(polygon)
  }

  /**
   * Adds a circle to the store.
   *
   * @param {LatLngLiteral} center - The center of the circle.
   * @param {number} radius - The radius of the circle.
   */
  function addCircle(center: LatLngLiteral, radius: number) {
    circles.value.push({ center, radius })
  }

  /**
   * Adds a GeoJSON string to the store.
   *
   * @param {string} geoJson - The GeoJSON string to add.
   */
  function addGeoJsonString(geoJson: string) {
    geoJsonStrings.value.push(geoJson)
  }

  /**
   * Clears all polygons, circles, and GeoJSON strings from the store.
   */
  function clearAll() {
    polygons.value = []
    circles.value = []
    geoJsonStrings.value = []
  }

  return {
    polygons,
    circles,
    geoJsonStrings,
    addPolygon,
    addCircle,
    addGeoJsonString,
    clearAll,
  }
})
