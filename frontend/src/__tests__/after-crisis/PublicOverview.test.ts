import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import PublicOverview from '@/components/after-crisis/PublicOverview.vue'

describe('PublicOverview.vue', () => {
  it('renders header and two InfoCards', () => {
    const wrapper = mount(PublicOverview, {
      global: {
        stubs: ['router-link'],
      },
    })

    expect(wrapper.text()).toContain('🚨 Etter Krise')
    expect(wrapper.text()).toContain('Refleksjonsnotat')
    expect(wrapper.text()).toContain('Oppdater Beredskap')

    const cards = wrapper.findAllComponents({ name: 'InfoCard' })
    expect(cards.length).toBe(2)
  })

  it('passes correct props to InfoCards', () => {
    const wrapper = mount(PublicOverview, {
      global: {
        stubs: ['router-link'],
      },
    })

    const cardTitles = wrapper.findAll('h2')
    expect(cardTitles[0].text()).toBe('📄 Refleksjonsnotat')
    expect(cardTitles[1].text()).toBe('🔧 Oppdater Beredskap')
  })
})
