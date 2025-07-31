import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import InfoCard from '@/components/after-crisis/InfoCard.vue'

describe('InfoCard.vue', () => {
  it('renders title, icon, and description', () => {
    const wrapper = mount(InfoCard, {
      props: {
        icon: '🚨',
        title: 'Test Card',
        description: 'Some description',
      },
      global: {
        stubs: ['router-link'],
      },
    })

    expect(wrapper.text()).toContain('Test Card')
    expect(wrapper.text()).toContain('🚨')
    expect(wrapper.text()).toContain('Some description')
  })

  it('renders a link if props are provided', () => {
    const wrapper = mount(InfoCard, {
      props: {
        icon: '📄',
        title: 'With Link',
        description: 'Has link',
        link: '/go-somewhere',
        linkText: 'Trykk her',
      },
      global: {
        stubs: {
          'router-link': {
            props: ['to'],
            template: '<a :href="to"><slot /></a>',
          },
        },
      },
    })

    const link = wrapper.find('a')
    expect(link.exists()).toBe(true)
    expect(link.attributes('href')).toBe('/go-somewhere')
    expect(link.text()).toBe('Trykk her')
  })
})
