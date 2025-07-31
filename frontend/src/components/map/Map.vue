<template>
  <div id="map" class="map"></div>
</template>

<script setup lang="ts">
import { defineEmits, toRaw } from 'vue'
import { onMounted, ref } from 'vue'
import type { Map as LeafletMap } from 'leaflet'
import * as L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet.pm/dist/leaflet.pm.css'
import 'leaflet/dist/leaflet.css'

const emit = defineEmits<{
  (e: 'mapReady', map: LeafletMap): void
}>()
const map = ref<LeafletMap | null>(null)

/**
 * Initializes the map, layers, geolocation, and popup functionality after component is mounted.
 */
onMounted(async () => {
  if (map.value) {
    map.value.remove()
    map.value = null
  }

  /**
   * Add maxbounds to not get away from Norway
   */
  const leafletMap = L.map('map', {
    center: [62, 8],
    zoom: 7,
    minZoom: 4,
    maxZoom: 20,
    maxBounds: [
      [-90, -180],
      [90, 180],
    ],
    maxBoundsViscosity: 1.0,
    keyboard: false,
    zoomControl: false,
  })
  emit('mapReady', toRaw(leafletMap))

  L.tileLayer(
    `https://api.mapbox.com/styles/v1/mapbox/streets-v12/tiles/{z}/{x}/{y}?access_token=${import.meta.env.VITE_MAPBOX_TOKEN}`,
    {
      tileSize: 512,
      zoomOffset: -1,
      detectRetina: true,
      attribution: '© Mapbox © OpenStreetMap',
    }
  ).addTo(leafletMap)
})
</script>

<style scoped>
.map {
  width: 100%;
  height: 100%;
}
</style>
