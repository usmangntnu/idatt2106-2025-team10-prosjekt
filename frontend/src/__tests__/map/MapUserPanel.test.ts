import { mount } from '@vue/test-utils'
import MapUserPanel from '@/components/map/MapUserPanel.vue'
import { vi } from 'vitest'
import { render } from '@testing-library/vue'

import { createTestingPinia } from '@pinia/testing'

render(MapUserPanel, {
  global: {
    plugins: [createTestingPinia()],
  },
})

vi.mock('@/stores/useLocationStore.ts', () => ({
  useLocationStore: () => ({
    latitude: { value: 10 },
    longitude: { value: 20 },
    getLocation: vi.fn(),
  }),
}))
vi.mock('leaflet-routing-machine', () => ({}))
vi.mock('@/services/mapApi.ts', () => ({
  getAllEventTypes: async () => [{ name: 'Fire' }],
  getAllEvents: async () => [],
  getAllPositions: async () => [],
  getAllPositionTypes: async () => [{ name: 'Station' }],
  getAllShelters: async () => [], // ✅ Add this line
}))

vi.mock('@/utils/socket.ts', () => ({
  createWebSocket: vi.fn(),
}))
vi.mock('@/composables/useMap.ts', () => ({
  useMap: vi.fn(() => ({
    removeAllMapTools: vi.fn(),
    clearAllShapes: vi.fn(),
    drawEventOnMap: vi.fn(),
    drawAllPositionFromPositions: vi.fn(),
    drawUserOnMap: vi.fn(),
    clearUserMarker: vi.fn(),
  })),
}))

vi.mock('@/utils/mapUtils', () => ({
  doSomething: vi.fn(),
  doOtherThing: vi.fn(),
}))

describe('MapUserPanel.vue', () => {
  it('mounts and initializes filters', async () => {
    const wrapper = mount(MapUserPanel)
    await new Promise((resolve) => setTimeout(resolve, 100)) // Wait for async onMounted
    expect(wrapper.exists()).toBe(true)
  })

  it('exposes handleMapReady method', () => {
    const wrapper = mount(MapUserPanel)
    expect(typeof wrapper.vm.handleMapReady).toBe('function')
  })
})
