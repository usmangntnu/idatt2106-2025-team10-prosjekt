import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import BeforeCrisisDashboard from '@/components/before-crisis/BeforeCrisisDashboard.vue'

describe('BeforeDashboard.vue', () => {
  it('renders the title and cards', () => {
    const wrapper = mount(BeforeCrisisDashboard, {
      global: {
        stubs: ['router-link'],
      },
    })

    expect(wrapper.text()).toContain('🛡️ Før Krise')
    expect(wrapper.text()).toContain('Quiz')
    expect(wrapper.text()).toContain('Tilfluktskart')

    const cards = wrapper.findAll('.rounded-xl')
    expect(cards.length).toBeGreaterThanOrEqual(2)
  })

  it('renders router-link targets correctly', () => {
    const wrapper = mount(BeforeCrisisDashboard, {
      global: {
        stubs: {
          'router-link': {
            props: ['to'],
            template: '<a :href="to"><slot /></a>',
          },
        },
      },
    })

    const links = wrapper.findAll('a')
    expect(links[0].attributes('href')).toBe('/quiz')
    expect(links[1].attributes('href')).toBe('/map')
  })
})
