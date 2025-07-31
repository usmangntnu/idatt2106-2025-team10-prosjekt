import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import EmergencyStorageDashboard from '@/components/storage/EmergencyStorageDashboard.vue'

describe('EmergencyStorageDashboard', () => {
  it('renders both summary and notification panels', () => {
    const wrapper = mount(EmergencyStorageDashboard, {
      props: {
        dashboard: {
          preparedness: {
            overallScore: 0.75,
            totalItems: 10,
            adequateItems: 5,
            lowStockItems: 3,
            expiringItems: 2,
          },
          recentNotifications: [],
        },
      },
      global: {
        stubs: ['PreparednessSummaryCard', 'NotificationPanel'],
      },
    })

    expect(
      wrapper.findComponent({ name: 'PreparednessSummaryCard' }).exists()
    ).toBe(true)
    expect(wrapper.findComponent({ name: 'NotificationPanel' }).exists()).toBe(
      true
    )
  })
})
