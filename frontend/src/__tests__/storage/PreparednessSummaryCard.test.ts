import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import PreparednessSummaryCard from '@/components/storage/PreparednessSummaryCard.vue'

describe('PreparednessSummaryCard', () => {
  const summary = {
    overallScore: 0.85,
    totalItems: 10,
    adequateItems: 7,
    lowStockItems: 2,
    expiringItems: 1,
  }

  it('renders score and details', () => {
    const wrapper = mount(PreparednessSummaryCard, {
      props: { summary },
    })

    expect(wrapper.text()).toContain('85%')
    expect(wrapper.text()).toContain('Total: 10')
    expect(wrapper.text()).toContain('OK: 7')
    expect(wrapper.text()).toContain('Lav beholdning: 2')
    expect(wrapper.text()).toContain('Utløper snart: 1')
  })

  it('applies correct ring colour based on score', () => {
    const wrapper = mount(PreparednessSummaryCard, {
      props: { summary },
    })

    const svg = wrapper.find('circle.stroke-green-500')
    expect(svg.exists()).toBe(true)
  })
})
