import { useMapStore, useDrawStore } from '@/stores/mapStore.ts'
import type { Map as LeafletMap, LatLngLiteral, LatLng } from 'leaflet'
import L from 'leaflet'
import 'leaflet.pm'
import * as turf from '@turf/turf'
import type { EventResponse, PositionResponse } from '@/types/types.ts'
import {
  getIconUrlForPosType,
  getIconUrlForEventType,
  formatEventPopup,
  formatPositionPopup,
} from '@/utils/mapUtils.ts'
import { useLocationStore } from '@/stores/useLocationStore.ts'
import { watch } from 'vue'

export function useMap(map: LeafletMap) {
  const mapStore = useMapStore()
  const drawStore = useDrawStore()

  const locationStore = useLocationStore()

  let drawnPolygons: L.Polygon[] = []
  let drawnCircles: L.Circle[] = []
  let drawnEventMarkers: L.Marker[] = []
  let drawnPositionMarkers: L.Marker[] = []
  let userMarkerStore: L.Marker | null = null
  let householdMarkerStore: L.Marker | null = null

  let routingControl: L.Routing.Control | null = null

  function showRoute(pos: PositionResponse) {
    locationStore.getLocation()

    const lat = locationStore.latitude.value
    const lng = locationStore.longitude.value

    const makeRoute = () => {
      if (routingControl) {
        map.removeControl(routingControl)
      }
      routingControl = L.Routing.control({
        waypoints: [
          L.latLng(lat!, lng!),
          L.latLng(pos.latitude, pos.longitude),
        ],
        router: L.Routing.osrmv1({
          serviceUrl: 'https://router.project-osrm.org/route/v1',
        }),
        showAlternatives: false,
        lineOptions: {
          styles: [
            {
              className: 'route-line-tailwind animate-shake',
            },
          ],
        } as L.Routing.LineOptions,
      }).addTo(map)
    }

    if (lat != null && lng != null) {
      makeRoute()
    } else {
      const unwatch = watch(
        () => [locationStore.latitude.value, locationStore.longitude.value],
        ([newLat, newLng]) => {
          if (newLat != null && newLng != null) {
            makeRoute()
            unwatch()
          }
        }
      )
    }
  }

  function clearAllShapes() {
    console.log('Clearing all shapes from map')
    drawnPolygons.forEach((p) => {
      if (map.hasLayer(p)) map.removeLayer(p)
    })
    drawnPolygons = []

    drawnCircles.forEach((c) => {
      if (map.hasLayer(c)) map.removeLayer(c)
    })
    drawnCircles = []

    if (drawnPositionMarkers.length > 0) {
      drawnPositionMarkers.forEach((m) => {
        if (map.hasLayer(m)) map.removeLayer(m)
      })
      drawnPositionMarkers = []
    }
    drawnEventMarkers.forEach((m) => {
      if (map.hasLayer(m)) map.removeLayer(m)
    })

    drawnEventMarkers = []
    drawnPositionMarkers = []
    drawStore.clearAll()
    mapStore.clearClickedPosition()
  }

  function activateDrawTools() {
    map.pm.addControls({
      position: 'topleft',
      drawCircle: true,
      drawPolygon: true,
      drawMarker: false,
      drawPolyline: false,
      drawRectangle: false,
      drawCircleMarker: false,
      editMode: false,
      dragMode: false,
      cutPolygon: false,
      removalMode: true,
    })

    map.on('pm:drawstart', (e: any) => {
      const shape = e.shape
      if (drawnPositionMarkers.length > 0) {
        drawnPositionMarkers.forEach((m) => {
          if (map.hasLayer(m)) map.removeLayer(m)
        })
        drawnPositionMarkers = []
      }
      if (shape === 'Marker') {
        clearAllShapes()
      }
      if (shape === 'Polygon') {
        clearAllShapes()
      }
      if (shape === 'Circle') {
        clearAllShapes()
      }
    })

    map.on('pm:create', (e: any) => {
      const { layer, shape } = e

      if (shape === 'Polygon') {
        drawnPolygons.push(layer as L.Polygon)
        layer.addTo(map)

        const latlngs = layer.getLatLngs()[0] as LatLng[]
        const coords: LatLngLiteral[] = latlngs.map((p) => ({
          lat: p.lat,
          lng: p.lng,
        }))

        drawStore.addGeoJsonString(JSON.stringify(layer.toGeoJSON()))
        drawStore.addPolygon(coords)
      } else if (shape === 'Circle') {
        const circleLayer = layer as L.Circle
        drawnCircles.push(circleLayer)
        layer.addTo(map)

        const center = layer.getLatLng()
        const radius = layer.getRadius()

        const turfCircle = turf.circle([center.lng, center.lat], radius, {
          steps: 512,
          units: 'meters',
        })

        const geoJsonString = JSON.stringify(turfCircle)

        drawStore.addGeoJsonString(geoJsonString)
        drawStore.addCircle({ lat: center.lat, lng: center.lng }, radius)
      } else if (shape === 'Marker') {
        clearAllShapes()

        const drawn = layer as L.Marker
        const pos = drawn.getLatLng()

        const marker = L.marker(pos, {
          icon: L.icon({
            iconUrl:
              'https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon.png',
            iconRetinaUrl:
              'https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon-2x.png',
            shadowUrl:
              'https://unpkg.com/leaflet@1.9.3/dist/images/marker-shadow.png',
            iconSize: [40, 51],
            iconAnchor: [20, 50],
          }),
        })

        map.removeLayer(e.layer)
        map.addLayer(marker)
        drawnPositionMarkers.push(marker)
        const geoJsonStringMarker = JSON.stringify(marker.toGeoJSON())
        mapStore.setClickedPosition({ lat: pos.lat, lng: pos.lng })
        mapStore.setClickedPositionGeoJson(geoJsonStringMarker)
        map.pm.disableDraw()
      }
    })
  }

  function switchDrawTool(type: 'Event' | 'Position') {
    console.log('Switching draw tool to:', type)
    map.pm.disableDraw()
    map.pm.removeControls()

    const opts: Record<string, boolean> = {
      drawPolygon: false,
      drawCircle: false,
      drawMarker: false,
      drawPolyline: false,
      drawRectangle: false,
      drawCircleMarker: false,
      editMode: false,
      dragMode: false,
      cutPolygon: false,
      removalMode: true,
    }
    if (type === 'Event') {
      console.log('Switching to Event draw tool')
      opts.drawPolygon = true
      opts.drawCircle = true
      opts.drawMarker = true
    } else {
      console.log('Switching to Position draw tool')
      opts.drawMarker = true
    }
    map.pm.addControls({ position: 'topleft', ...opts })
  }

  function drawGeoJsonGeometry(geoJsonString: string) {
    const geoJson = JSON.parse(geoJsonString)
    clearAllShapes()

    if (geoJson.type === 'Polygon') {
      const coords = geoJson.coordinates[0].map(
        ([lng, lat]: [number, number]) => ({ lat, lng })
      )
      const polygon = L.polygon(coords)

      drawnPolygons.push(polygon)
      drawStore.addPolygon(coords)
      polygon.addTo(map)
      return
    }

    if (geoJson.type === 'Point') {
      const [lng, lat] = geoJson.coordinates
      const marker = L.marker({ lat, lng })
      drawnPositionMarkers.push(marker)
      const el = marker.getElement()
      if (el) {
        el.classList.add('pulse')

        marker.on('click', () => {
          el.classList.add('click-effect')
          setTimeout(() => el.classList.remove('click-effect'), 300)
        })
      }
      marker.addTo(map)
      mapStore.setClickedPosition({ lat, lng })
      return
    }

    console.warn('Unknown GeoJSON type:', geoJson.type)
  }

  const severityColors: Record<string, { stroke: string; fill: string }> = {
    LOW: { stroke: 'green', fill: 'limegreen' },
    MEDIUM: { stroke: 'yellow', fill: 'gold' },
    HIGH: { stroke: 'orange', fill: 'orange' },
    CRITICAL: { stroke: 'darkred', fill: 'red' },
  }

  function drawCircleFromCenter(
    evt: EventResponse,
    center: LatLngLiteral,
    radius: number,
    showPopUp = true
  ) {
    const { stroke, fill } = severityColors[evt.severity] ?? {
      stroke: 'gray',
      fill: 'lightgray',
    }

    const useGradient = true

    const circle = L.circle(center, {
      radius,
      color: stroke,
      fillColor: fill,
      fillOpacity: 0.42,
      weight: 9,
      dashArray: '5',
      className: 'interactive-circle',
    })

    if (showPopUp) {
      circle
        .addTo(map)
        .bindPopup(formatEventPopup(evt), { maxWidth: 300 })
        .openPopup()
    } else {
      circle.addTo(map).bindPopup(formatEventPopup(evt), { maxWidth: 300 })
    }

    drawnCircles.push(circle)
    drawStore.addCircle(center, radius)

    const circleEl = (circle as any)._path as SVGElement
    circleEl.classList.add('pulsating-circle')

    if (useGradient) {
      circleEl.classList.add('gradient-circle')
    }

    circle
      .on('mouseover', function () {
        circleEl.classList.add('hover-effect')
      })
      .on('mouseout', function () {
        circleEl.classList.remove('hover-effect')
      })
      .on('click', function () {
        circleEl.classList.add('click-effect')
        setTimeout(() => {
          circleEl.classList.remove('click-effect')
        }, 700)
      })

    return circle
  }

  function drawPolygonFromCoords(
    evt: EventResponse,
    coords: LatLngLiteral[],
    showPopUp = true
  ) {
    const { stroke, fill } = severityColors[evt.severity] ?? {
      stroke: 'gray',
      fill: 'lightgray',
    }

    const useGradient = true

    const poly = L.polygon(coords, {
      color: stroke,
      fillColor: fill,
      fillOpacity: 0.4,
      weight: 1,
      className: 'interactive-polygon',
    })

    if (showPopUp) {
      poly
        .addTo(map)
        .bindPopup(formatEventPopup(evt), { maxWidth: 300 })
        .openPopup()
    } else {
      poly.addTo(map).bindPopup(formatEventPopup(evt), { maxWidth: 300 })
    }

    drawnPolygons.push(poly)
    drawStore.addPolygon(coords)

    const polyEl = (poly as any)._path as SVGElement

    if (useGradient) {
      polyEl.classList.add('gradient-polygon')
    }
    poly
      .on('mouseover', function () {
        polyEl.classList.add('poly-hover-effect')
      })
      .on('mouseout', function () {
        polyEl.classList.remove('poly-hover-effect')
      })
      .on('click', function () {
        polyEl.classList.add('poly-click-effect')
        setTimeout(() => {
          polyEl.classList.remove('poly-click-effect')
        }, 700)
      })

    return poly
  }

  function drawPosition(position: PositionResponse, showPopUp = true) {
    clearAllShapes()

    const pos = { lat: position.latitude, lng: position.longitude }
    const iconUrl = getIconUrlForPosType(position.type.name)

    const marker = L.marker(pos, {
      icon: L.icon({
        iconUrl,
        iconRetinaUrl: iconUrl,
        iconSize: [40, 51],
        iconAnchor: [20, 50],
      }),
    })

    if (showPopUp) {
      marker
        .addTo(map)
        .bindPopup(formatPositionPopup(position), { maxWidth: 300 })
        .openPopup()
    } else {
      const el = marker.getElement()
      if (el) {
        el.classList.add('pulse')

        marker.on('click', () => {
          el.classList.add('click-effect')
          setTimeout(() => el.classList.remove('click-effect'), 300)
        })
      }
      marker.on('popupopen', () => {
        const btn = document.getElementById(
          `route-btn-${position.id}`
        ) as HTMLButtonElement
        if (!btn) return

        btn.textContent = routingControl ? 'Skjul rute' : 'Vis rute'
        btn.onclick = () => {
          if (routingControl) {
            map.removeControl(routingControl)
            routingControl = null
            btn.textContent = 'Vis rute'
          } else {
            showRoute(position)
            btn.textContent = 'Skjul rute'
          }
        }
      })

      marker
        .addTo(map)
        .bindPopup(formatPositionPopup(position), { maxWidth: 300 })
    }
    drawnPositionMarkers.push(marker)
    mapStore.setClickedPositionGeoJson(JSON.stringify(marker.toGeoJSON()))
    mapStore.setClickedPosition({
      lat: position.latitude,
      lng: position.longitude,
    })
  }

  function drawEventOnMap(evt: EventResponse, showPopUp = true) {
    console.log('Drawing event on map:', evt)
    if (evt.circleData) {
      const circle = drawCircleFromCenter(
        evt,
        { lat: evt.circleData.latitude, lng: evt.circleData.longitude },
        evt.circleData.radius,
        showPopUp
      )
      drawnCircles.push(circle)
      console.log('Drawing Circle:', circle)
      return
    }

    try {
      const geoJson = JSON.parse(evt.geometryGeoJson)

      if (geoJson.type === 'Feature') {
        const { type, coordinates } = geoJson.geometry
        if (type === 'Polygon') {
          const coords = coordinates[0].map(([lng, lat]: [number, number]) => ({
            lat,
            lng,
          }))
          const poly = drawPolygonFromCoords(evt, coords, showPopUp)
          drawnPolygons.push(poly)
          return
        }
        if (type === 'Point') {
          const [lng, lat] = coordinates
          const iconUrl = getIconUrlForEventType(evt.eventType.name)

          const pulsatingIcon = L.divIcon({
            className: 'pulsating-marker',
            iconSize: [20, 20],
            iconAnchor: [10, 10],
            popupAnchor: [0, -10],
          })
          const marker = L.marker(
            { lat, lng },
            {
              icon: pulsatingIcon,
            }
          )
          drawnEventMarkers.push(marker)
          if (showPopUp) {
            marker
              .addTo(map)
              .bindPopup(formatEventPopup(evt), { maxWidth: 300 })
              .openPopup()
          } else {
            const el = marker.getElement()
            if (el) {
              el.classList.add('pulse')

              marker.on('click', () => {
                el.classList.add('click-effect')
                setTimeout(() => el.classList.remove('click-effect'), 300)
              })
            }
            console.log('Drawing Point:', geoJson.coordinates)
            marker
              .addTo(map)
              .bindPopup(formatEventPopup(evt), { maxWidth: 300 })
          }

          drawnEventMarkers.push(marker)
          mapStore.setClickedPosition({ lat, lng })
          return
        }
      } else if (geoJson.type === 'Polygon') {
        const coords = geoJson.coordinates[0].map(
          ([lng, lat]: [number, number]) => ({ lat, lng })
        )
        const poly = drawPolygonFromCoords(evt, coords, showPopUp)
        drawnPolygons.push(poly)
        return
      } else if (geoJson.type === 'Point') {
        const [lng, lat] = geoJson.coordinates
        const iconUrl = getIconUrlForEventType(evt.eventType.name)

        const pulsingIcon = L.divIcon({
          html: `
    <div class="pulsing-marker">
      <div class="ring ring1"></div>
      <div class="ring ring2"></div>
      <div class="dot"></div>
    </div>`,
          className: '', // ingen ekstra wrapper-klasser
          iconSize: [40, 40], // samsvar med CSS-container
          iconAnchor: [20, 20], // sentrer ikonet på lat/lng
          popupAnchor: [0, -20], // juster popup-pilen
        })

        // så i koden din:
        const marker = L.marker([lat, lng], {
          icon: pulsingIcon,
        })
        drawnEventMarkers.push(marker)
        if (showPopUp) {
          marker
            .addTo(map)
            .bindPopup(formatEventPopup(evt), { maxWidth: 300 })
            .openPopup()
        } else {
          console.log('Drawing Point:', geoJson.coordinates)
          const el = marker.getElement()
          if (el) {
            el.classList.add('pulse')

            marker.on('click', () => {
              el.classList.add('click-effect')
              setTimeout(() => el.classList.remove('click-effect'), 300)
            })
          }
          marker.addTo(map).bindPopup(formatEventPopup(evt), { maxWidth: 300 })
        }

        drawnEventMarkers.push(marker)
        mapStore.setClickedPosition({ lat, lng })
        return
      }

      console.warn('Unknown geometry type in event', evt.id, geoJson.type)
    } catch (e) {
      console.error('Invalid GeoJSON for event:', evt, e)
    }
  }

  function clearAllPositionsFromMap() {
    if (drawnPositionMarkers.length > 0) {
      drawnPositionMarkers.forEach((m) => {
        if (map.hasLayer(m)) map.removeLayer(m)
      })
      drawnPositionMarkers = []
    }
  }

  function clearAllEventsFromMap() {
    drawnPolygons.forEach((p) => {
      if (map.hasLayer(p)) map.removeLayer(p)
    })
    drawnPolygons = []

    drawnCircles.forEach((c) => {
      if (map.hasLayer(c)) map.removeLayer(c)
    })
    drawnCircles = []

    drawnEventMarkers.forEach((m) => {
      if (map.hasLayer(m)) map.removeLayer(m)
    })
    drawnEventMarkers = []
  }

  function clearEventFromMap(event: EventResponse) {
    const geoJson = JSON.parse(event.geometryGeoJson)

    console.log('Clearing event from map:', event.id, geoJson)
    if (event.circleData) {
      console.log('Clearing Circle:', event.circleData)
      const circle = drawnCircles.find((c) => {
        const center = c.getLatLng()
        return (
            event.circleData &&
            center.lat === event.circleData.latitude &&
            center.lng === event.circleData.longitude
        )
      })
      if (circle) {
        map.removeLayer(circle)
        drawnCircles = drawnCircles.filter((c) => c !== circle)
      }
    }


    if (geoJson.type === 'Feature') {
      console.log('GeoJSON Feature:', geoJson)
      const { type, coordinates } = geoJson.geometry
      if (type === 'Polygon') {
        const coords = coordinates[0].map(([lng, lat]: [number, number]) => ({
          lat,
          lng,
        }))
        const poly = drawnPolygons.find((p) => {
          const latlngs = p.getLatLngs()[0] as LatLng[]
          return (
            Array.isArray(latlngs) &&
            latlngs.every((latlng: LatLng) =>
              coords.some(
                (c: LatLngLiteral) =>
                  c.lat === latlng.lat && c.lng === latlng.lng
              )
            )
          )
        })
        if (poly) {
          map.removeLayer(poly)
          drawnPolygons = drawnPolygons.filter((p) => p !== poly)
        }
      } else if (type === 'Point') {
        const [lng, lat] = coordinates
        const marker = drawnEventMarkers.find(
          (m) => m.getLatLng().lat === lat && m.getLatLng().lng === lng
        )
        if (marker) {
          map.removeLayer(marker)
          drawnEventMarkers.splice(drawnEventMarkers.indexOf(marker), 1)
        }
      }
    }
  }

  function drawAllPositionFromPositions(
    positions: PositionResponse[],
    clearAll = true
  ) {
    if (clearAll) {
      clearAllShapes()
    }

    console.log('Drawing all positions:', positions)
    positions.forEach((position) => {
      console.log('Drawing Position:', position)
      const pos = { lat: position.latitude, lng: position.longitude }
      const iconUrl = getIconUrlForPosType(position.type.name)

      const marker = L.marker(pos, {
        icon: L.icon({
          iconUrl,
          iconRetinaUrl: iconUrl,
          shadowUrl:
            'https://unpkg.com/leaflet@1.9.3/dist/images/marker-shadow.png',
          iconSize: [40, 51],
          iconAnchor: [20, 50],
          className: 'pulse-marker',
        }),
      })

      const el = marker.getElement()
      if (el) {
        el.classList.add('pulse')

        marker.on('click', () => {
          el.classList.add('click-effect')
          setTimeout(() => el.classList.remove('click-effect'), 300)
        })
      }
      marker.on('popupopen', () => {
        const btn = document.getElementById(
          `route-btn-${position.id}`
        ) as HTMLButtonElement
        if (!btn) return
        btn.textContent = routingControl ? 'Skjul rute' : 'Vis rute'
        btn.onclick = () => {
          if (routingControl) {
            map.removeControl(routingControl)
            routingControl = null
            btn.textContent = 'Vis rute'
          } else {
            showRoute(position)
            btn.textContent = 'Skjul rute'
          }
        }
      })

      drawnPositionMarkers.push(marker)
      marker
        .addTo(map)
        .bindPopup(formatPositionPopup(position), { maxWidth: 300 })

      mapStore.setClickedPositionGeoJson(JSON.stringify(marker.toGeoJSON()))
      mapStore.setClickedPosition({
        lat: position.latitude,
        lng: position.longitude,
      })
    })
  }

  function drawAllEventsFromEvents(events: EventResponse[], clearAll = true) {
    if (clearAll) {
      clearAllShapes()
    }

    events.forEach((evt) => {
      if (evt.circleData) {
        const circle = drawCircleFromCenter(
          evt,
          { lat: evt.circleData.latitude, lng: evt.circleData.longitude },
          evt.circleData.radius
        )
        map.closePopup()
        drawnCircles.push(circle)
        drawStore.clearAll()
        return
      }

      try {
        const geoJson = JSON.parse(evt.geometryGeoJson)

        if (geoJson.type === 'Feature') {
          const { type, coordinates } = geoJson.geometry
          if (type === 'Polygon') {
            const coords = coordinates[0].map(
              ([lng, lat]: [number, number]) => ({ lat, lng })
            )
            const poly = drawPolygonFromCoords(evt, coords)
            drawnPolygons.push(poly)
            return
          }
        } else if (geoJson.type === 'Polygon') {
          const coords = geoJson.coordinates[0].map(
            ([lng, lat]: [number, number]) => ({ lat, lng })
          )
          const poly = drawPolygonFromCoords(evt, coords)
          drawnPolygons.push(poly)
          drawStore.clearAll()
          return
        } else if (geoJson.type === 'Point') {
          const [lng, lat] = geoJson.coordinates
          const iconUrl = getIconUrlForEventType(evt.eventType.name)
          const marker = L.marker(
            { lat, lng },
            {
              icon: L.icon({
                iconUrl,
                iconRetinaUrl: iconUrl,
                shadowUrl:
                  'https://unpkg.com/leaflet@1.9.3/dist/images/marker-shadow.png',
                iconSize: [40, 51],
                iconAnchor: [20, 50],
                className: 'pulse-marker',
              }),
            }
          )
          drawnEventMarkers.push(marker)
          const el = marker.getElement()
          if (el) {
            el.classList.add('pulse')
            marker.on('click', () => {
              el.classList.add('click-effect')
              setTimeout(() => el.classList.remove('click-effect'), 300)
            })
          }
          marker.addTo(map).bindPopup(formatEventPopup(evt), { maxWidth: 300 })
          drawnEventMarkers.push(marker)
          mapStore.clearClickedPosition()
          return
        }
        console.warn('Unknown geometry type in event', evt.id, geoJson.type)
      } catch (e) {
        console.error('Invalid GeoJSON for event:', evt, e)
      }
    })
  }

  function beginDrawPolygon() {
    map.pm.enableDraw('Polygon', {
      snappable: true,
      snapDistance: 20,
      finishOn: 'dblclick',
    })
  }
  function beginDrawCircle() {
    map.pm.enableDraw('Circle', {
      snappable: true,
      snapDistance: 20,
      finishOn: 'dblclick',
    })
  }
  function beginDrawMarker() {
    map.pm.enableDraw('Marker', {
      snappable: true,
      snapDistance: 20,
      finishOn: 'dblclick',
    })
  }
  function stopDraw() {
    map.pm.disableDraw()
    map.pm.removeControls()
  }

  function startEventDraw() {
    switchDrawTool('Event')
  }
  function startPositionDraw() {
    switchDrawTool('Position')
  }

  function disableDraw() {
    map.pm.disableDraw()
    map.pm.removeControls()
    clearAllShapes()
  }

  function drawUserOnMap(lat: number, lng: number) {
    if (userMarkerStore) {
      map.removeLayer(userMarkerStore)
    }
    const userMarker = L.marker([lat, lng], {
      icon: L.icon({
        iconUrl: '/icons/map/user-location.svg',
        iconSize: [65, 65],
        iconAnchor: [20, 50],
      }),
    })
    console.log('User marker:', userMarker)
    userMarkerStore = userMarker
    userMarker.addTo(map)
  }

  function clearUserMarker() {
    if (userMarkerStore) {
      map.removeLayer(userMarkerStore)
      userMarkerStore = null
    }
  }

  function drawHouseholdOnMap(lat: number, lng: number) {
    if (householdMarkerStore) {
      map.removeLayer(householdMarkerStore)
    }
    const householdMarker = L.marker([lat, lng], {
      icon: L.icon({
        iconUrl: '/icons/map/house-svgrepo-com.svg', // Change later
        iconSize: [65, 65],
        iconAnchor: [20, 50],
      }),
    })
    console.log('Household marker: ', householdMarker)
    householdMarkerStore = householdMarker
    householdMarker.addTo(map)
  }

  function clearHouseholdMarker() {
    if (householdMarkerStore) {
      map.removeLayer(householdMarkerStore)
      householdMarkerStore = null
    }
  }


  function removeAllMapTools() {
    map.pm.removeControls()
    map.pm.disableDraw()
  }

  return {
    activateDrawTools,
    beginDrawPolygon,
    beginDrawCircle,
    beginDrawMarker,
    clearAllPositionsFromMap,
    clearAllShapes,
    clearEventFromMap,
    clearAllEventsFromMap,
    clearUserMarker,
    clearHouseholdMarker,
    disableDraw,
    drawAllPositionFromPositions,
    drawCircleFromCenter,
    drawEventOnMap,
    drawGeoJsonGeometry,
    drawAllEventsFromEvents,
    drawPolygonFromCoords,
    drawMarker: drawPosition,
    drawUserOnMap,
    drawHouseholdOnMap,
    removeAllMapTools,
    stopDraw,
    startEventDraw,
    startPositionDraw,
    switchDrawTool,
  }
}
