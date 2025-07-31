import { mount } from '@vue/test-utils'
import MapAdminPanel from '@/components/map/MapAdminPanel.vue'
import { createTestingPinia } from '@pinia/testing'

import { vi } from 'vitest'

// Mock the entire mapApi module before importing the component
vi.mock('@/services/mapApi', () => ({
  getAllEventTypes: vi.fn(() => Promise.resolve([])),
  getAllEvents: vi.fn(() => Promise.resolve([])),
  getAllPositions: vi.fn(() => Promise.resolve([])),
  getAllPositionTypes: vi.fn(() => Promise.resolve([])),
  getEventById: vi.fn(),
  getPositionById: vi.fn(),
}))
vi.mock('@/utils/mapUtils', () => ({
  doSomething: vi.fn(),
  doOtherThing: vi.fn(),
}))

describe('MapAdminPanel.vue', () => {
  it('mounts without crashing', () => {
    const wrapper = mount(MapAdminPanel, {
      global: {
        plugins: [createTestingPinia()],
      },
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('exposes handleMapReady method', () => {
    const wrapper = mount(MapAdminPanel, {
      global: {
        plugins: [createTestingPinia()],
      },
    })
    expect(typeof wrapper.vm.handleMapReady).toBe('function')
  })
})
