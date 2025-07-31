import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import NotificationPanel from '@/components/storage/NotificationPanel.vue'
import { render } from '@testing-library/vue'
import { createTestingPinia } from '@pinia/testing'

render(NotificationPanel, {
  global: {
    plugins: [createTestingPinia()],
  },
})

describe('NotificationPanel', () => {
  const notifications = [
    { id: 1, type: 'EXPIRATION', itemName: 'Melk' },
    { id: 2, type: 'LOW_STOCK', itemName: 'Batterier' },
  ]

  it('renders message for each notification', () => {
    const wrapper = mount(NotificationPanel, {
      props: { notifications },
    })

    expect(wrapper.text()).toContain('Melk utløper snart')
    expect(wrapper.text()).toContain('Batterier har lav beholdning')
  })

  it('shows fallback message if no notifications', () => {
    const wrapper = mount(NotificationPanel, {
      props: { notifications: [] },
    })

    expect(wrapper.text()).toContain('Ingen varsler 🎉')
  })
})
