import { mount } from '@vue/test-utils'
import NavBar from '@/components/NavBar.vue'
import { createTestingPinia } from '@pinia/testing'
import { describe, it, expect, vi } from 'vitest'

vi.mock('@/stores/user.ts', () => ({
  useUserStore: () => ({
    isAuthenticated: true,
    fetchCurrentUser: vi.fn(),
    logout: vi.fn(),
  }),
}))

vi.mock('@/stores/household.ts', () => ({
  useHouseholdStore: () => ({
    household: { id: 1 },
    fetchHousehold: vi.fn(),
  }),
}))

vi.mock('@/stores/notification.ts', () => ({
  useNotificationStore: () => ({
    notifications: [],
    fetchNotifications: vi.fn(),
    hasNotifications: true,
  }),
}))

describe('NavBar.vue', () => {
  it('renders and shows logout when authenticated', () => {
    const wrapper = mount(NavBar, {
      global: {
        plugins: [createTestingPinia()],
      },
    })
    expect(wrapper.text()).toContain('Logg ut')
  })
})
