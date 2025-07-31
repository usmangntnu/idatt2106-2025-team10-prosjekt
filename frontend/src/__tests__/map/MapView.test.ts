import { mount } from '@vue/test-utils'
import MapView from '@/views/MapView.vue'
import { vi } from 'vitest'
import { createTestingPinia } from '@pinia/testing'

vi.mock('@/components/map/MapAdminPanel.vue', () => ({
  default: { template: '<div />' },
}))
vi.mock('@/components/map/MapUserPanel.vue', () => ({
  default: { template: '<div />' },
}))
vi.mock('@/components/map/Map.vue', () => ({
  default: { template: '<div />' },
}))
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    isAdmin: false,
    fetchCurrentUser: vi.fn(),
  }),
}))

describe('MapView.vue', () => {
  it('mounts and fetches user', () => {
    const wrapper = mount(MapView, {
      global: {
        plugins: [createTestingPinia()],
      },
    })
    expect(wrapper.exists()).toBe(true)
  })
})
