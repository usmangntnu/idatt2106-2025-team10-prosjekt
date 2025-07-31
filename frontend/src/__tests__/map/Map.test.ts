import { mount } from '@vue/test-utils'
import Map from '@/components/map/Map.vue'
import { vi } from 'vitest'

vi.mock('leaflet', () => {
  return {
    map: vi.fn(() => ({
      remove: vi.fn(),
      on: vi.fn(),
      addLayer: vi.fn(),
      setView: vi.fn(),
    })),
    tileLayer: vi.fn(() => ({ addTo: vi.fn() })),
  }
})

describe('Map.vue', () => {
  it('emits mapReady with Leaflet map instance', async () => {
    const wrapper = mount(Map)
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted()).toHaveProperty('mapReady')
  })
})
